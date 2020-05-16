package com.seiko.wechat.service

import com.seiko.wechat.util.p2p.MesAdapter
import kotlinx.coroutines.delay
import java.io.DataInputStream
import java.io.DataOutputStream

class MessageAdapter : MesAdapter<Int>() {
    companion object {
        private const val TAG = "MessageAdapter"
    }

    override suspend fun decode(input: DataInputStream): Int? {
        delay(500)
        return 100
    }

    override suspend fun encode(data: Int, output: DataOutputStream) {

    }
}