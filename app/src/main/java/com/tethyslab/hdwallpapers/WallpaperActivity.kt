package com.tethyslab.hdwallpapers

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.doOnPreDraw
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import com.takusemba.spotlight.Spotlight
import com.takusemba.spotlight.Target
import kotlinx.android.synthetic.main.layout_wallpaper.*
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream

class WallpaperActivity : AppCompatActivity() {
    lateinit var currentBitmap : Bitmap
    private lateinit var sharedPreferences : SharedPreferences
    private val target = object : com.squareup.picasso.Target {
        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
            Snackbar.make(findViewById(R.id.wallpaperLayout), "Image loading...", Snackbar.LENGTH_SHORT)
                .setTextColor(resources.getColor(R.color.colorAccent))
                .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                .show()
        }

        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
            wallpaperProgresBar.visibility = View.GONE
            Snackbar.make(findViewById(R.id.wallpaperLayout), "Error: "+e?.message, Snackbar.LENGTH_SHORT)
                .setTextColor(resources.getColor(R.color.colorAccent))
                .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                .show()
        }
        override fun onBitmapLoaded(
            bitmap: Bitmap?,
            from: Picasso.LoadedFrom?
        ) {
            wallpaperProgresBar.visibility = View.GONE
            try{
                imageView.setImageBitmap(bitmap)
                currentBitmap = bitmap!!

            }catch (e: Exception){
                Snackbar.make(findViewById(R.id.wallpaperLayout), "Error: "+e.message, Snackbar.LENGTH_SHORT)
                    .setTextColor(resources.getColor(R.color.colorAccent))
                    .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                    .show()
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_wallpaper)

        val circularProgressDrawable = CircularProgressDrawable(applicationContext)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        sharedPreferences = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE)
        val editor : SharedPreferences.Editor = sharedPreferences.edit()
        val currentImage = sharedPreferences.getString("currentCatLink",null)

        Picasso.get().load(currentImage).into(target)

        if (sharedPreferences.contains("wallpaperTarget")) {
            window.decorView.rootView.doOnPreDraw {
                val target = Target.Builder()
                    .setOverlay(layoutInflater.inflate(R.layout.wallpaper_target, null))
                    .build()
                var spotlight = Spotlight.Builder(this)
                    .setTargets(target)
                    .setBackgroundColor(R.color.background)
                    .build()
                spotlight.start()
                editor.remove("wallpaperTarget").apply()
                imageView.setOnClickListener { spotlight.finish() }

            }
        }
        saveGallery.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
            }else{
                saveToGallery(applicationContext,currentBitmap,"Daily Wallpaper")
            }
        }
    }
    fun saveToGallery(context: Context, bitmap: Bitmap, albumName: String) {
        val filename = "${System.currentTimeMillis()}.png"
        val write: (OutputStream) -> Boolean = {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                    put(
                        MediaStore.MediaColumns.RELATIVE_PATH,
                        "${Environment.DIRECTORY_DCIM}/$albumName"
                    )
                }

                context.contentResolver.let {
                    it.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                        ?.let { uri ->
                            it.openOutputStream(uri)?.let(write)
                        }
                }
                Snackbar.make(findViewById(R.id.wallpaperLayout),"Saved...", Snackbar.LENGTH_LONG)
                    .setTextColor(resources.getColor(R.color.colorAccent))
                    .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                    .show()
            }catch (e:Exception){
                Snackbar.make(findViewById(R.id.wallpaperLayout),"Error..."+e.message, Snackbar.LENGTH_SHORT)
                    .setTextColor(resources.getColor(R.color.colorAccent))
                    .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                    .show()
            }
        } else {
            try {
                val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + File.separator + albumName
                val file = File(imagesDir)
                if (!file.exists()) {
                    file.mkdir()
                }
                val image = File(imagesDir, filename)
                write(FileOutputStream(image))
                MediaScannerConnection.scanFile(
                    this, arrayOf(image.toString()), null
                ) { path, uri -> }
                Snackbar.make(findViewById(R.id.wallpaperLayout), "Saved...", Snackbar.LENGTH_LONG)
                    .setTextColor(resources.getColor(R.color.colorAccent))
                    .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                    .show()
            }catch (e: Exception){
                Snackbar.make(findViewById(R.id.wallpaperLayout), "Error..."+e.message, Snackbar.LENGTH_SHORT)
                    .setTextColor(resources.getColor(R.color.colorAccent))
                    .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                    .show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 0){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                saveToGallery(applicationContext,currentBitmap,"Daily Wallpaper")
            }else{
                Snackbar.make(findViewById(R.id.wallpaperLayout), "Permission denied...", Snackbar.LENGTH_SHORT)
                    .setTextColor(resources.getColor(R.color.colorAccent))
                    .setBackgroundTint(resources.getColor(R.color.colorPrimaryDark))
                    .show()
            }
        }
    }
}
