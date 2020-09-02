package com.example.instagram_clone_clean_architecture.feature.post.domain.model

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.model.PostDomainModel
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File
import java.util.*

@RunWith(JUnit4::class)
class PostUploadDomainModelTest {

    @MockK(relaxed = true)
    internal lateinit var mockFile: Uri

    @Before
    fun setup() {
        MockKAnnotations.init(this)
    }

    @Test
    fun `isPostReady should return true when all fields are non-null value`() {
        val post = PostUploadDomainModel(imageFile = mockFile, description = "1", location = "1", date = Date(), belongUserId = 1)

        // expect
        post.isPostReady shouldBeEqualTo true
    }

    @Test
    fun `isPostReady should return false when imageFile is missing`() {
        val post = PostUploadDomainModel(description = "1", location = "1", date = Date(), belongUserId = 1)

        // expect
        post.isPostReady shouldBeEqualTo false
    }

    @Test
    fun `isPostReady should return false when description is missing`() {
        val post = PostUploadDomainModel(imageFile = mockFile, location = "1", date = Date(), belongUserId = 1)

        // expect
        post.isPostReady shouldBeEqualTo false
    }

    @Test
    fun `isPostReady should return false when location is missing`() {
        val post = PostUploadDomainModel(imageFile = mockFile, description = "1", date = Date(), belongUserId = 1)

        // expect
        post.isPostReady shouldBeEqualTo false
    }

    @Test
    fun `isPostReady should return false when date is missing`() {
        val post = PostUploadDomainModel(imageFile = mockFile, location = "1", description = "1", belongUserId = 1)

        // expect
        post.isPostReady shouldBeEqualTo false
    }

    @Test
    fun `isPostReady should return false when belongUserId is missing`() {
        val post = PostUploadDomainModel(imageFile = mockFile, location = "1", date = Date(), description = "1")

        // expect
        post.isPostReady shouldBeEqualTo false
    }
}