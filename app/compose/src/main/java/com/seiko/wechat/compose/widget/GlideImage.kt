package com.seiko.wechat.compose.widget

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import androidx.compose.*
import androidx.ui.core.ContextAmbient
import androidx.ui.core.Modifier
import androidx.ui.core.WithConstraints
import androidx.ui.foundation.Box
import androidx.ui.foundation.Canvas
import androidx.ui.foundation.ContentGravity
import androidx.ui.foundation.Image
import androidx.ui.graphics.ImageAsset
import androidx.ui.graphics.asImageAsset
import androidx.ui.graphics.drawscope.drawCanvas
import androidx.ui.tooling.preview.Preview
import androidx.ui.unit.IntPx
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun GlideImage(
    url: String,
    modifier: Modifier = Modifier,
    onImageReady: (() -> Unit)? = null
) {
    var image by state<ImageAsset?> { null }
    var drawable by state<Drawable?> { null }
    val context = ContextAmbient.current

    onCommit(url) {
        val glide = Glide.with(context)
        var target: CustomTarget<Bitmap>? = null


        val job = CoroutineScope(Dispatchers.Main).launch {
            target = object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {
                    image = null
                    drawable = placeholder
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    FrameManager.framed {
                        image = resource.asImageAsset()
                        onImageReady?.invoke()
                    }
                }
            }

            glide.asBitmap()
                .load(url)
                .into(target!!)
        }

        onDispose {
            image = null
            drawable = null
            glide.clear(target)
            job.cancel()
        }
    }

    val theImage = image
    val theDrawable = drawable
    if (theImage != null) {
        Box(
            modifier = modifier,
            gravity = ContentGravity.Center
        ) {
            Image(asset = theImage)
        }
    } else if (theDrawable != null) {
        Canvas(modifier = modifier) {
            drawCanvas { canvas, _ -> // pxSize
                theDrawable.draw(canvas.nativeCanvas)
            }
        }
    }
}

@Preview
@Composable
fun GlideImagePreview() {
    GlideImage("https://github.com/vinaygaba/CreditCardView/raw/master/images/Feature%20Image.png")
}
