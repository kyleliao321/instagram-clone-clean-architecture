package com.example.instagram_clone_clean_architecture.app.domain.model

import android.net.Uri
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.*

class PostUploadDomainModelTest {

    @MockK(relaxed = true)
    internal lateinit var mockFile: Uri

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `isPostReady should return true when all fields are non-null value`() {
        val post =
            PostUploadDomainModel(
                imageFile = mockFile,
                description = "1",
                location = "1",
                date = Date(),
                belongUserId = "mockUserId"
            )

        // expect
        post.isPostReady shouldBeEqualTo true
    }

    @Test
    fun `isPostReady should return false when imageFile is missing`() {
        val post =
            PostUploadDomainModel(
                description = "1",
                location = "1",
                date = Date(),
                belongUserId = "mockUserId"
            )

        // expect
        post.isPostReady shouldBeEqualTo false
    }

    @Test
    fun `isPostReady should return true when description is missing`() {
        val post =
            PostUploadDomainModel(
                imageFile = mockFile,
                location = "1",
                date = Date(),
                belongUserId = "mockUserId"
            )

        // expect
        post.isPostReady shouldBeEqualTo true
    }

    @Test
    fun `isPostReady should return true when location is missing`() {
        val post =
            PostUploadDomainModel(
                imageFile = mockFile,
                description = "1",
                date = Date(),
                belongUserId = "mockUserId"
            )

        // expect
        post.isPostReady shouldBeEqualTo true
    }

    @Test
    fun `isPostReady should return true when date is missing`() {
        val post =
            PostUploadDomainModel(
                imageFile = mockFile,
                location = "1",
                description = "1",
                belongUserId = "mockUserId"
            )

        // expect
        post.isPostReady shouldBeEqualTo true
    }

    @Test
    fun `isPostReady should return false when belongUserId is missing`() {
        val post =
            PostUploadDomainModel(
                imageFile = mockFile,
                location = "1",
                date = Date(),
                description = "1"
            )

        // expect
        post.isPostReady shouldBeEqualTo false
    }
}