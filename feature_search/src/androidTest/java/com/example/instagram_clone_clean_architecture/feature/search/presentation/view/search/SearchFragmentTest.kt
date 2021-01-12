package com.example.instagram_clone_clean_architecture.feature.search.presentation.view.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.agoda.kakao.text.KButton
import com.example.instagram_clone_clean_architecture.feature.search.R
import com.example.library_test_utils.makeDIAwareTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

@RunWith(AndroidJUnit4ClassRunner::class)
class SearchFragmentTest {

    private val _state = MutableLiveData<SearchViewModel.ViewState>(SearchViewModel.ViewState())
    private val stateLiveData: LiveData<SearchViewModel.ViewState> = _state

    private val _errorMessage = MutableLiveData<String?>(null)
    private val errorMessageLiveData = _errorMessage

    private val viewModel: SearchViewModel = mockk(relaxed = true)

    private val fragment = SearchFragment()

    @get:Rule
    val diAwareTestRule = makeDIAwareTestRule(
        fragment,
        DI.Module("SEARCH_FRAGMENT_TEST_MODULE") {
            bind<SearchViewModel>() with singleton {
                every { viewModel.stateLiveData } returns stateLiveData
                every { viewModel.errorMessage } returns errorMessageLiveData
                viewModel
            }
        })

    @Test
    fun shouldTriggerViewModelLoadUserProfileListWhenSearchButtonClicked() {
        // given
        val searchKeyword = "searchKeyword"

        // when
        onScreen<SearchScreen> {
            searchField { typeText(searchKeyword) }
            searchButton.click()

            verify(exactly = 1) { viewModel.loadUserProfileList(searchKeyword) }
        }
    }

    class SearchScreen : Screen<SearchScreen>() {
        val searchField = KEditText { withId(R.id.search_field) }
        val searchButton = KButton { withId(R.id.search_button) }
    }
}