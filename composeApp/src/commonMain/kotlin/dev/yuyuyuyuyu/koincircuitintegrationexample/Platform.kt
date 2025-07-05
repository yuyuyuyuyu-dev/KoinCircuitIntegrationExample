package dev.yuyuyuyuyu.koincircuitintegrationexample

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform