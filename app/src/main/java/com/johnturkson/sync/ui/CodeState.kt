package com.johnturkson.sync.ui

import com.johnturkson.sync.data.Account

data class CodeState(val account: Account, val code: String, val isVisible: Boolean)
