package com.velu.learning.cropimage

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.view.Menu
import android.view.MenuItem
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.activity_blur_image.*
import java.io.File
import java.io.FileOutputStream


class BlurImageActivity : AppCompatActivity() {

    private lateinit var bitmapImage: Bitmap
    private lateinit var rs: RenderScript

    companion object {
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
                bitmapImage = BlurBuilder.blurRenderScript(this@BlurImageActivity, bitmapImage, radius)!!
                iv_cropped_image.setImageBitmap(bitmapImage)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }

    private fun loadImageUsingGlide(imageUri: Uri) {
        Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .skipMemoryCache(true)
            .diskCacheStrategy(DiskCacheStrategy.NONE)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    bitmapImage = resource
                    iv_cropped_image.setImageBitmap(bitmapImage)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    /* this is called when imageView is cleared on lifecycle call or for some other reason.
                    if you are referencing the bitmap somewhere else too other than this imageView clear it here as you can no longer have the bitmap
                     */
                }
            })
    }

    private fun saveBitmapChangesToFile() {
        val imageFile = File(cacheDir, Constants.CROPPED_IMAGE_NAME)
        try {
            val os = FileOutputStream(imageFile)
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, os)
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
