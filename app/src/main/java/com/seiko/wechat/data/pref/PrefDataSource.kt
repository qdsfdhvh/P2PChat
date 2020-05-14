package com.seiko.wechat.data.pref

interface PrefDataSource {

    var userLogoIndex: Int
    var userName: String

    /**
     * 设备UUID
     * PS:每个安装应用时自动生成，并不是单个设备唯一。
     */
    var deviceUUID: String

}