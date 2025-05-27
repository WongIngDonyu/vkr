package com.example.vkr

import com.example.vkr.data.repository.AuthRepository
import com.example.vkr.presentation.screens.signup.SignUpViewModel
import junit.framework.TestCase.fail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*
import retrofit2.Response

@ExperimentalCoroutinesApi
class SignUpViewModelTest {
    private lateinit var viewModel: SignUpViewModel
    private val repository: AuthRepository = mock()

    @Before
    fun setup() {
        Dispatchers.setMain(StandardTestDispatcher())
        viewModel = SignUpViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun sign_up_with_error() {
        viewModel.name = "Я"
        viewModel.nickname = "WW"
        viewModel.phone = "7916089671"
        viewModel.password = "123456789"
        viewModel.confirmPassword = "123456789!"
        viewModel.signUp()
        if (viewModel.nameErrorText.isEmpty()) {
            println("Ошибка: не выведено сообщение об ошибке имени")
            fail("Ожидалась ошибка в поле Имя")
        } else {
            println("Ошибка имени: ${viewModel.nameErrorText}")
        }

        if (viewModel.nicknameErrorText.isEmpty()) {
            println("Ошибка: не выведено сообщение об ошибке никнейма")
            fail("Ожидалась ошибка в поле Никнейм")
        } else {
            println("Ошибка никнейма: ${viewModel.nicknameErrorText}")
        }

        if (viewModel.phoneErrorText.isEmpty()) {
            println("Ошибка: не выведено сообщение об ошибке телефона")
            fail("Ожидалась ошибка в поле Телефон")
        } else {
            println("Ошибка телефона: ${viewModel.phoneErrorText}")
        }

        if (viewModel.passwordErrorText.isEmpty()) {
            println("Ошибка: не выведено сообщение об ошибке пароля")
            fail("Ожидалась ошибка в поле Пароль")
        } else {
            println("Ошибка пароля: ${viewModel.passwordErrorText}")
        }

        if (viewModel.confirmPasswordErrorText.isEmpty()) {
            println("Ошибка: не выведено сообщение об ошибке подтверждения пароля")
            fail("Ожидалась ошибка в поле Подтверждение пароля")
        } else {
            println("Ошибка подтверждения пароля: ${viewModel.confirmPasswordErrorText}")
        }
    }

    @Test
    fun sign_up_with_valid_data() = runTest {
        viewModel.name = "Георгий"
        viewModel.nickname = "WongIngDonyu"
        viewModel.phone = "79160796711"
        viewModel.password = "Secure123"
        viewModel.confirmPassword = "Secure123"
        val response = Response.success(ResponseBody.create(null, "Success"))
        whenever(repository.register(any())).thenReturn(response)
        viewModel.signUp()
        advanceUntilIdle()
        assert(viewModel.navigateToLogin)
    }
}