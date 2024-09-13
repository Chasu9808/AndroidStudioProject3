import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import android.util.Log

class AuthorizationInterceptor(private val token: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest: Request = chain.request()

        Log.d("RetrofitRequest", "Request Headers: Authorization: Bearer $token")
        Log.d("AuthorizationInterceptor", "Original Request URL: ${originalRequest.url}")
        Log.d("AuthorizationInterceptor", "Original Request Headers: ${originalRequest.headers}")

        val newRequest: Request = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        val response = chain.proceed(newRequest)

        Log.d("AuthorizationInterceptor", "Response Code: ${response.code}")
        Log.d("AuthorizationInterceptor", "Response Headers: ${response.headers}")


        return response
    }
}
