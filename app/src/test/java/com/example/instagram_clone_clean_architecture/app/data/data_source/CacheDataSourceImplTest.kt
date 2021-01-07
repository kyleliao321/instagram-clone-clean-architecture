package com.example.instagram_clone_clean_architecture.app.data.data_source

import android.net.Uri
import com.example.instagram_clone_clean_architecture.app.domain.data_source.CacheDataSource
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class CacheDataSourceImplTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    private lateinit var cacheDataSource: CacheDataSource

    @Before
    fun setup() {
        cacheDataSource = CacheDataSourceImpl()
    }

    @Test
    fun `should return correct value and clean up cache image when consumeCachedSelectedImageUri invoke`() {
        var firstResult: Either<Uri, Failure>? = null
        var secondResult: Either<Uri, Failure>? = null
        val mockImageUri = mockk<Uri>()

        // when
        mainCoroutineRule.runBlockingTest {
            cacheDataSource.cacheUserSelectedImageUri(mockImageUri)
            firstResult = cacheDataSource.consumeCachedSelectedImageUri()
            secondResult = cacheDataSource.consumeCachedSelectedImageUri()
        }

        // expect
        firstResult shouldBeEqualTo Either.Success(mockImageUri)
        secondResult shouldBeEqualTo Either.Failure(Failure.CacheNotFound)
    }

}