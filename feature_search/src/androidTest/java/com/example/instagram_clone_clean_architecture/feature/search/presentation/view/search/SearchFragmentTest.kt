package com.example.instagram_clone_clean_architecture.feature.search.presentation.view.search

import androidx.lifecycle.MutableLiveData
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.agoda.kakao.edit.KEditText
import com.agoda.kakao.screen.Screen
import com.agoda.kakao.screen.Screen.Companion.onScreen
import com.agoda.kakao.text.KButton
import com.example.instagram_clone_clean_architecture.feature.search.R
import com.example.instagram_clone_clean_architecture.feature.search.domain.usecase.GetUserProfileListUseCase
import com.example.library_base.presentation.navigation.NavigationManager
import com.example.library_test_utils.makeDIAwareTestRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.kodein.di.DI
import org.kodein.di.bind
import org.kodein.di.singleton

@RunWith(AndroidJUnit4ClassRunner::class)
class SearchFragmentTest {


    private val navManger: NavigationManager = mockk(relaxed = true)
    private val getUserProfileListUseCase: GetUserProfileListUseCase = mockk(relaxed = true)

    private val viewModel: SearchViewModel = spyk(SearchViewModel(
        navManger,
        getUserProfileListUseCase
    ))

    private val fragment = SearchFragment()

    @get:Rule
    val diAwareTestRule = makeDIAwareTestRule(
        fragment,
        DI.Module("SEARCH_FRAGMENT_TEST_MODULE") {
        bind<SearchViewModel>() with  singleton { viewModel }
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

    class SearchScreen: Screen<SearchScreen>() {
        val searchField = KEditText { withId(R.id.search_field) }
        val searchButton = KButton { withId(R.id.search_button) }
    }

}