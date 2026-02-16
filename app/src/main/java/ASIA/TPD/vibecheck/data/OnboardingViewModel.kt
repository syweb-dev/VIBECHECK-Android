/*
MIT License
Copyright (c) 2026 ASIA TPD
See the LICENSE file in the project root for full license information.
*/
package top.lucanex.top.vibecheck.data

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class OnboardingViewModel(private val savedStateHandle: SavedStateHandle) : ViewModel() {

    private val _currentPage = MutableStateFlow(savedStateHandle.get<Int>("current_page") ?: 0)
    val currentPage: StateFlow<Int> = _currentPage.asStateFlow()

    fun onPageChanged(page: Int) {
        if (_currentPage.value != page) {
            _currentPage.value = page
            savedStateHandle["current_page"] = page
        }
    }
}
