package com.example.instagram_clone_clean_architecture.feature.search.presentation.view.search

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.example.instagram_clone_clean_architecture.app.domain.model.UserDomainModel
import com.example.instagram_clone_clean_architecture.feature.search.domain.repository.SearchRepository
import com.example.instagram_clone_clean_architecture.feature.search.domain.usecase.GetUserProfileListUseCase
import com.example.library_base.domain.exception.Failure
import com.example.library_base.domain.utility.Either
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_test_utils.runBlockingTest
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class SearchViewModelTest {

    @get:Rule
    val mainCoroutineRule = com.example.library_test_utils.CoroutineTestRule()

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @MockK(relaxed = true)
    internal lateinit var searchRepository: SearchRepository

    @MockK(relaxed = true)
    internal lateinit var navManager: NavigationManager

    @MockK(relaxed = true)
    internal lateinit var observer: Observer<SearchViewModel.ViewState>


    private lateinit var getUserProfileListUseCase: GetUserProfileListUseCase

    private lateinit var testViewModel: SearchViewModel

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        getUserProfileListUseCase = GetUserProfileListUseCase(searchRepository, mainCoroutineRule.testDispatcher)

        testViewModel = SearchViewModel(
            navManager,
            getUserProfileListUseCase
        )

        testViewModel.stateLiveData.observeForever(observer)
    }

    @After
    fun teardown() {
        testViewModel.stateLiveData.removeObserver(observer)
    }

    /**
     * ViewState typical test
     */
    @Test
    fun `viewModel should initialize with correct view state`() {
        testViewModel.stateLiveData.value shouldBeEqualTo SearchViewModel.ViewState(
            isUserProfileListLoading = false,
            isServerError = false,
            isNetworkError = false,
            userProfileList = listOf(),
            keyword = null
        )
    }

    @Test
    fun `verify view state when loadUserProfileList invoke with non-blank keyword and repository return successfully`() {
        val mockReturnList = mockk<List<UserDomainModel>>()
        val searchKeyword = "test"

        // given
        testViewModel.stateLiveData.value!!.keyword = searchKeyword
        every { runBlocking { searchRepository.getUserProfileListByKeyword(any()) } } returns Either.Success(mockReturnList)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadUserProfileList(searchKeyword) }

        // expect
        verify(exactly = 3) { observer.onChanged(any()) } // init, startSearch, finishSearch
        testViewModel.stateLiveData.value shouldBeEqualTo SearchViewModel.ViewState(
            isUserProfileListLoading = false,
            isNetworkError = false,
            isServerError = false,
            userProfileList = mockReturnList,
            keyword = searchKeyword
        )
    }

    @Test
    fun `verify view state when loadUserProfileList invoke with non-blank keyword and repository return with failure`() {
        val searchKeyword = "test"

        // given
        testViewModel.stateLiveData.value!!.keyword = searchKeyword
        every { runBlocking { searchRepository.getUserProfileListByKeyword(any()) } } returns Either.Failure(Failure.NetworkConnection)

        // when
        mainCoroutineRule.runBlockingTest { testViewModel.loadUserProfileList(searchKeyword) }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, startSearch, finishSearch, error
        testViewModel.stateLiveData.value shouldBeEqualTo SearchViewModel.ViewState(
            isUserProfileListLoading = false,
            isNetworkError = true,
            isServerError = false,
            userProfileList = listOf(),
            keyword = searchKeyword
        )
    }

    @Test
    fun `verify view state when getUserProfileListUseCase return form data not complete failure`() {
        val searchKeyword = ""
        // when
        testViewModel.stateLiveData.value!!.keyword = searchKeyword
        mainCoroutineRule.runBlockingTest { testViewModel.loadUserProfileList(searchKeyword) }

        // expect
        verify(exactly = 4) { observer.onChanged(any()) } // init, start, finsih, fail
        testViewModel.stateLiveData.value shouldBeEqualTo SearchViewModel.ViewState(
            isUserProfileListLoading = false,
            isServerError = false,
            isNetworkError = false,
            isValidationFail = true,
            userProfileList = listOf(),
            keyword = searchKeyword
        )
    }


}