package com.velu.learning.cropimage

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image_result.*

class ImageResultActivity : AppCompatActivity() {

    companion object {
        const val EXTRAS_IMAGE_BITMAP = "image_bitmap"

        fun getStartIntent(context: Context, bitmapImage: Bitmap): Intent {
            val intent = Intent(context, ImageResultActivity::class.java)
            intent.putExtra(EXTRAS_IMAGE_BITMAP, bitmapImage)
            return intent
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_result)

        val bitmapImage: Bitmap = intent.getParcelableExtra(EXTRAS_IMAGE_BITMAP)
        iv_cropped_image.setImageBitmap(blurRenderScript(bitmapImage, 1))

        seekbar_blur.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                val radius = if(progress <= 0) 1 else progress
                iv_cropped_image.setImageBitmap(blurRenderScript(bitmapImage, radius))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })
    }



    private fun blurRenderScript(smallBitmap: Bitmap, radius: Int): Bitmap? {

        var smallBitmap = smallBitmap
        try {
            smallBitmap = RGB565toARGB888(smallBitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val bitmap = Bitmap.createBitmap(
            smallBitmap.width, smallBitmap.height,
            Bitmap.Config.ARGB_8888
        )
        val renderScript = RenderScript.create(this)
        val blurInput = Allocation.createFromBitmap(renderScript, smallBitmap)
        val blurOutput = Allocation.createFromBitmap(renderScript, bitmap)
        val blur = ScriptIntrinsicBlur.create(
            renderScript,
            Element.U8_4(renderScript)
        )
        blur.setInput(blurInput)
        blur.setRadius(radius.toFloat()) // radius must be 0 < r <= 25
        blur.forEach(blurOutput)
        blurOutput.copyTo(bitmap)
        renderScript.destroy()
        return bitmap
    }

    @Throws(java.lang.Exception::class)
    private fun RGB565toARGB888(img: Bitmap): Bitmap {
        val numPixels = img.width * img.height
        val pixels = IntArray(numPixels)
        //Get JPEG pixels.  Each int is the color values for one pixel.
        img.getPixels(pixels, 0, img.width, 0, 0, img.width, img.height)
        //Create a Bitmap of the appropriate format.
        val result =
            Bitmap.createBitmap(img.width, img.height, Bitmap.Config.ARGB_8888)
        //Set RGB pixels.
        result.setPixels(pixels, 0, result.width, 0, 0, result.width, result.height)
        return result
    }
}
