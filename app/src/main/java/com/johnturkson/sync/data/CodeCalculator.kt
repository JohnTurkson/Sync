package com.johnturkson.sync.data

import kotlin.random.Random

fun Code.computeTOTP(time: Long = System.currentTimeMillis()): String {
    return Random.nextInt(111111, 999999).toString()
}
