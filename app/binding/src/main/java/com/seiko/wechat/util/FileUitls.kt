package com.seiko.wechat.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Environment
import java.io.File

fun Context.getPictureDir(): File {
    return getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
}

fun Context.getResourceDir(): File {
    return getExternalFilesDir("Resource")!!
}

fun Context.openFile(filePath: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addCategory("android.intent.category.DEFAULT")
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setDataAndType(Uri.fromFile(File(filePath)), "image/*")
    startActivity(intent)
}