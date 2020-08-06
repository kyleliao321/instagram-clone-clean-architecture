package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.instagram_clone_clean_architecture.feature.profile.presentation.edit.ProfileEditFragmentDirections
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_base.domain.utility.runBlockingTest
import com.example.library_base.presentation.navigation.NavigationManager
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
class NavigateToMainProfileUseCaseTest {
    @get:Rule
    val mainCoroutineRule = CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    private lateinit var navigateToMainProfileUseCase: NavigateToMainProfileUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        navigateToMainProfileUseCase = NavigateToMainProfileUseCase(navigationManager, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct result when navigate to edit profile fragment`() {
        var result: Either<Unit, Failure>? = null

        // given
        every { navigationManager.onNavEvent(any()) } returns Unit

        // when
        val navDir = ProfileEditFragmentDirections.actionProfileEditFragmentToProfileMainFragment(1)
        val params = NavigateToMainProfileUseCase.Param(navDir)

        mainCoroutineRule.runBlockingTest { navigateToMainProfileUseCase(params) { result = it } }

        // expect
        result shouldBeEqualTo Either.Success(Unit)
    }
}