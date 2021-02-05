package com.example.photoeditor

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.viewpager.widget.ViewPager
import com.example.photoeditor.Adapter.ViewPagerAdapter
import com.example.photoeditor.Interface.*
import com.example.photoeditor.Utils.BitmapUtils
import com.google.android.material.snackbar.Snackbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.yalantis.ucrop.UCrop
import com.zomato.photofilters.imageprocessors.Filter
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter
import ja.burhanrashid52.photoeditor.OnSaveBitmap
import ja.burhanrashid52.photoeditor.PhotoEditor
import ja.burhanrashid52.photoeditor.PhotoEditorView
import java.io.File
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), FiltersListFragmentListener, EditImageFragmentListener, BrushFragmentListener, AddTextFragmentListener, EmojiFragmentListener {
    var photoEditor: PhotoEditor? = null
    var photoEditorView: PhotoEditorView? = null
    var coordinatorLayout: CoordinatorLayout? = null
    var originalBitmap: Bitmap? = null
    var filteredBitmap: Bitmap? = null
    var finalBitmap: Bitmap? = null
    var filtersListFragment: FiltersListFragment? = null
    var editImageFragment: EditImageFragment? = null
    var btn_filters_list: CardView? = null
    var btn_edit: CardView? = null
    var btn_brush: CardView? = null
    var btn_emoji: CardView? = null
    var btn_text: CardView? = null
    var btn_add_image: CardView? = null
    var btn_crop: CardView? = null
    var edit_side: LinearLayout? = null
    var brightnessfinal = 0
    var saturationfinal = 1.0f
    var constraintfinal = 1.0f
    var onEdit = false
    var image_selected_uri: Uri? = null

    companion object {
        val pictureName: String? = "IU.jpg"
        const val PERMISSION_PICK_IMAGE = 1000
        const val PERMISSION_INSERT_IMAGE = 1001
        const val CAMERA_REQUEST = 1002

        init {
            System.loadLibrary("NativeImageProcessor")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<Toolbar?>(R.id.toolBar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setTitle("    PhotoEditor")
        supportActionBar?.setIcon(R.mipmap.ic_final_app_logo)
        photoEditorView = findViewById<View?>(R.id.image_preview) as PhotoEditorView?
        photoEditor = PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .setDefaultEmojiTypeface(Typeface.createFromAsset(assets, "emojione-android.ttf"))
                .build()
        coordinatorLayout = findViewById<View?>(R.id.coordinator) as CoordinatorLayout?
        edit_side = findViewById<View?>(R.id.edit_side) as LinearLayout?
        edit_side?.visibility = View.INVISIBLE
        btn_filters_list = findViewById<View?>(R.id.btn_filter_list) as CardView?
        btn_edit = findViewById<View?>(R.id.btn_edit) as CardView?
        btn_brush = findViewById<View?>(R.id.btn_brush) as CardView?
        btn_text = findViewById<View?>(R.id.btn_text) as CardView?
        btn_emoji = findViewById<View?>(R.id.btn_emoji) as CardView?
        btn_crop = findViewById<View?>(R.id.btn_crop) as CardView?
        btn_add_image = findViewById<View?>(R.id.btn_add_image) as CardView?
        btn_filters_list?.setOnClickListener(View.OnClickListener {
            if (filtersListFragment != null) {
                filtersListFragment!!.show(supportFragmentManager, filtersListFragment!!.getTag())
            } else {
                val filtersListFragment: FiltersListFragment? = FiltersListFragment.getInstance(null)
                if (filtersListFragment != null) {
                    FiltersListFragment.setListener(filtersListFragment, this@MainActivity)
                }
                filtersListFragment?.show(supportFragmentManager, filtersListFragment.tag)
            }
        })
        btn_edit?.setOnClickListener(View.OnClickListener {
            val editImageFragment: EditImageFragment? = EditImageFragment.getInstance()
            editImageFragment?.setListener(this@MainActivity)
            editImageFragment?.show(supportFragmentManager, editImageFragment.tag)
        })
        btn_brush?.setOnClickListener(View.OnClickListener {
            photoEditor!!.setBrushDrawingMode(true)
            val brushFragment: BrushFragment? = BrushFragment.getInstance()
            if (brushFragment != null) {
                BrushFragment.setListener(brushFragment, this@MainActivity)
            }
            brushFragment?.show(supportFragmentManager, brushFragment.tag)
        })
        btn_text?.setOnClickListener(View.OnClickListener {
            val addTextFragment: AddTextFragment? = AddTextFragment.getInstance()
            if (addTextFragment != null) {
                AddTextFragment.setListener(addTextFragment, this@MainActivity)
            }
            addTextFragment?.show(supportFragmentManager, addTextFragment.tag)
        })
        btn_emoji?.setOnClickListener {
            val emojiFragment: EmojiFragment? = EmojiFragment.getInstance()
            if (emojiFragment != null) {
                EmojiFragment.setListener(emojiFragment, this@MainActivity)
            }
            emojiFragment?.show(supportFragmentManager, emojiFragment.tag)
        }
        btn_crop?.setOnClickListener { startCrop(image_selected_uri) }
        btn_add_image?.setOnClickListener { addImageToPicture() }
        loadImage()
        image_selected_uri = intent.data
        if (image_selected_uri != null) {
            setData(image_selected_uri)
        }
    }

    private fun addImageToPicture() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            startActivityForResult(intent, PERMISSION_INSERT_IMAGE)
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest?>?, token: PermissionToken?) {
                        Toast.makeText(this@MainActivity, "Permission Denied", Toast.LENGTH_SHORT).show()
                    }
                }).check()
    }

    private fun startCrop(uri: Uri?) {
        val destinationFileName = StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString()
        val ucrop = UCrop.of(uri!!, Uri.fromFile(File(cacheDir, destinationFileName)))
        ucrop.start(this@MainActivity)
    }

    private fun loadImage() {
        originalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName!!, 100, 100)
        filteredBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        finalBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        photoEditorView?.source?.setImageBitmap(originalBitmap)
    }

    private fun setupViewPager(viewPager: ViewPager?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        filtersListFragment = FiltersListFragment()
        FiltersListFragment.setListener(filtersListFragment!!, this)
        editImageFragment = EditImageFragment()
        editImageFragment?.setListener(this)
        adapter.addFragment(filtersListFragment!!, "FILTERS")
        adapter.addFragment(editImageFragment!!, "EDIT")
        viewPager?.adapter = adapter
    }

    override fun onBrightnessChanged(brightness: Int) {
        brightnessfinal = brightness
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightness))
        photoEditorView?.source?.setImageBitmap(myFilter.processFilter(filteredBitmap?.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onSaturationChanged(saturation: Float) {
        saturationfinal = saturation
        val myFilter = Filter()
        myFilter.addSubFilter(SaturationSubfilter(saturation))
        photoEditorView?.source?.setImageBitmap(myFilter.processFilter(filteredBitmap?.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onConstraintChanged(constraint: Float) {
        constraintfinal = constraint
        val myFilter = Filter()
        myFilter.addSubFilter(ContrastSubFilter(constraint))
        photoEditorView?.source?.setImageBitmap(myFilter.processFilter(filteredBitmap?.copy(Bitmap.Config.ARGB_8888, true)))
    }

    override fun onEditStarted() {}
    override fun onEditCompleted() {
        val bitmap = filteredBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        val myFilter = Filter()
        myFilter.addSubFilter(BrightnessSubFilter(brightnessfinal))
        myFilter.addSubFilter(SaturationSubfilter(saturationfinal))
        myFilter.addSubFilter(ContrastSubFilter(constraintfinal))
        finalBitmap = myFilter.processFilter(bitmap)
    }

    override fun onFilterSelected(filter: Filter) {
        //resetControl();
        filteredBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        photoEditorView?.source?.setImageBitmap(filter.processFilter(filteredBitmap))
        finalBitmap = filteredBitmap?.copy(Bitmap.Config.ARGB_8888, true)
    }

    private fun resetControl() {
        if (editImageFragment == null) editImageFragment?.resetControls()
        brightnessfinal = 0
        saturationfinal = 1.0f
        constraintfinal = 1.0f
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_open) {
            openImageFromGallery()
            return true
        } else if (id == R.id.action_save && onEdit) {
            saveImageToGallery()
            return true
        } else if (id == R.id.action_camera) {
            openCamera()
            return true
        }
        return super.onOptionsItemSelected(item)
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
                            image_selected_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
                            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_selected_uri)
                            startActivityForResult(cameraIntent, CAMERA_REQUEST)
                        } else {
                            Toast.makeText(this@MainActivity, "Permission denied!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest?>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
    }

    private fun saveImageToGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        if (report!!.areAllPermissionsGranted()) {
                            photoEditor?.saveAsBitmap(object : OnSaveBitmap {
                                override fun onBitmapReady(saveBitmap: Bitmap?) {
                                    try {
                                        finalBitmap = saveBitmap
                                        photoEditorView?.source?.setImageBitmap(saveBitmap)
                                        val path = BitmapUtils.insertImage(contentResolver, finalBitmap!!, System.currentTimeMillis().toString() + "_profile.jpg", null)
                                        if (!TextUtils.isEmpty(path)) {
                                            val snackbar = Snackbar.make(coordinatorLayout!!, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                                                    .setAction("OPEN") { openImage(path) }
                                            snackbar.show()
                                        } else {
                                            val snackbar = Snackbar.make(coordinatorLayout!!, "Image saved to gallery!", Snackbar.LENGTH_LONG)
                                            snackbar.show()
                                        }
                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }
                                }

                                override fun onFailure(e: Exception?) {}
                            })
                        } else {
                            Toast.makeText(this@MainActivity, "Permission denied!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest?>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
    }

    private fun openImage(path: String?) {
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.setDataAndType(Uri.parse(path), "image/*")
        startActivity(intent)
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
                            startActivityForResult(intent, PERMISSION_PICK_IMAGE)
                        } else {
                            Toast.makeText(this@MainActivity, "Permission denied!", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest?>?, token: PermissionToken?) {
                        token?.continuePermissionRequest()
                    }
                })
                .check()
    }

    private fun setData(uri: Uri?) {
        edit_side?.visibility = View.VISIBLE
        onEdit = true
        val bitmap = BitmapUtils.getBitmapFromGallery(this, uri!!, 800, 800)
        originalBitmap?.recycle()
        finalBitmap?.recycle()
        filteredBitmap?.recycle()
        originalBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
        finalBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        filteredBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
        photoEditorView?.source?.setImageBitmap(originalBitmap)
        bitmap?.recycle()
        filtersListFragment = FiltersListFragment.getInstance(originalBitmap)
        filtersListFragment?.let { FiltersListFragment.setListener(it, this) }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            edit_side?.visibility = View.VISIBLE
            onEdit = true
            if (requestCode == PERMISSION_PICK_IMAGE) {
                val bitmap = BitmapUtils.getBitmapFromGallery(this, data?.data!!, 800, 800)
                image_selected_uri = data.getData()
                originalBitmap?.recycle()
                finalBitmap?.recycle()
                filteredBitmap?.recycle()
                originalBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                finalBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
                filteredBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
                photoEditorView?.source?.setImageBitmap(originalBitmap)
                bitmap?.recycle()
                filtersListFragment = FiltersListFragment.getInstance(originalBitmap)
                filtersListFragment?.let { FiltersListFragment.setListener(it, this) }
            } else if (requestCode == PERMISSION_INSERT_IMAGE) {
                val bitmap = BitmapUtils.getBitmapFromGallery(this, data?.data!!, 250, 250)
                photoEditor?.addImage(bitmap)
            }
            if (requestCode == CAMERA_REQUEST) {
                val bitmap = BitmapUtils.getBitmapFromGallery(this, image_selected_uri!!, 800, 800)
                originalBitmap?.recycle()
                finalBitmap?.recycle()
                filteredBitmap?.recycle()
                originalBitmap = bitmap?.copy(Bitmap.Config.ARGB_8888, true)
                finalBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
                filteredBitmap = originalBitmap?.copy(Bitmap.Config.ARGB_8888, true)
                photoEditorView?.source?.setImageBitmap(originalBitmap)
                bitmap?.recycle()
                filtersListFragment = FiltersListFragment.getInstance(originalBitmap)
                filtersListFragment?.let { FiltersListFragment.setListener(it, this) }
            } else if (requestCode == UCrop.REQUEST_CROP) {
                handleCropResult(data)
            }
        } else if (requestCode == UCrop.RESULT_ERROR) {
            handleCropError(data)
        }
    }

    private fun handleCropError(data: Intent?) {
        val cropError = UCrop.getError(data!!)
        if (cropError != null) {
            Toast.makeText(this, "" + cropError.message, Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Unexpected Error", Toast.LENGTH_SHORT).show()
        }
    }

    private fun handleCropResult(data: Intent?) {
        val resultUri = UCrop.getOutput(data!!)
        if (resultUri != null) {
            photoEditorView?.source?.setImageURI(resultUri)
            val bitmap = (photoEditorView?.source?.drawable as BitmapDrawable).bitmap
            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
            filteredBitmap = originalBitmap
            finalBitmap = originalBitmap
        } else {
            Toast.makeText(this, "Cannot retrieve crop image", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onBrushSizeChangeListener(size: Float) {
        photoEditor?.brushSize = size
    }

    override fun onBrushOpacityChangeListener(opacity: Int) {
        photoEditor?.setOpacity(opacity)
    }

    override fun onBrushColorChangeListener(color: Int) {
        photoEditor?.brushColor = color
    }

    override fun onBrushStateChangeListener(isEraser: Boolean) {
        if (isEraser) photoEditor?.brushEraser() else photoEditor?.setBrushDrawingMode(true)
    }

    override fun onAddTextButtonClick(typeface: Typeface, text: String, color: Int) {
        photoEditor?.addText(typeface, text, color)
    }

    override fun onEmojiSelected(emoji: String) {
        photoEditor?.addEmoji(emoji)
    }
}