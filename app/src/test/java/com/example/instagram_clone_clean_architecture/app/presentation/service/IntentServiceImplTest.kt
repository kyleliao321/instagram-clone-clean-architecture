package com.example.instagram_clone_clean_architecture.app.presentation.service

import androidx.appcompat.app.AppCompatActivity
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class IntentServiceImplTest {

    @MockK(relaxed = true)
    internal lateinit var activity: AppCompatActivity

    private lateinit var intentService: IntentServiceImpl

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        intentService = IntentServiceImpl()

        intentService.init(activity)
    }

    @Test
    fun `should return correct type when the camera intent is resolvable`() {
        // when
        val result = intentService.openCamera()

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return failure when the camera intent is unresolvable`() {
        // given
        every { activity.startActivityForResult(any(), any()) } throws Exception()

        // when
        val result = intentService.openCamera()

        // expect
        result shouldBeEqualTo Either.Failure(Failure.CameraServiceFail)
    }

    @Test
    fun `should return correct type when the photo gallery intent is resolvable`() {
        // when
        val result = intentService.openPhotoGallery()

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return failure when the photo gallery intent is unresolvable`() {
        // given
        every { activity.startActivityForResult(any(), any()) } throws Exception()

        // when
        val result = intentService.openPhotoGallery()

        // expect
        result shouldBeEqualTo Either.Failure(Failure.PhotoGalleryServiceFail)
    }

}