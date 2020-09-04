package com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase

import com.example.library_base.domain.exception.Failure
import com.example.library_test_utils.CoroutineTestRule
import com.example.library_base.domain.utility.Either
import com.example.library_test_utils.runBlockingTest
import com.example.library_base.presentation.navigation.NavigationManager
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.amshove.kluent.shouldBeEqualTo
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class NavigationUseCaseTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    private lateinit var navigationUseCase: NavigationUseCase

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        navigationUseCase = NavigationUseCase(navigationManager, mainCoroutineRule.testDispatcher)
    }

    @Test
    fun `should return correct result when navigate`() {
        var result: Either<Unit, Failure>? = null

        // when
        val params = mockk<NavigationUseCase.Param>(relaxed = true)

        mainCoroutineRule.runBlockingTest {
            navigationUseCase(params) {
                result = it
            }
        }

        result shouldBeEqualTo Either.Success(Unit)
    }
}