package com.example.commutual.model

import com.google.firebase.firestore.DocumentId

data class Post(
    @DocumentId val postId: String = "",
    val userId: String = "",
//    val user: User = User(),
    val title: String = "",
    val description: String = "",
    val category: CategoryEnum = CategoryEnum.DEFAULT,
//    val category: CategoryEnum = CategoryEnum.DEFAULT
//    val timestamp: Any = FieldValue.serverTimestamp()
)