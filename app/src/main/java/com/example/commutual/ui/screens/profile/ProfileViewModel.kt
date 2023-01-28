/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.commutual.ui.screens.profile

import androidx.compose.runtime.mutableStateOf
import com.example.commutual.*
import com.example.commutual.model.Post
import com.example.commutual.model.User
import com.example.commutual.model.service.AccountService
import com.example.commutual.model.service.ConfigurationService
import com.example.commutual.model.service.LogService
import com.example.commutual.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
  logService: LogService,
  private val storageService: StorageService,
  private val accountService: AccountService,
  private val configurationService: ConfigurationService
) : CommutualViewModel(logService) {

  val userPosts = storageService.userPosts
  val user = mutableStateOf(User())

  fun onEditProfileClick(openScreen: (String) -> Unit) {
    openScreen(EDIT_PROFILE_SCREEN)
  }


  fun initialize() {
    launchCatching {
        user.value = storageService.getUser(accountService.currentUserId) ?: User()
    }
  }


  fun onPostClick(openScreen: (String) -> Unit, post: Post) {
    openScreen("$PROFILE_POST_SCREEN?$POST_ID={${post.postId}}")
  }

  fun onSettingsClick(openScreen: (String) -> Unit) {
    openScreen(SETTINGS_SCREEN)
  }

}