package com.example.instagram_clone_clean_architecture.app.data.data_source

import android.app.Application
import android.content.SharedPreferences
import com.example.instagram_clone_clean_architecture.app.domain.data_source.LocalDataSource
import com.example.library_base.domain.exception.Failure
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class LocalDataSourceImplTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

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
    fun `should return failure when local login user name not found in share prefenrece`() {
        var result: Either<String, Failure>? = null

        // given
        every { application.getSharedPreferences(any(), any()) } returns sharePref
        every { sharePref.getInt(any(), any()) } returns -1

        // when
        mainCoroutineRule.runBlockingTest {
            result = localDataSource.getLocalLoginUserName()
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LocalAccountNotFound)
    }

    @Test
    fun `should return failure when local login user password not found in share prefenrece`() {
        var result: Either<String, Failure>? = null

        // given
        every { application.getSharedPreferences(any(), any()) } returns sharePref
        every { sharePref.getInt(any(), any()) } returns -1

        // when
        mainCoroutineRule.runBlockingTest {
            result = localDataSource.getLocalLoginUserPassword()
        }

        // expect
        result shouldBeEqualTo Either.Failure(Failure.LocalAccountNotFound)
    }

    @Test
    fun `should return correct type when update local user name in share preference`() {
        var result: Either<Unit, Failure>? = null
        val mockLocalUserName = "1"

        // given
        every { application.getSharedPreferences(any(), any()) } returns sharePref
        every { sharePref.edit() } returns sharePrefEditor
        every { sharePrefEditor.putString(any(), any()) } returns sharePrefEditor
        every { sharePrefEditor.commit() } returns true

        // when
        mainCoroutineRule.runBlockingTest {
            result = localDataSource.updateLocalLoginUserName(mockLocalUserName)
        }

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return correct type when update local user password in share preference`() {
        var result: Either<Unit, Failure>? = null
        val mockLocalUserPassword = "1"

        // given
        every { application.getSharedPreferences(any(), any()) } returns sharePref
        every { sharePref.edit() } returns sharePrefEditor
        every { sharePrefEditor.putString(any(), any()) } returns sharePrefEditor
        every { sharePrefEditor.commit() } returns true

        // when
        mainCoroutineRule.runBlockingTest {
            result = localDataSource.updateLocalLoginUserPassword(mockLocalUserPassword)
        }

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }

}