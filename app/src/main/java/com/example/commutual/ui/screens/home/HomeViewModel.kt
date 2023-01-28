package com.example.commutual.ui.screens.home

import androidx.lifecycle.ViewModel
import com.example.commutual.EDIT_POST_SCREEN
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(): ViewModel() {

    // Navigate to EditPost Screen when user clicks on FAB
    fun onAddClick(openScreen: (String) -> Unit) = openScreen(EDIT_POST_SCREEN)
}