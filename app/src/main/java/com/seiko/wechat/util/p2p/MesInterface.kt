package com.seiko.wechat.util.p2p

import java.io.DataInputStream
import java.io.DataOutputStream


interface MesDecoder<out T> {
    suspend fun decode(input: DataInputStream): T?
}

interface MesEncoder<in T> {
    suspend fun encode(data: T, output: DataOutputStream)
}

abstract class MesAdapter<T> : MesDecoder<T>, MesEncoder<T>