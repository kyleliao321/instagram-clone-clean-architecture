package com.example.instagram_clone_clean_architecture.app.data.data_source

import android.app.Application
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.File

@RunWith(JUnit4::class)
class LocalDataSourceImplTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var application: Application

    @MockK(relaxed = true)
    internal lateinit var sharePref: SharedPreferences

    @MockK(relaxed = true)
    internal lateinit var sharePrefEditor: SharedPreferences.Editor

    private lateinit var localDataSource: LocalDataSource

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        localDataSource = LocalDataSourceImpl()
        localDataSource.init(application)
    }

    @Test
    fun `should return failure when local login user not found in share preference`() {
        var result: Either<Int, Failure>? = null

        // given
        every { application.getSharedPreferences(any(), any()) } returns sharePref
        every { sharePref.getInt(any(), any()) } returns -1

        // when
        mainCoroutineRule.runBlockingTest {
            result = localDataSource.getLocalLoginUserId()
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LocalAccountNotFound)
    }

    @Test
    fun `should return correct type when local login user found in share preference`() {
        var result: Either<Int, Failure>? = null
        val mockLocalUserId = 1

        // given
        every { application.getSharedPreferences(any(), any()) } returns sharePref
        every { sharePref.getInt(any(), any()) } returns mockLocalUserId

        // when
        mainCoroutineRule.runBlockingTest {
            result = localDataSource.getLocalLoginUserId()
        }

        // expect
        result shouldBeEqualTo Either.Success(mockLocalUserId)
    }

    @Test
    fun `should return correct type when update local user in share preference`() {
        var result: Either<Unit, Failure>? = null
        val mockLocalUser = 1

        // given
        every { application.getSharedPreferences(any(), any()) } returns sharePref
        every { sharePref.edit() } returns sharePrefEditor
        every { sharePrefEditor.putInt(any(), any()) } returns sharePrefEditor
        every { sharePrefEditor.commit() } returns true

        // when
        mainCoroutineRule.runBlockingTest {
            result = localDataSource.updateLocalLoginUserId(mockLocalUser)
        }

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return correct value and clean up cache image when consumeLoadedImage invoke`() {
        var firstResult: Either<Uri?, Failure>? = null
        var secondResult: Either<Uri?, Failure>? = null
        val mockImageUri = mockk<Uri>()

        // when
        mainCoroutineRule.runBlockingTest {
            localDataSource.loadImage(mockImageUri)
            firstResult = localDataSource.consumeLoadedImage()
            secondResult = localDataSource.consumeLoadedImage()
        }

        // expect
        firstResult shouldBeEqualTo Either.Success(mockImageUri)
        secondResult shouldBeEqualTo Either.Success(null)
    }


}