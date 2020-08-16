package com.example.instagram_clone_clean_architecture.app.domain.model

data class UserDomainModel(
    val id: Int,
    var name: String,
    var userName: String,
    var imageSrc: String? = null,
    var description: String? = null,
    var postNum: Int,
    var followingNum: Int,
    var followerNum: Int
) {

    enum class Type {
        SAME, REMOVE, FOLLOW
    }

    inline fun getType(userProfile: UserDomainModel, followingList: List<UserDomainModel>) : Type {

        if (userProfile == this) {
            return Type.SAME
        }

        return when (this) {
            in followingList -> Type.REMOVE
            !in followingList -> Type.FOLLOW
            else -> throw IllegalStateException("Conditions should be exhausted")
        }
    }

}