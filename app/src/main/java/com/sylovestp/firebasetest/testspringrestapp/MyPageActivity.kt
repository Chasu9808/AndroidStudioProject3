package com.sylovestp.firebasetest.testspringrestapp

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.sylovestp.firebasetest.testspringrestapp.retrofit.MyApplication
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageActivity : AppCompatActivity() {

    private lateinit var deleteButton: Button
    private lateinit var myApplication: MyApplication

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_page)


        myApplication = application as MyApplication


        deleteButton = findViewById(R.id.button_delete_account)

        deleteButton.setOnClickListener {
            if (isNetworkAvailable(this)) {
                deleteAccount()
            } else {
                Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteAccount() {
        val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val token = sharedPreferences.getString("jwt_token", null)

        if (token != null) {
            val bearerToken = "Bearer $token"


            myApplication.getApiService().deleteAccount(bearerToken)
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            val editor = sharedPreferences.edit()
                            editor.clear()
                            editor.apply()

                            Toast.makeText(this@MyPageActivity, "Account deleted successfully", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@MyPageActivity, MainActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            finish()
                        } else {
                            Log.e("RetrofitResponse", "Response 실패: ${response.errorBody()?.string()} - Code: ${response.code()}")
                            Toast.makeText(this@MyPageActivity, "Failed to delete account: ${response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Log.e("DeleteAccount", "Error: ${t.message}")
                        Toast.makeText(this@MyPageActivity, "Error deleting account", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(this, "Token not found, please login again", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@MyPageActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return when {
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            else -> false
        }
    }
}
