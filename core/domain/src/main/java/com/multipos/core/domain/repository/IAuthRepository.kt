package com.multipos.core.domain.repository

import com.multipos.core.domain.model.User
import com.multipos.core.domain.util.Result

interface IAuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun refreshToken(): Result<User>
    suspend fun logout(): Result<Unit>
    suspend fun getCurrentUser(): Result<User>
    suspend fun validateToken(): Result<Boolean>
}