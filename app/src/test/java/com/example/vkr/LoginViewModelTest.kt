package com.example.vkr

import android.app.Application
import com.example.vkr.data.repository.AuthRepository
import com.example.vkr.data.repository.TeamRepository
import com.example.vkr.presentation.screens.login.LoginViewModel
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

@ExperimentalCoroutinesApi
class LoginViewModelTest {
    private lateinit var viewModel: LoginViewModel
    private val repository: AuthRepository = mock()
    private val teamRepository: TeamRepository = mock()
    private val application = mock<Application>()

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = LoginViewModel(application, repository, teamRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun login_with_invalid_phone() {
        viewModel.phone = "7916089671"
        viewModel.password = "Secure123"
        viewModel.onLoginClick()
        if (!viewModel.phoneError) {
            println("Ошибка: phoneError = false, номер из 10 цифр прошёл валидацию")
            fail("Ожидалась ошибка в поле Телефон: номер должен содержать ровно 11 цифр")
        } else {
            println("Номер некорректный")
        }
    }

    @Test
    fun login_with_valid_data() = runTest {
        viewModel.phone = "79160796711"
        viewModel.password = "Secure123"
        whenever(repository.login(any(), any())).thenReturn(true)
        whenever(repository.loadAndStoreUserByPhone(any())).thenReturn(true)
        viewModel.onLoginClick()
        advanceUntilIdle()
        if (!viewModel.navigateToHome) {
            println("Ошибка: navigateToHome не стал true после успешного логина")
            fail("navigateToHome должен быть true при успешном логине")
        } else {
            println("Логин успешен")
        }
    }

    @Test
    fun login_with_wrong_pass() = runTest {
        viewModel.phone = "79160796711"
        viewModel.password = "Secure"
        whenever(repository.login(any(), any())).thenReturn(false)
        viewModel.onLoginClick()
        advanceUntilIdle()
        if (!viewModel.loginError) {
            println("Ошибка: loginError не установлен при неправильном пароле")
            fail("Ожидалась ошибка логина: loginError должен быть true при неверных данных")
        } else {
            println("Логин не успешный")
        }
    }
}