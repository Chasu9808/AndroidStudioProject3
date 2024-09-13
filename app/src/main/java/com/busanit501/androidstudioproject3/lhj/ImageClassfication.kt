package com.busanit501.androidstudioproject3.lhj

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.busanit501.androidlabtest501.R
import com.busanit501.androidstudioproject3.Service.ClassificationResponse
import com.busanit501.androidstudioproject3.Service.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class ImageClassification : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200
    private val CAMERA_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("ImageClassification", "onCreate 실행됨")
        setContentView(R.layout.image_classify)

        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)
        val buttonCamera: Button = findViewById(R.id.buttonCamera)
        val buttonGallery: Button = findViewById(R.id.buttonGallery)

        buttonCamera.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.CAMERA
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_CODE
                )
            } else {
                openCamera()
            }
        }

        buttonGallery.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            Toast.makeText(this, "카메라를 실행할 수 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera()
        } else {
            Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    val extras = data.extras
                    val photo: Bitmap? = extras?.get("data") as Bitmap?
                    if (photo != null) {
                        imageView.setImageBitmap(photo)
                        uploadImage(photo)
                    } else {
                        Toast.makeText(this, "사진을 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImage: Uri? = data.data
                    if (selectedImage != null) {
                        imageView.setImageURI(selectedImage)
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        uploadImage(bitmap)
                    } else {
                        Toast.makeText(this, "이미지를 선택할 수 없습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun uploadImage(bitmap: Bitmap) {
        val file = convertBitmapToFile("image.jpg", bitmap)
        file?.let {
            val requestFile = it.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val body = MultipartBody.Part.createFormData("image", it.name, requestFile)
            RetrofitClient.instance.uploadImage(body)
                .enqueue(object : Callback<ClassificationResponse> {
                    override fun onResponse(
                        call: Call<ClassificationResponse>,
                        response: Response<ClassificationResponse>
                    ) {
                        if (response.isSuccessful) {
                            val result = response.body()
                            result?.let {
                                val displayText = "Predicted Class: ${it.predictedClassLabel}"
                                resultTextView.text = displayText
                            }
                        } else {
                            Toast.makeText(
                                this@ImageClassification,
                                "응답을 가져오는 데 실패했습니다.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(call: Call<ClassificationResponse>, t: Throwable) {
                        Toast.makeText(this@ImageClassification, "서버와 연결할 수 없습니다.", Toast.LENGTH_SHORT)
                            .show()
                    }
                })
        } ?: run {
            Toast.makeText(this, "이미지 파일 변환에 실패했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun convertBitmapToFile(filename: String, bitmap: Bitmap): File? {
        return try {
            val file = File(applicationContext.cacheDir, filename)
            file.createNewFile()
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}