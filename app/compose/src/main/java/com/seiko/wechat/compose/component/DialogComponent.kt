package com.seiko.wechat.compose.component

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.AdapterList
import androidx.ui.foundation.Text
import androidx.ui.layout.preferredHeightIn
import androidx.ui.material.AlertDialog
import androidx.ui.material.Button
import androidx.ui.unit.dp

@Composable
fun DialogSingleCheckComponent(
    title: String,
    text: String,
    onPopupConfirmed: () -> Unit,
    onPopupDismissed: () -> Unit
) {
    AlertDialog(
        onCloseRequest = onPopupDismissed,
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            Button(onClick = onPopupDismissed) {
                Text(text = "取消")
            }
        },
        dismissButton = {
            Button(onClick = onPopupConfirmed) {
                Text(text = "确认")
            }
        }
    )
}

@Composable
fun <T> DialogListComponent(
    title: String,
    onPopupDismissed: () -> Unit,
    data: List<T>,
    itemCallback: @Composable() (T) -> Unit
) {
    AlertDialog(
        onCloseRequest = onPopupDismissed,
        title = { Text(text = title) },
        text = {
            AdapterList(
                modifier = Modifier.preferredHeightIn(maxHeight = 240.dp),
                data = data,
                itemCallback = itemCallback)
        },
        confirmButton = {
            Button(onClick = onPopupDismissed) {
                Text(text = "取消")
            }
        }
    )
}