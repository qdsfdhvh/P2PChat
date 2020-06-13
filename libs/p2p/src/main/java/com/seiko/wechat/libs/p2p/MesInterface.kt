package com.seiko.wechat.libs.p2p

import java.net.Socket

interface MesDecoder<I, out T> {
    fun createSource(socket: Socket): I
    fun decode(source: I): T?
}

interface MesEncoder<O, in T> {
    fun createSink(socket: Socket): O
    fun encode(sink: O, msg: T)
}

abstract class MesAdapter<I, O, T> : MesDecoder<I, T>, MesEncoder<O, T>