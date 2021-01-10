package com.example.photoeditor;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.photoeditor.Utils.BitmapUtils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.IOException;
import java.util.List;

import static com.example.photoeditor.MainActivity.CAMERA_REQUEST;
import static com.example.photoeditor.MainActivity.PERMISSION_PICK_IMAGE;

public class StartActivity extends AppCompatActivity {

    CardView btnAddImage, btnOpenCamera;
    Uri image_taken_by_camera_uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        setContentView(R.layout.activity_start);

        ImageView iw= (ImageView)findViewById(R.id.app_logo);
        Bitmap applogoBitmap = BitmapUtils.getBitmapFromAssets(this, "app_logo.png", 240, 240);
        iw.setImageBitmap(applogoBitmap);

        btnAddImage = (CardView) findViewById(R.id.btn_open_image);
        btnOpenCamera = (CardView) findViewById(R.id.btn_open_camera);

        btnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageFromGallery();
            }
        });

        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PERMISSION_PICK_IMAGE)
            {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.setData(data.getData());
                startActivity(intent);
            }
            else if (requestCode == CAMERA_REQUEST){
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                intent.setData(image_taken_by_camera_uri);
                startActivity(intent);
            }
        }
    }

    private void openCamera() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted())
                        {
                            ContentValues values = new ContentValues();
                            values.put(MediaStore.Images.Media.TITLE, "New picture");
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");

                            image_taken_by_camera_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_taken_by_camera_uri);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                        else
                        {
                            Toast.makeText(StartActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void openImageFromGallery() {
        Dexter.withActivity(this)
            .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .withListener(new MultiplePermissionsListener() {
                @Override
                public void onPermissionsChecked(MultiplePermissionsReport report) {
                    if (report.areAllPermissionsGranted())
                    {
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setType("image/*");
                        startActivityForResult(intent, PERMISSION_PICK_IMAGE);
                    }
                    else
                    {
                        Toast.makeText(StartActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                    token.continuePermissionRequest();
                }
            })
            .check();
    }
}