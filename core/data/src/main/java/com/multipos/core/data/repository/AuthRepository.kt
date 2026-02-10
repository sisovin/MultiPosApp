package com.multipos.core.data.repository

import com.multipos.core.data.mapper.toDomain
import com.multipos.core.data.remote.dto.LoginRequestDto
import com.multipos.core.data.remote.dto.RefreshTokenRequestDto
import com.multipos.core.data.remote.service.AuthService
import com.multipos.core.domain.model.User
import com.multipos.core.domain.repository.IAuthRepository
import com.multipos.core.domain.util.Result
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager
) : IAuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = try {
        val response = authService.login(
            LoginRequestDto(email = email, password = password)
        )
        val user = response.user.toDomain()
        tokenManager.saveTokens(response.accessToken, response.refreshToken)
        Result.Success(user)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun refreshToken(): Result<User> = try {
        val refreshToken = tokenManager.getRefreshToken()
            ?: return Result.Error(Exception("No refresh token available"))

        val response = authService.refreshToken(
            RefreshTokenRequestDto(refreshToken = refreshToken)
        )
        val user = response.user.toDomain()
        tokenManager.saveTokens(response.accessToken, response.refreshToken)
        Result.Success(user)
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun logout(): Result<Unit> = try {
        val token = tokenManager.getAccessToken()
        if (token != null) {
            authService.logout("Bearer $token")
        }
        tokenManager.clearTokens()
        Result.Success(Unit)
    } catch (e: Exception) {
        tokenManager.clearTokens()
        Result.Success(Unit) // Consider logout successful even if API call fails
    }

    override suspend fun getCurrentUser(): Result<User> = try {
        val token = tokenManager.getAccessToken()
            ?: return Result.Error(Exception("No access token available"))

        val userDto = authService.getCurrentUser("Bearer $token")
        Result.Success(userDto.toDomain())
    } catch (e: Exception) {
        Result.Error(e)
    }

    override suspend fun validateToken(): Result<Boolean> = try {
        val token = tokenManager.getAccessToken()
            ?: return Result.Success(false)

        val response = authService.validateToken("Bearer $token")
        Result.Success(response.data ?: false)
    } catch (e: Exception) {
        Result.Success(false)
    }
}