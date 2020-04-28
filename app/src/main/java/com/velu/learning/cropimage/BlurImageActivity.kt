package com.velu.learning.cropimage

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.renderscript.RenderScript
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.animation.GlideAnimation
import com.bumptech.glide.request.target.SimpleTarget
import com.commit451.nativestackblur.NativeStackBlur
import kotlinx.android.synthetic.main.activity_blur_image.*
import java.io.File
import java.io.FileOutputStream


class BlurImageActivity : AppCompatActivity() {

    private lateinit var originalBitmapImage: Bitmap
    private lateinit var customizedBitmapImage: Bitmap
    private lateinit var rs: RenderScript


    companion object {
        private const val TAG = "BlurImageActivity"

        const val EXTRAS_IMAGE_URI = "image_uri"

        fun getStartIntent(context: Context, uri: Uri): Intent {
            val intent = Intent(context, BlurImageActivity::class.java)
            intent.putExtra(EXTRAS_IMAGE_URI, uri)
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blur_image)

        rs = RenderScript.create(this)

        val imageUri: Uri = intent.getParcelableExtra(EXTRAS_IMAGE_URI)
        loadImageUsingGlide(imageUri)

        seekbar_blur.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val radius = if (progress <= 0) 1 else progress

                customizedBitmapImage = NativeStackBlur.process(originalBitmapImage, radius)

                iv_cropped_image.setImageBitmap(customizedBitmapImage)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private fun loadImageUsingGlide(imageUri: Uri) {

        Glide.with(this)
            .load(imageUri)
            .asBitmap()
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(object : SimpleTarget<Bitmap?>() {

                override fun onResourceReady(
                    resource: Bitmap?,
                    glideAnimation: GlideAnimation<in Bitmap?>?
                ) {
                    originalBitmapImage = resource!!
                    customizedBitmapImage = resource
                    iv_cropped_image.setImageBitmap(originalBitmapImage)
                }
            })


    }

    private fun saveBitmapChangesToFile() {
        val imageFile = File(cacheDir, Constants.CROPPED_IMAGE_NAME)
        try {
            val os = FileOutputStream(imageFile)
            customizedBitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, os)
            os.flush()
            os.close()

            Toast.makeText(this, "Changes saved", Toast.LENGTH_LONG).show()
            finish()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_image_result, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == R.id.menu_done) {
            saveBitmapChangesToFile()
        }

        return super.onOptionsItemSelected(item)
    }
}
