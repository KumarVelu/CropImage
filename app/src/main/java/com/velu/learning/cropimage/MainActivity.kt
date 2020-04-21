package com.velu.learning.cropimage

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yalantis.ucrop.UCrop
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
        private const val RC_SELECT_IMAGE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_choose_pic.setOnClickListener{
           launchImagePickerActivity()
        }

        btn_test.setOnClickListener{
            startActivity(Intent(this, ImageResultActivity::class.java))
        }
    }

    private fun launchImagePickerActivity(){
        val intent = Intent(Intent.ACTION_GET_CONTENT)
            .setType("image/*")
            .addCategory(Intent.CATEGORY_OPENABLE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val mimeTypes = arrayOf("image/jpeg", "image/png")
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        }

        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.label_select_picture)), RC_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if(resultCode == RESULT_OK){

            if(requestCode == RC_SELECT_IMAGE){
                val selectedUri = intent?.data
                if (selectedUri != null) {
                    startCrop(selectedUri)
                } else {
                    Toast.makeText(this, R.string.toast_cannot_retrieve_selected_image, Toast.LENGTH_SHORT).show()
                }
            }else if(requestCode == UCrop.REQUEST_CROP){
                handleCropResult(intent!!)
            }
        }
    }

    private fun startCrop(uri: Uri) {
        UCrop.of(uri, Uri.fromFile(File(cacheDir, Constants.CROPPED_IMAGE_NAME)))
            .start(this)
    }

    private fun handleCropResult(result: Intent) {
        val resultUri = UCrop.getOutput(result)
        if (resultUri != null) {
            startActivity(BlurImageActivity.getStartIntent(this, resultUri))
        } else {
            Toast.makeText(this, R.string.toast_cannot_retrieve_cropped_image, Toast.LENGTH_SHORT).show()
        }
    }
}
