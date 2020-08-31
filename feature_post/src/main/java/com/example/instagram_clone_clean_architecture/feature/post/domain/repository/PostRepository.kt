package com.example.instagram_clone_clean_architecture.feature.post.domain.repository

import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import java.io.File

interface PostRepository {

    suspend fun getUserSelectedImage(): Either<File, Failure>

}