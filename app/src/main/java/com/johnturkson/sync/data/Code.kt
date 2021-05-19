package com.johnturkson.sync.data

import androidx.room.Entity

@Entity(tableName = "codes", primaryKeys = ["issuer", "account"])
data class Code(val issuer: String, val account: String, val secret: String)
