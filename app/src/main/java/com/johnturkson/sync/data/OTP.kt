package com.johnturkson.sync.data

import kotlin.random.Random

fun Account.computeOTP(time: Long = System.currentTimeMillis()): String {
    return Random.nextInt(111111, 999999).toString()
}
