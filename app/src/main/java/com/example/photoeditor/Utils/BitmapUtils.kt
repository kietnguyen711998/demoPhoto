package com.example.photoeditor.Utils

import android.content.ContentResolver
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.net.Uri
import android.provider.MediaStore
import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStream

object BitmapUtils {
    fun getBitmapFromAssets(context: Context, fileName: String, width: Int, height: Int): Bitmap? {
        val assetManager = context.assets
        val inputStream: InputStream
        val bitmap: Bitmap? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            inputStream = assetManager.open(fileName)
            options.inSampleSize = calculateInSampleSize(options, width, height)
            options.inJustDecodeBounds = false
            return BitmapFactory.decodeStream(inputStream, null, null)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
        return null
    }

    fun getBitmapFromGallery(context: Context, uri: Uri, width: Int, height: Int): Bitmap? {
        val filePathColumn = arrayOf<String?>(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(uri, filePathColumn, null, null, null, null)
        cursor?.moveToFirst()
        val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
        val picturePath = cursor?.getString(columnIndex!!)
        cursor?.close()
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeFile(picturePath, options)
        options.inSampleSize = calculateInSampleSize(options, width, height)
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeFile(picturePath, options)
    }

    fun applyOverlay(context: Context, sourceImage: Bitmap, overlayDrawableResourceId: Int): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val width = sourceImage.getWidth()
            val height = sourceImage.getHeight()
            val r = context.getResources()
            val imageAsDrawable: Drawable = BitmapDrawable(r, sourceImage)
            val layers = arrayOfNulls<Drawable?>(2)
            layers[0] = imageAsDrawable
            layers[1] = BitmapDrawable(r, decodeSampledBitmapFromResource(r, overlayDrawableResourceId, width, height))
            val layerDrawable = LayerDrawable(layers)
            bitmap = drawableToBitmap(layerDrawable)
        } catch (ex: Exception) {
        }
        return bitmap
    }

    fun decodeSampledBitmapFromResource(res: Resources, resId: Int,
                                        reqWidth: Int, reqHeight: Int): Bitmap? {

        // First decode with inJustDecodeBounds=true to check dimensions
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, options)

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false
        return BitmapFactory.decodeResource(res, resId, options)
    }

    private fun calculateInSampleSize(
            options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        // Raw height and width of image
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1
        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight
                    && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        var bitmap: Bitmap? = null
        if (drawable is BitmapDrawable) {
            val bitmapDrawable = drawable as BitmapDrawable?
            if (bitmapDrawable?.getBitmap() != null) {
                return bitmapDrawable.getBitmap()
            }
        }
        bitmap = if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
        } else {
            Bitmap.createBitmap(drawable.intrinsicWidth, drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888)
        }
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    @Throws(IOException::class)
    fun insertImage(cr: ContentResolver, source: Bitmap, title: String, description: String?): String? {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title)
        values.put(MediaStore.Images.Media.DESCRIPTION, description)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis())
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        var uri: Uri? = null
        var stringUrl: String? = null
        try {
            uri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val outputStream = cr.openOutputStream(uri!!)
            outputStream.use { outputStream ->
                source.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
            }
            val id = ContentUris.parseId(uri)
            val miniThumb = MediaStore.Images.Thumbnails.getThumbnail(cr, id, MediaStore.Images.Thumbnails.MINI_KIND, null)
            storeThumbnail(cr, miniThumb, id, 50f, 50f, MediaStore.Images.Thumbnails.MICRO_KIND)
        } catch (e: FileNotFoundException) {
            if (uri != null) {
                cr.delete(uri, null, null)
                uri = null
            }
            e.printStackTrace()
        }
        if (uri != null) stringUrl = uri.toString()
        return stringUrl
    }

    private fun storeThumbnail(cr: ContentResolver, source: Bitmap, id: Long, width: Float, height: Float, kind: Int): Bitmap? {
        val matrix = Matrix()
        val scaleX = width / source.width
        val scaleY = height / source.height
        matrix.setScale(scaleX, scaleY)
        val thumb = Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
        val contentValues = ContentValues(4)
        contentValues.put(MediaStore.Images.Thumbnails.KIND, kind)
        contentValues.put(MediaStore.Images.Thumbnails.IMAGE_ID, id)
        contentValues.put(MediaStore.Images.Thumbnails.WIDTH, width)
        contentValues.put(MediaStore.Images.Thumbnails.HEIGHT, height)
        val uri = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, contentValues)
        return try {
            val outputStream = cr.openOutputStream(uri!!)
            thumb.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream?.close()
            thumb
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}