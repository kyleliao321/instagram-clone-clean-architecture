package com.example.instagram_clone_clean_architecture.feature.profile.data.repository

import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.instagram_clone_clean_architecture.app.domain.data_source.RemoteDataSource
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.profile.domain.repository.ProfileRepository
import com.example.library_base.domain.exception.Failure
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

@RunWith(JUnit4::class)
class ProfileRepositoryImplTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var localDataSource: LocalDataSource

    @MockK(relaxed = true)
    internal lateinit var remoteDataSource: RemoteDataSource

    @MockK(relaxed = true)
    internal lateinit var cacheDataSource: CacheDataSource

    private lateinit var profileRepositoryImpl: ProfileRepository

    private val mockUserProfile = UserDomainModel(
        id = "mockUserId",
        userName = "mockUserName",
        name = "mockName",
        description = "mockDes",
        imageSrc = null,
        postNum = 0,
        followerNum = 0,
        followingNum = 0
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        profileRepositoryImpl = ProfileRepositoryImpl(
            localDataSource,
            remoteDataSource,
            cacheDataSource
        )
    }

    @Test
    fun `cleanUserLocalData should return whatever cacheDataSource cacheLoginUserProfile return when it return failure`() {
        // given
        var result: Either<Unit, Failure>? = null

        every { cacheDataSource.cacheAuthToken(any()) } returns Unit
        coEvery { cacheDataSource.cacheLoginUserProfile(any()) } returns Either.Failure(Failure.CacheNotFound)

        // when
        mainCoroutineRule.runBlockingTest {
            result = profileRepositoryImpl.cleanUserLocalData()
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.CacheNotFound)
    }

    @Test
    fun `cleanUserLocalData should return whatever localDataSource updateLocalLoginUserName return when it return failure`() {
        // given
        var result: Either<Unit, Failure>? = null

        every { cacheDataSource.cacheAuthToken(any()) } returns Unit
        coEvery { cacheDataSource.cacheLoginUserProfile(any()) } returns Either.Success(Unit)
        coEvery { localDataSource.updateLocalLoginUserName(any()) } returns Either.Failure(Failure.LocalAccountNotFound)

        // when
        mainCoroutineRule.runBlockingTest {
            result = profileRepositoryImpl.cleanUserLocalData()
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LocalAccountNotFound)
    }

    @Test
    fun `cleanUserLocalData should return whatever localDataSource updateLocalLoginUserPassword return when it return failure`() {
        // given
        var result: Either<Unit, Failure>? = null

        every { cacheDataSource.cacheAuthToken(any()) } returns Unit
        coEvery { cacheDataSource.cacheLoginUserProfile(any()) } returns Either.Success(Unit)
        coEvery { localDataSource.updateLocalLoginUserName(any()) } returns Either.Success(Unit)
        coEvery { localDataSource.updateLocalLoginUserPassword(any()) } returns Either.Failure(
            Failure.LocalAccountNotFound
        )

        // when
        mainCoroutineRule.runBlockingTest {
            result = profileRepositoryImpl.cleanUserLocalData()
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LocalAccountNotFound)
    }

    @Test
    fun `updateUserProfile should call cacheDataSource cacheLoginUserProfile and return correct result when remoteDataSource updateUserProfile return success`() {
        // given
        var result: Either<UserDomainModel, Failure>? = null

        coEvery { remoteDataSource.updateUserProfile(any()) } returns Either.Success(mockUserProfile)
        coEvery { cacheDataSource.cacheLoginUserProfile(any()) } returns Either.Success(Unit)

        // when
        mainCoroutineRule.runBlockingTest {
            result = profileRepositoryImpl.updateUserProfile(mockk())
        }

        // expect
        coVerify(exactly = 1) { cacheDataSource.cacheLoginUserProfile(any()) }
        result shouldBeEqualTo Either.Success(mockUserProfile)
    }

    @Test
    fun `updateUserProfile should return whatever cacheDataSource cacheLoginUserProfile return when it return failure`() {
        // given
        var result: Either<UserDomainModel, Failure>? = null

        coEvery { remoteDataSource.updateUserProfile(any()) } returns Either.Success(mockUserProfile)
        coEvery { cacheDataSource.cacheLoginUserProfile(any()) } returns Either.Failure(Failure.CacheNotFound)

        // when
        mainCoroutineRule.runBlockingTest {
            result = profileRepositoryImpl.updateUserProfile(mockk())
        }

        // expect
        coVerify(exactly = 1) { cacheDataSource.cacheLoginUserProfile(any()) }
        result shouldBeEqualTo Either.Failure(Failure.CacheNotFound)
    }


}