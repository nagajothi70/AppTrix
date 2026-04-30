package com.example.apptrix.ui.authentication.biometric

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import com.example.models.sealed.BiometricResult
import com.example.service.repository.BiometricInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class BiometricViewModel @Inject constructor(
    private val repo: BiometricInterface
) : ViewModel() {

    private val _state = MutableStateFlow<BiometricResult?>(null)
    val state = _state.asStateFlow()
    fun authenticate(activity: FragmentActivity) {
        repo.authenticate(activity) {
            _state.value = it
        }
    }
}
