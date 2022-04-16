package com.johnturkson.sync.android.data

import androidx.room.Entity

@Entity(tableName = "accounts", primaryKeys = ["issuer", "name"])
data class Account(val issuer: String, val name: String, val secret: String)
