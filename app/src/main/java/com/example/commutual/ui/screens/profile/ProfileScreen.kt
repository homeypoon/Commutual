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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.commutual.R
import com.example.commutual.common.composable.ActionToolbar
import com.example.commutual.common.composable.BasicButton
import com.example.commutual.common.ext.basicButton
import com.example.commutual.ui.screens.item.PostItem
import com.example.commutual.R.string as AppText


@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
@ExperimentalMaterialApi
fun ProfileScreen(
    openScreen: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    LaunchedEffect(Unit) { viewModel.initialize() }

    val userPosts = viewModel.userPosts.collectAsStateWithLifecycle(emptyList())
    val user by viewModel.user

    Column(
        modifier = Modifier
            .fillMaxHeight()
    ) {
        ActionToolbar(
            title = AppText.profile,
            endActionIcon = R.drawable.ic_settings,
            endAction = { viewModel.onSettingsClick(openScreen) },
            modifier = modifier
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {

            Text(text = user.username, style = MaterialTheme.typography.h2)
            Text(text = user.bio, style = MaterialTheme.typography.body1)
            BasicButton(
                AppText.edit_profile,
                Modifier.basicButton()
            ) { viewModel.onEditProfileClick(openScreen) }

            LazyColumn(
                Modifier
                    .weight(1f)
                    .wrapContentHeight()
            ) {

                items(userPosts.value, key = { it.postId }) { postItem ->
                    Surface(modifier = Modifier.clickable {
                        viewModel.onPostClick(openScreen, postItem)
                    }) {
                        PostItem(
                            post = postItem
                        )
                    }

                }
            }
        }


    }
}