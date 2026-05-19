package com.example.domain.model.user

data class User(
    val id: Int,
    val name: String,
    val photoUrl: String?, //TODO чем фото хранить?
    val bio: String = "",
)
//TODO подписки упражнения посты и тд в репозитории по обращению к id пользователя