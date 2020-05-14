package com.seiko.wechat.util

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import androidx.fragment.app.Fragment
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

/**
 * 绑定Service并返回Binder
 */
@ExperimentalCoroutinesApi
inline fun <reified S: Service, reified B: IBinder> Context.bindService(
    autoStop: Boolean = true
): Flow<B> = callbackFlow {
    val intent = Intent(this@bindService, S::class.java)
    val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            offer(service as B)
        }
        override fun onServiceDisconnected(name: ComponentName?) {
            close()
        }
    }
    bindService(intent, conn, Context.BIND_AUTO_CREATE)
    awaitClose {
        unbindService(conn)
        if (autoStop) {
            stopService(intent)
        }
    }
}

@ExperimentalCoroutinesApi
inline fun <reified S: Service, reified B: IBinder> Fragment.bindService(
    autoStop: Boolean = true
) = requireActivity().bindService<S, B>(autoStop)