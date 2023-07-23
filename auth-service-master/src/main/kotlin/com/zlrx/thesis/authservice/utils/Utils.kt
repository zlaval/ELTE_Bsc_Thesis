package com.zlrx.thesis.authservice.utils

import com.chrylis.codec.base58.Base58UUID
import java.util.UUID

const val SYSTEM = "system"
fun newId() = Base58UUID().encode(UUID.randomUUID())
