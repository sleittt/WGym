package com.example.presentation.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    var uiState by mutableStateOf(RegisterUiState())
        private set

    fun onNameChange(name: String) {
        uiState = uiState.copy(name = name, error = null)
    }

    fun onEmailChange(email: String) {
        uiState = uiState.copy(email = email, error = null)
    }

    fun onPasswordChange(password: String) {
        uiState = uiState.copy(password = password, error = null)
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        uiState = uiState.copy(confirmPassword = confirmPassword, error = null)
    }

    fun register() {
        val current = uiState
        if (current.name.isBlank() || current.email.isBlank() || current.password.isBlank()) {
            uiState = current.copy(error = "Заполните все поля")
            return
        }
        if (current.password.length < 4) {
            uiState = current.copy(error = "Пароль минимум 4 символа")
            return
        }
        if (current.password != current.confirmPassword) {
            uiState = current.copy(error = "Пароли не совпадают")
            return
        }

        viewModelScope.launch {
            uiState = current.copy(isLoading = true, error = null)
            authRepository.register(current.name, current.email, current.password)
                .onSuccess {
                    uiState = uiState.copy(isLoading = false, isSuccess = true)
                }
                .onFailure { e ->
                    uiState = uiState.copy(isLoading = false, error = e.message ?: "Ошибка")
                }
        }
    }
}

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: String? = null
)
