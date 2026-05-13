package com.example.apptrix.ui.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.ui.EmailVerificationState
import com.example.models.ui.ForgotPasswordState
import com.example.models.ui.LoginState
import com.example.models.ui.SignupState
import com.example.service.repository.AuthInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repo: AuthInterface
) : ViewModel() {

    private val _state = MutableStateFlow(EmailVerificationState())
    val state = _state.asStateFlow()
    private val _forgotState = MutableStateFlow(ForgotPasswordState())
    val forgotState = _forgotState.asStateFlow()
    private val _loginState = MutableStateFlow(LoginState())
    val loginState = _loginState.asStateFlow()
    private val _signupState = MutableStateFlow(SignupState())
    val signupState = _signupState.asStateFlow()

    init {
        startTimer()
    }

    fun signup(
        username: String,
        email: String,
        phone: String,
        password: String,
        deviceId: String
    ) {

        val validation = validateSignup(username, email, phone, password)

        if (validation.isNotEmpty()) {
            _signupState.value = SignupState(error = validation)
            return
        }

        _signupState.value = SignupState(isLoading = true)

        repo.signup(username, email, phone, password, deviceId) { result ->

            result.onSuccess {
                _signupState.value = SignupState(isSuccess = true)
            }

            result.onFailure {
                _signupState.value = SignupState(
                    error = it.message ?: "Signup failed"
                )
            }
        }
    }

    fun login(email: String, password: String, deviceId: String) {

        val validation = validateLogin(email, password)

        if (validation.isNotEmpty()) {
            _loginState.value = LoginState(error = validation)
            return
        }

        _loginState.value = LoginState(isLoading = true)

        repo.login(email, password, deviceId) { result ->

            result.onSuccess {
                _loginState.value = LoginState(isSuccess = true)
            }

            result.onFailure {
                _loginState.value = LoginState(
                    error = it.message ?: "Error"
                )
            }
        }
    }
    fun sendReset(email: String) {

        if (email.isEmpty()) {
            _forgotState.value = ForgotPasswordState(
                message = "Enter email"
            )
            return
        }

        _forgotState.value = ForgotPasswordState(isLoading = true)

        repo.sendPasswordReset(email) { result ->

            result.onSuccess {
                _forgotState.value = ForgotPasswordState(
                    isSuccess = true,
                    message = it
                )
            }

            result.onFailure {
                _forgotState.value = ForgotPasswordState(
                    message = it.message ?: "Error"
                )
            }
        }
    }
    fun resendEmail() {
        _state.value = _state.value.copy(isSending = true)

        repo.resendVerification { result ->
            _state.value = _state.value.copy(isSending = false)

            result.onSuccess {
                _state.value = _state.value.copy(message = it)
                startTimer()
            }

            result.onFailure {
                _state.value = _state.value.copy(message = it.message ?: "Error")
            }
        }
    }

    private fun startTimer() {
        viewModelScope.launch {
            _state.value = _state.value.copy(canResend = false, timer = 90)

            var time = 90
            while (time > 0) {
                delay(1000)
                time--
                _state.value = _state.value.copy(timer = time)
            }

            _state.value = _state.value.copy(canResend = true)
        }
    }

    fun validateLogin(email: String, password: String): String {
      return repo.validateLogin(email = email , password = password)
    }

    fun validateSignup(
        username: String,
        email: String,
        phone: String,
        password: String
    ): String {
        return repo.validateSignup(username = username, email = email, phone = phone, password = password)
    }
}
