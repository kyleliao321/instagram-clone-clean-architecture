package com.example.instagram_clone_clean_architecture.feature.profile.presentation.main

import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserPostUseCase
import com.example.instagram_clone_clean_architecture.feature.profile.domain.usecase.GetUserProfileUseCase
import com.example.library_base.presentation.navigation.NavigationManager
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class ProfileMainViewModelTest {

    @MockK(relaxed = true)
    internal lateinit var navigationManager: NavigationManager

    @MockK
    internal lateinit var getUserProfileUseCase: GetUserProfileUseCase

    @MockK
    internal lateinit var getUserPostUseCase: GetUserPostUseCase

    private val mainThreadSurrogate = newSingleThreadContext("TestThread")

    private lateinit var testViewModel: ProfileMainViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        Dispatchers.setMain(mainThreadSurrogate)

        testViewModel = ProfileMainViewModel(
            navigationManager,
            getUserProfileUseCase,
            getUserPostUseCase,
            mainThreadSurrogate
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun `should invoke getUserProfileUseCase and  when loadData`() {
        coEvery { getUserPostUseCase(any(), any()) } returns Unit
        coEvery { getUserProfileUseCase(any(), any()) } returns Unit

        testViewModel.loadDate()

        coVerify { getUserProfileUseCase(any(), any()) }
        coVerify { getUserPostUseCase(any(), any()) }
    }

    @Test
    fun `verify ViewState when getUserProfile returns null`() {
        // given
    }

}