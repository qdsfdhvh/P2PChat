package com.seiko.wechat.compose.widget

import androidx.compose.Composable
import androidx.ui.core.Modifier
import androidx.ui.core.clip
import androidx.ui.foundation.Image
import androidx.ui.foundation.shape.corner.RoundedCornerShape
import androidx.ui.graphics.ImageAsset
import androidx.ui.layout.preferredSize
import androidx.ui.res.imageResource
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.Dp
import androidx.ui.unit.dp
import com.seiko.wechat.R


@Composable
fun CircleImage(
    asset: ImageAsset,
    size: Dp = 80.dp,
    modifier: Modifier = Modifier
) {
    Image(
        asset = asset,
        modifier = modifier
            .preferredSize(size, size)
            .clip(RoundedCornerShape(size / 2))
    )
}

@Preview
@Composable
fun CircleImagePreview() {
    CircleImage(
        asset = imageResource(R.drawable.wechat_iv_0)
    )
}