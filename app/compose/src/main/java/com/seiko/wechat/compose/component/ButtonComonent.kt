package com.seiko.wechat.compose.component

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.foundation.Text
import androidx.ui.layout.fillMaxWidth
import androidx.ui.layout.height
import androidx.ui.material.Button
import androidx.ui.material.MaterialTheme
import com.seiko.wechat.compose.theme.buttonHeight

@Composable
fun ButtonLightComponent(
    modifier: Modifier,
    onClick: () -> Unit,
    text: String
) {
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(buttonHeight),
        onClick = onClick,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.button
        )
    }
}