package com.example.library_base.presentation.navigation

import androidx.navigation.NavDirections
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.amshove.kluent.mock
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class NavigationManagerTest {

    private lateinit var navigationManager: NavigationManager

    @MockK
    internal lateinit var navEventCallbackListener: (NavDirections) -> Unit

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        navigationManager = NavigationManager()
    }

    @Test
    fun `onNavEvent should invoke navEventCallbackListener`() {
        val navDir = mock<NavDirections>()

        every { navEventCallbackListener(navDir) } returns Unit

        navigationManager.setNavEventCallbackListener(navEventCallbackListener)

        navigationManager.onNavEvent(navDir)

        verify { navEventCallbackListener(navDir) }
    }
}