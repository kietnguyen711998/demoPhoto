package com.example.photoeditor

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.photoeditor.Utils.BitmapUtils
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {
    var image_taken_by_camera_uri: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val actionBar = supportActionBar
        actionBar?.hide()
        setContentView(R.layout.activity_start)
        val iw = findViewById<View?>(R.id.app_logo) as ImageView?
        val applogoBitmap = BitmapUtils.getBitmapFromAssets(this, "app_logo.png", 240, 240)
        iw?.setImageBitmap(applogoBitmap)
        btn_open_image?.setOnClickListener { openImageFromGallery() }
        btn_open_camera?.setOnClickListener { openCamera() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == MainActivity.PERMISSION_PICK_IMAGE) {
                val intent = Intent(this@StartActivity, MainActivity::class.java)
                intent.data = data?.data
                startActivity(intent)
            } else if (requestCode == MainActivity.CAMERA_REQUEST) {
                val intent = Intent(this@StartActivity, MainActivity::class.java)
                intent.data = image_taken_by_camera_uri
                startActivity(intent)
            }
        }
    }

    private fun openCamera() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val values = ContentValues()
                            values.put(MediaStore.Images.Media.TITLE, "New picture")
                            values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera")
                            image_taken_by_camera_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_taken_by_camera_uri)
                            startActivityForResult(cameraIntent, MainActivity.Companion.CAMERA_REQUEST)
                        } else {
                            Toast.makeText(this@StartActivity, "Permission denied!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest?>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
    }

    private fun openImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(intent, MainActivity.Companion.PERMISSION_PICK_IMAGE)
                        } else {
                            Toast.makeText(this@StartActivity, "Permission denied!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest?>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
    }
}