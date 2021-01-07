package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import android.graphics.Bitmap
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.app.domain.model.UserProfileUploadDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.extension.getJpegByteArray
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_test_utils.runBlockingTest
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class UpdateUserProfileUseCaseTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var profileRepository: ProfileRepository

    private lateinit var testUseCase: UpdateUserProfileUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        testUseCase = UpdateUserProfileUseCase(
            profileRepository,
            mainCoroutineRule.testDispatcher
        )
    }

    @Test
    fun `should return whatever profileRepository getBitmap return when it return failure`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val user = mockk<UserProfileUploadDomainModel>(relaxed = true)
        val param = mockk<UpdateUserProfileUseCase.Param>(relaxed = true)

        every { user.imageUri } returns mockk()
        every { param.userProfile } returns user
        coEvery { profileRepository.getBitmap(any()) } returns Either.Failure(Failure.ExternalImageDecodeFail)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ExternalImageDecodeFail)
    }

    @Test
    fun `should return whatever profileRepository cacheCompressedUploadImage return when it return failure`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val imageBitmap = mockk<Bitmap>(relaxed = true)
        val imageByteArray = ByteArray(10)
        val user = mockk<UserProfileUploadDomainModel>(relaxed = true)
        val param = mockk<UpdateUserProfileUseCase.Param>(relaxed = true)

        every { imageBitmap.getJpegByteArray(any()) } returns imageByteArray
        every { user.imageUri } returns mockk()
        every { param.userProfile } returns user
        coEvery { profileRepository.getBitmap(any()) } returns Either.Success(imageBitmap)
        coEvery { profileRepository.cacheCompressedUploadImage(any(), any()) } returns Either.Failure(Failure.ExternalImageDecodeFail)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.ExternalImageDecodeFail)
    }

    @Test
    fun `should return whatever profileRepository updateUserProfile return`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val imageBitmap = mockk<Bitmap>(relaxed = true)
        val imageByteArray = ByteArray(10)
        val cachedFile = mockk<File>(relaxed = true)
        val user = mockk<UserProfileUploadDomainModel>(relaxed = true)
        val param = mockk<UpdateUserProfileUseCase.Param>(relaxed = true)

        every { imageBitmap.getJpegByteArray(any()) } returns imageByteArray
        every { user.imageUri } returns mockk()
        every { param.userProfile } returns user
        coEvery { profileRepository.getBitmap(any()) } returns Either.Success(imageBitmap)
        coEvery { profileRepository.cacheCompressedUploadImage(any(), any()) } returns Either.Success(cachedFile)
        coEvery { profileRepository.updateUserProfile(any()) } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.NetworkConnection)
    }

    @Test
    fun `should return whatever profileRepository updateUserProfile return when userProfile imageUri is null`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val user = mockk<UserProfileUploadDomainModel>(relaxed = true)
        val param = mockk<UpdateUserProfileUseCase.Param>(relaxed = true)

        every { user.imageUri } returns null
        every { param.userProfile } returns user
        coEvery { profileRepository.updateUserProfile(any()) } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.NetworkConnection)
    }

    @Test
    fun `cachedFile delete should invoke no matter what profileRepository updateUserProfile return`() {
        var result: Either<UserDomainModel, Failure>? = null

        // given
        val imageBitmap = mockk<Bitmap>(relaxed = true)
        val imageByteArray = ByteArray(10)
        val cachedFile = mockk<File>(relaxed = true)
        val user = mockk<UserProfileUploadDomainModel>(relaxed = true)
        val param = mockk<UpdateUserProfileUseCase.Param>(relaxed = true)

        every { imageBitmap.getJpegByteArray(any()) } returns imageByteArray
        every { user.imageUri } returns mockk()
        every { param.userProfile } returns user
        coEvery { profileRepository.getBitmap(any()) } returns Either.Success(imageBitmap)
        coEvery { profileRepository.cacheCompressedUploadImage(any(), any()) } returns Either.Success(cachedFile)
        coEvery { profileRepository.updateUserProfile(any()) } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest {
            testUseCase(param) {
                result = it
            }
        }

        // expect
        verify(exactly = 1) { cachedFile.delete() }
    }

}