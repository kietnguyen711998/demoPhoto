package com.example.photoeditor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.Toast;

import com.example.photoeditor.Adapter.ViewPagerAdapter;
import com.example.photoeditor.Interface.AddTextFragmentListener;
import com.example.photoeditor.Interface.BrushFragmentListener;
import com.example.photoeditor.Interface.EditImageFragmentListener;
import com.example.photoeditor.Interface.EmojiFragmentListener;
import com.example.photoeditor.Interface.FiltersListFragmentListener;
import com.example.photoeditor.Utils.BitmapUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.yalantis.ucrop.UCrop;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MainActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener, BrushFragmentListener, AddTextFragmentListener, EmojiFragmentListener {

    public static final String pictureName = "IU.jpg";
    public static final int PERMISSION_PICK_IMAGE = 1000;
    public static final int PERMISSION_INSERT_IMAGE = 1001;
    private static final int CAMERA_REQUEST = 1002;

    PhotoEditor photoEditor;
    PhotoEditorView photoEditorView;

    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    CardView btn_filters_list, btn_edit,btn_brush, btn_emoji, btn_text,btn_add_image, btn_crop;
    LinearLayout edit_side;

    int brightnessfinal = 0;
    float saturationfinal = 1.0f;
    float constraintfinal = 1.0f;

    boolean onEdit = false;

    Uri image_selected_uri;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setTitle("PhotoEditor");
        getSupportActionBar().setIcon(R.drawable.ic_app_logo);

        photoEditorView = (PhotoEditorView) findViewById(R.id.image_preview);
        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(getAssets(),"emojione-android.ttf"))
                .build();

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);

        edit_side = (LinearLayout)findViewById(R.id.edit_side);
        edit_side.setVisibility(View.INVISIBLE);

        btn_filters_list = (CardView)findViewById(R.id.btn_filter_list);
        btn_edit = (CardView)findViewById(R.id.btn_edit);
        btn_brush = (CardView)findViewById(R.id.btn_brush);
        btn_text = (CardView)findViewById(R.id.btn_text);
        btn_emoji = (CardView)findViewById(R.id.btn_emoji);
        btn_crop = (CardView)findViewById(R.id.btn_crop);
        btn_add_image = (CardView)findViewById(R.id.btn_add_image);


        btn_filters_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filtersListFragment != null)
                {
                    filtersListFragment.show(getSupportFragmentManager(), filtersListFragment.getTag());
                }
                else {
                    FiltersListFragment filtersListFragment = FiltersListFragment.getInstance(null);
                    filtersListFragment.setListener(MainActivity.this);
                    filtersListFragment.show(getSupportFragmentManager(), filtersListFragment.getTag());
                }
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditImageFragment editImageFragment = EditImageFragment.getInstance();
                editImageFragment.setListener(MainActivity.this);
                editImageFragment.show(getSupportFragmentManager(), editImageFragment.getTag());
            }
        });

        btn_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEditor.setBrushDrawingMode(true);

                BrushFragment brushFragment = BrushFragment.getInstance();
                brushFragment.setListener(MainActivity.this);
                brushFragment.show(getSupportFragmentManager(),brushFragment.getTag());
            }
        });

        btn_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddTextFragment addTextFragment = AddTextFragment.getInstance();
                addTextFragment.setListener(MainActivity.this);
                addTextFragment.show(getSupportFragmentManager(),addTextFragment.getTag());
            }
        });

        btn_emoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EmojiFragment emojiFragment = EmojiFragment.getInstance();
                emojiFragment.setListener(MainActivity.this);
                emojiFragment.show(getSupportFragmentManager(),emojiFragment.getTag());
            }
        });

        btn_crop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCrop(image_selected_uri);
            }
        });

        btn_add_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addImageToPicture();
            }
        });

        loadImage();
    }

    private void addImageToPicture() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted()){
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent,PERMISSION_INSERT_IMAGE);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                    }
                }).check();
    }

    private void startCrop(Uri uri) {
        String destinationFileName = new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();

        UCrop ucrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destinationFileName)));

        ucrop.start(MainActivity.this);
    }

    private void loadImage() {
        originalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName, 100, 100);
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(originalBitmap);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, "FILTERS");
        adapter.addFragment(editImageFragment, "EDIT");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessfinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(filteredBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationfinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(filteredBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onConstraintChanged(float constraint) {
        constraintfinal = constraint;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(constraint));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(filteredBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessfinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationfinal));
        myFilter.addSubFilter(new ContrastSubFilter(constraintfinal));

        finalBitmap = myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        //resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void resetControl() {
        if (editImageFragment == null)
            editImageFragment.resetControls();

        brightnessfinal = 0;
        saturationfinal = 1.0f;
        constraintfinal = 1.0f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.action_open)
        {
            openImageFromGallery();
            return true;
        }
        else if(id == R.id.action_save && onEdit)
        {
            saveImageToGallery();
            return true;
        }
        else if(id == R.id.action_camera)
        {
            openCamera();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
                            image_selected_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                            Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_selected_uri);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .check();
    }

    private void saveImageToGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted())
                        {
                            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {
                                    try {

                                        photoEditorView.getSource().setImageBitmap(saveBitmap);

                                        final String path = BitmapUtils.insertImage(getContentResolver(), finalBitmap, System.currentTimeMillis()+"_profile.jpg", null);

                                        if(!TextUtils.isEmpty(path))
                                        {
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                                                    .setAction("OPEN", new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            openImage(path);
                                                        }
                                                    });
                                            snackbar.show();
                                        }
                                        else
                                        {
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout, "Image saved to gallery!", Snackbar.LENGTH_LONG);

                                            snackbar.show();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
        .check();
    }

    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);
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
                            Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
        .check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            edit_side.setVisibility(View.VISIBLE);
            onEdit = true;
            if (requestCode == PERMISSION_PICK_IMAGE) {
                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800, 800);

                image_selected_uri = data.getData();

                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                filtersListFragment = FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);
            }
            else if(requestCode == PERMISSION_INSERT_IMAGE)
            {
                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this,data.getData(), 250, 250 );
                photoEditor.addImage(bitmap);
            }
            if (requestCode == CAMERA_REQUEST) {
                Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, image_selected_uri, 800, 800);

                originalBitmap.recycle();
                finalBitmap.recycle();
                filteredBitmap.recycle();

                originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
                photoEditorView.getSource().setImageBitmap(originalBitmap);
                bitmap.recycle();

                filtersListFragment =  FiltersListFragment.getInstance(originalBitmap);
                filtersListFragment.setListener(this);
            }
            else if (requestCode == UCrop.REQUEST_CROP)
            {
                handleCropResult(data);
            }
        }
        else if (requestCode == UCrop.RESULT_ERROR)
        {
            handleCropError(data);
        }
    }

    private void handleCropError(Intent data) {
        final Throwable cropError = UCrop.getError(data);

        if (cropError != null)
        {
            Toast.makeText(this, "" + cropError.getMessage(), Toast.LENGTH_SHORT).show();
        }
        else 
        {
            Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleCropResult(Intent data) {
        final Uri resultUri = UCrop.getOutput(data);
        if (resultUri != null)
        {
            photoEditorView.getSource().setImageURI(resultUri);

            Bitmap bitmap = ((BitmapDrawable)photoEditorView.getSource().getDrawable()).getBitmap();
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredBitmap = originalBitmap;
            finalBitmap = originalBitmap;
        }
        else
        {
            Toast.makeText(this, "Cannot retrieve crop image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBrushSizeChangeListener(float size) {
        photoEditor.setBrushSize(size);
    }

    @Override
    public void onBrushOpacityChangeListener(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushColorChangeListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangeListener(boolean isEraser) {
        if(isEraser)
            photoEditor.brushEraser();
        else
            photoEditor.setBrushDrawingMode(true);
    }

    @Override
    public void onAddTextButtonClick(Typeface typeface, String text, int color) {
        photoEditor.addText(typeface,text,color);
    }

    @Override
    public void onEmojiSelected(String emoji) {
        photoEditor.addEmoji(emoji);
    }
}