package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.edit.ProfileEditFragmentDirections
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.follower.ProfileFollowerFragmentDirections
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.following.ProfileFollowingFragmentDirections
import com.example.instagram_clone_clean_architecture.feature.profile.presentation.view.main.ProfileMainFragmentDirections
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import com.example.library_base.presentation.navigation.NavigationManager
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class NavigationUseCaseTest {

    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    private lateinit var navigationUseCase: NavigationUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        navigationUseCase = NavigationUseCase(navigationManager, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct result when navigate to profileEditFragment from profileMainFragment`() {
        var result: Either<Unit, Failure>? = null

        // when
        val navDir = ProfileMainFragmentDirections.actionProfileMainFragmentToProfileEditFragment(1)
        val params = NavigationUseCase.Param(navDir)

        mainCoroutineRule.runBlockingTest {
            navigationUseCase(params) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return correct result when navigate to profileFollowerFragment from profileMainFragment`() {
        var result: Either<Unit, Failure>? = null

        // when
        val navDir = ProfileMainFragmentDirections.actionProfileMainFragmentToProfileFollowerFragment(1)
        val params = NavigationUseCase.Param(navDir)

        mainCoroutineRule.runBlockingTest {
            navigationUseCase(params) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return correct result when navigate to profileFollowingFragment from profileMainFragment`() {
        var result: Either<Unit, Failure>? = null

        // when
        val navDir = ProfileMainFragmentDirections.actionProfileMainFragmentToProfileFollowingFragment(1)
        val params = NavigationUseCase.Param(navDir)

        mainCoroutineRule.runBlockingTest {
            navigationUseCase(params) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return correct result when navigate to profileMainFragment from profileFollowerFragment`() {
        var result: Either<Unit, Failure>? = null

        // when
        val navDir = ProfileFollowerFragmentDirections.actionProfileFollowerFragmentToProfileMainFragment(1)
        val params = NavigationUseCase.Param(navDir)

        mainCoroutineRule.runBlockingTest {
            navigationUseCase(params) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return correct result when navigate to profileMainFragment from profileFollowingFragment`() {
        var result: Either<Unit, Failure>? = null

        // when
        val navDir = ProfileFollowingFragmentDirections.actionProfileFollowingFragmentToProfileMainFragment(1)
        val params = NavigationUseCase.Param(navDir)

        mainCoroutineRule.runBlockingTest {
            navigationUseCase(params) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(Unit)
    }

    @Test
    fun `should return correct result when navigate to profileMainFragment from profileEditFragment`() {
        var result: Either<Unit, Failure>? = null

        // when
        val navDir = ProfileEditFragmentDirections.actionProfileEditFragmentToProfileMainFragment(1)
        val params = NavigationUseCase.Param(navDir)

        mainCoroutineRule.runBlockingTest {
            navigationUseCase(params) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(Unit)
    }

}