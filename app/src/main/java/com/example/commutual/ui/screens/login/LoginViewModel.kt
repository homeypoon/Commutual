package com.example.commutual.ui.screens.login

import androidx.compose.runtime.mutableStateOf
import com.example.commutual.HOME_SCREEN
import com.example.commutual.SIGN_UP_SCREEN
import com.example.commutual.common.ext.isValidEmail
import com.example.commutual.common.snackbar.SnackbarManager
import com.example.commutual.model.service.AccountService
import com.example.commutual.model.service.LogService
import com.example.commutual.ui.screens.CommutualViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import com.example.commutual.R.string as AppText


@HiltViewModel
class LoginViewModel @Inject constructor(
  private val accountService: AccountService,
  logService: LogService
) : CommutualViewModel(logService) {
  var uiState = mutableStateOf(LoginUiState())
    private set

  private val email
    get() = uiState.value.email
  private val password
    get() = uiState.value.password

  fun onEmailChange(newValue: String) {
    uiState.value = uiState.value.copy(email = newValue)
  }

  fun onPasswordChange(newValue: String) {
    uiState.value = uiState.value.copy(password = newValue)
  }

  fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
    if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      return
    }

    if (password.isBlank()) {
      SnackbarManager.showMessage(AppText.empty_password_error)
      return
    }

    launchCatching {
      accountService.signIn(email, password)
      openAndPopUp(HOME_SCREEN, HOME_SCREEN)
    }
  }

  fun onRegisterClick(openAndPopUp: (String, String) -> Unit) {
    openAndPopUp(SIGN_UP_SCREEN, SIGN_UP_SCREEN)
  }

  fun onForgotPasswordClick() {
    if (email.isBlank()) {
      SnackbarManager.showMessage(AppText.enter_email)
      return
    } else if (!email.isValidEmail()) {
      SnackbarManager.showMessage(AppText.email_error)
      return
    }

    launchCatching {
      accountService.sendRecoveryEmail(email)
      SnackbarManager.showMessage(AppText.recovery_email_sent)
    }
  }
}
