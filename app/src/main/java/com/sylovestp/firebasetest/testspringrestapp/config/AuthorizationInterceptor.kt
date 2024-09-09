import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import android.util.Log

class AuthorizationInterceptor(private val token: String) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        // 원래의 요청 가져오기
        val originalRequest: Request = chain.request()

        // 로그 추가: 원래의 요청 URL 및 헤더 확인
        Log.d("RetrofitRequest", "Request Headers: Authorization: Bearer $token")
        Log.d("AuthorizationInterceptor", "Original Request URL: ${originalRequest.url}")
        Log.d("AuthorizationInterceptor", "Original Request Headers: ${originalRequest.headers}")

        // 새로운 요청 만들기 (Authorization 헤더 추가)
        val newRequest: Request = originalRequest.newBuilder()
            .addHeader("Authorization", "Bearer $token")
            .build()

        // 요청을 서버로 진행 (다음 단계로 전달)
        val response = chain.proceed(newRequest)

        // 로그 추가: 응답 코드 및 헤더 확인
        Log.d("AuthorizationInterceptor", "Response Code: ${response.code}")
        Log.d("AuthorizationInterceptor", "Response Headers: ${response.headers}")


        // 최종 응답 반환
        return response
    }
}
