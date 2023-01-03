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

package com.example.functions.model.service.impl

import com.example.functions.model.Post
import com.example.functions.model.service.AccountService
import com.example.functions.model.service.StorageService
import com.example.functions.model.service.trace
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.firestore.ktx.toObjects
import javax.inject.Inject
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.asDeferred
import kotlinx.coroutines.tasks.await

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore, private val auth: AccountService) :
  StorageService {

  override val posts: Flow<List<Post>>
    get() =
      auth.currentUser.flatMapLatest { user ->
        currentCollection(user.id).snapshots().map { snapshot -> snapshot.toObjects() }
      }

  override suspend fun getPost(postId: String): Post? =
    currentCollection(auth.currentUserId).document(postId).get().await().toObject()

  override suspend fun save(post: Post): String =
    trace(SAVE_POST_TRACE) { currentCollection(auth.currentUserId).add(post).await().id }

  override suspend fun update(post: Post): Unit =
    trace(UPDATE_POST_TRACE) {
      currentCollection(auth.currentUserId).document(post.id).set(post).await()
    }

  override suspend fun delete(postId: String) {
    currentCollection(auth.currentUserId).document(postId).delete().await()
  }

  // TODO: It's not recommended to delete on the client:
  // https://firebase.google.com/docs/firestore/manage-data/delete-data#kotlin+ktx_2
  override suspend fun deleteAllForUser(userId: String) {
    val matchingPosts = currentCollection(userId).get().await()
    matchingPosts.map { it.reference.delete().asDeferred() }.awaitAll()
  }

  private fun currentCollection(uid: String): CollectionReference =
    firestore.collection(USER_COLLECTION).document(uid).collection(POST_COLLECTION)

  companion object {
    private const val USER_COLLECTION = "users"
    private const val POST_COLLECTION = "posts"
    private const val SAVE_POST_TRACE = "savePost"
    private const val UPDATE_POST_TRACE = "updatePost"
  }
}