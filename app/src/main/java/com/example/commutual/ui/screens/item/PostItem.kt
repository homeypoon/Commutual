package com.example.commutual.ui.screens.item

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.commutual.common.ext.categoryChip
import com.example.commutual.model.CategoryEnum
import com.example.commutual.model.Post

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItem(
    post: Post,
) {
    ListItem(

        headlineText = {
            Text(
                post.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        overlineText = {
            Text(
                stringResource(post.category.categoryStringRes),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.categoryChip(
                    MaterialTheme.colorScheme.secondary
                )
            )
        },
        supportingText = {
            Column {

                Text(
                    post.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )


            }
        }
    )
//    Divider()
//    Card(
//        backgroundColor = MaterialTheme.colors.background,
//        modifier = Modifier.padding(8.dp, 0.dp, 8.dp, 8.dp)
//    ) {
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Column(modifier = Modifier.weight(1f)) {
//                Text(text = task.title)
//                Text(text = task.description)
//            }
//        }
}

@Preview
@Composable
fun postItem() {
    PostItem(post = Post("fds", "userid", "title", "description", CategoryEnum.CODING))
}