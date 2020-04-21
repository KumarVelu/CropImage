package com.velu.learning.cropimage

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    companion object{
        private const val TAG = "MainActivity"
        private const val RC_SELECT_IMAGE = 101
        private const val RC_CROP_IMAGE = 102
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_choose_pic.setOnClickListener{
           launchImagePickerActivity()
        }
    }

    private fun launchImagePickerActivity(){
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, RC_SELECT_IMAGE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        if(resultCode == RESULT_OK){

            if(requestCode == RC_SELECT_IMAGE){
                intent?.data?.let {
                    cropImage(it)
                } ?: Toast.makeText(this, "Try again", Toast.LENGTH_LONG).show()
            }else if(requestCode == RC_CROP_IMAGE){
                val extras = intent?.extras
                val selectedBitmap = extras?.getParcelable<Bitmap>("data")
                launchImageResultActivity(selectedBitmap)
            }
        }
    }

    private fun launchImageResultActivity(selectedBitmap: Bitmap?) {
            selectedBitmap?.let {
                startActivity(ImageResultActivity.getStartIntent(this, it))
            }
    }

    /* private fun processIntentDataFromGallery(imageUri : Uri){
         val projection = arrayOf(MediaStore.Images.Media.DATA)
         val cursor = contentResolver.query(imageUri, projection, null, null,
             null)
         cursor?.let {
             it.moveToFirst()
             val imagePath = it.getString(0)
             cropImage(imagePath)
             it.close()
         }
     }*/

    private fun cropImage(imageUri: Uri){
        val cropIntent = Intent("com.android.camera.action.CROP")

        cropIntent.setDataAndType(imageUri, "image/*")
        // set crop properties
        cropIntent.putExtra("crop", "true")
        // indicate aspect of desired crop
        cropIntent.putExtra("aspectX", 1)
        cropIntent.putExtra("aspectY", 1)
        // indicate output X and Y
        cropIntent.putExtra("outputX", 1000)
        cropIntent.putExtra("outputY", 1000)

        // retrieve data on return
        cropIntent.putExtra("return-data", true);
        // start the activity - we handle returning in onActivityResult
        startActivityForResult(cropIntent, RC_CROP_IMAGE);
    }
}
