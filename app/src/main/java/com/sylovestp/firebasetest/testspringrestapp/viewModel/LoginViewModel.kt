import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sylovestp.firebasetest.testspringrestapp.repository.LoginRepository
import kotlinx.coroutines.launch

class LoginViewModel(private val loginRepository: LoginRepository) : ViewModel() {

    private val _loginUser = MutableLiveData<String?>()  // 로그인한 사용자 ID 또는 null
    val loginUser: LiveData<String?> get() = _loginUser

    fun login(username: String, password: String) {
        // 코루틴 내에서 suspend 함수를 호출
        viewModelScope.launch {
            // loginRepository.login()은 Boolean을 반환하므로 성공 여부를 확인
            val loginSuccessful = loginRepository.login(username, password)

            if (loginSuccessful) {
                _loginUser.postValue(username)  // 로그인 성공 시 사용자 이름을 저장
            } else {
                _loginUser.postValue(null)  // 로그인 실패 시 null 설정
            }
        }
    }
}
