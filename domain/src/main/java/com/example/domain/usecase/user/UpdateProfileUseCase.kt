package com.example.domain.usecase.user

import com.example.domain.model.user.User
import com.example.domain.repository.UserRepository
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(
        name: String,
        photoUrl: String
    ): Result<User>{
       val currentUser = userRepository.getCurrentUser()
           ?: return Result.failure(IllegalStateException("Аккаунт не найден"))
       if (name.isBlank()){
           return Result.failure(IllegalArgumentException("Имя не может быть пустым"))
       }
        if (name.length > 50){
            return Result.failure(IllegalArgumentException("Имя слишком большое"))
        }
        return try {
            val updatedUser = userRepository.updateProfile(
                currentUser.id,
                name,
                photoUrl
            )
            Result.success(updatedUser)
        } catch (e : Exception){
            Result.failure(e)
        }
    }
}