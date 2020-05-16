package com.seiko.wechat.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class PeerBean(
    val uuid: UUID,
    val name: String,
    val logoResId: Int,
    val address: String
) : Parcelable