package com.sylovestp.firebasetest.testspringrestapp

import android.Manifest
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
import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import com.sylovestp.firebasetest.testspringrestapp.dto.PredictionResult
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
    private val CAMERA_REQUEST_CODE = 100
    private val GALLERY_REQUEST_CODE = 200
    private val CAMERA_PERMISSION_CODE = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("MainActivitySpring", "onCreate 실행됨")
        setContentView(R.layout.activity_ai_predict)

        // MyApplication 초기화
        val myApplication = applicationContext as MyApplication
        myApplication.initialize(this)  // apiService 초기화

        imageView = findViewById(R.id.imageView)
        resultTextView = findViewById(R.id.resultTextView)
        val buttonCamera: Button = findViewById(R.id.buttonCamera)
        val buttonGallery: Button = findViewById(R.id.buttonGallery)

        buttonCamera.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        buttonGallery.setOnClickListener {
            val galleryIntent =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, GALLERY_REQUEST_CODE)
        }
    }

    // 권한 확인 및 요청 메서드
    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 카메라 권한이 없는 경우, 권한 요청
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // 이전에 권한 요청을 거부한 경우, 왜 권한이 필요한지 설명하고 다시 요청
                Toast.makeText(this, "카메라 접근 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }

            // 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            // 권한이 이미 있는 경우 카메라 열기
            openCamera()
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
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                // 권한이 허용된 경우 카메라 열기
                openCamera()
            } else {
                // 권한이 거부된 경우 안내 메시지
                Toast.makeText(this, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 갤러리 및 카메라에서 이미지를 받아 처리하는 메서드
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

            // MyApplication 인스턴스에서 apiService를 가져옴
            val apiService = (application as MyApplication).getApiService()

            apiService.predictImage(body).enqueue(object : Callback<PredictionResult> {
                override fun onResponse(
                    call: Call<PredictionResult>,
                    response: Response<PredictionResult>
                ) {
                    if (response.isSuccessful) {
                        val result = response.body()
                        if (result != null) {
                            Log.d("MainActivitySpring", "서버 응답 성공: $result")

                            val displayText = """
                                Predicted Class: ${result.predictedLabel}
                            """.trimIndent()

                            resultTextView.text = displayText
                        } else {
                            Log.d("MainActivitySpring", "서버 응답이 null입니다.")
                        }
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.d("MainActivitySpring", "응답 에러: ${response.code()}, $errorBody")
                    }
                }

                override fun onFailure(call: Call<PredictionResult>, t: Throwable) {
                    Log.d("MainActivitySpring", "서버 연결 실패: ${t.message}")
                }
            })
        } ?: run {
            Log.d("MainActivitySpring", "이미지 파일 변환 실패")
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

            Log.d("MainActivitySpring", "이미지 파일 변환 성공: ${file.absolutePath}")
            file
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("MainActivitySpring", "이미지 파일 변환 중 오류 발생: ${e.message}")
            null
        }
    }
}
