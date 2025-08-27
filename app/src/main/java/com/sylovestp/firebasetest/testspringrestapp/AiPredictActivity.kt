package com.sylovestp.firebasetest.testspringrestapp

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sylovestp.firebasetest.testspringrestapp.dto.PredictionResult
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream

class AiPredictActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var resultTextView: TextView
    private lateinit var jwtToken: String

    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200
    private val CAMERA_PERMISSION_CODE = 101
    private val MEDIA_PERMISSION_CODE = 102

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AiPredictActivity", "onCreate 실행됨")
        setContentView(R.layout.activity_ai_predict)

        jwtToken = getJwtTokenFromSharedPreferences()
        if (jwtToken.isEmpty()) {
            showToast("JWT 토큰이 없습니다. 다시 로그인해주세요.")
            finish()
            return
        }

        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)
        val buttonCamera: Button = findViewById(R.id.buttonCamera)
        val buttonGallery: Button = findViewById(R.id.buttonGallery)

        buttonCamera.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        buttonGallery.setOnClickListener {
            checkGalleryPermissionAndOpenGallery()
        }
    }

    private fun getJwtTokenFromSharedPreferences(): String {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("jwt_token", "").orEmpty()
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                showToast("카메라 접근 권한이 필요합니다.")
            }
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            openCamera()
        }
    }

    private fun checkGalleryPermissionAndOpenGallery() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
            != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_MEDIA_IMAGES)) {
                showToast("갤러리 접근 권한이 필요합니다.")
            }
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_IMAGES),
                MEDIA_PERMISSION_CODE
            )
        } else {
            openGallery()
        }
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (cameraIntent.resolveActivity(packageManager) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE)
        } else {
            showToast("카메라를 실행할 수 없습니다.")
        }
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openCamera()
                } else {
                    showToast("카메라 권한이 필요합니다.")
                }
            }
            MEDIA_PERMISSION_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    openGallery()
                } else {
                    showToast("갤러리 권한이 필요합니다.")
                }
            }
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
                        showToast("사진을 가져올 수 없습니다.")
                    }
                }
                GALLERY_REQUEST_CODE -> {
                    val selectedImage: Uri? = data.data
                    if (selectedImage != null) {
                        imageView.setImageURI(selectedImage)
                        val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
                        uploadImage(bitmap)
                    } else {
                        showToast("이미지를 선택할 수 없습니다.")
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

            val apiService = (application as MyApplication).getApiService()

            apiService.predictImage("Bearer $jwtToken", body).enqueue(object : Callback<PredictionResult> {
                override fun onResponse(
                    call: Call<PredictionResult>,
                    response: Response<PredictionResult>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            Log.d("AiPredictActivity", "서버 응답 성공: $result")

                            val displayText = "Predicted Class: ${result.predictedLabel}\nDescription: ${result.description}"
                            resultTextView.text = displayText

                            val videoView: WebView = findViewById(R.id.videoWebView)
                            val videoUrl = "<iframe width=\"100%\" height=\"315\" src=\"${result.videoUrl}\" frameborder=\"0\" allowfullscreen></iframe>"
                            videoView.settings.javaScriptEnabled = true
                            videoView.loadData(videoUrl, "text/html", "utf-8")

                        } else {
                            Log.d("AiPredictActivity", "서버 응답이 null입니다.")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("AiPredictActivity", "응답 에러: ${response.code()}, $errorBody")
                    }
                }

                override fun onFailure(call: Call<PredictionResult>, t: Throwable) {
                    Log.d("AiPredictActivity", "서버 연결 실패: ${t.message}")
                }
            })
        } ?: run {
            Log.d("AiPredictActivity", "이미지 파일 변환 실패")
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

            Log.d("AiPredictActivity", "이미지 파일 변환 성공: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("AiPredictActivity", "이미지 파일 변환 중 오류 발생: ${e.message}")
            null
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
