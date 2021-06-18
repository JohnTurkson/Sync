package com.johnturkson.sync.data

import java.util.Locale
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import kotlin.experimental.and

fun Account.computeOTP(interval: Int = 30, offset: Int = 0, time: Long = System.currentTimeMillis() / 1000): String {
    val key = secret.toUpperCase(Locale.ROOT)
    val seed = key.decodeBase32().toHexString()
    val counter = (time - offset) / interval
    val encoded = counter.toString(16).toUpperCase(Locale.ROOT).padStart(16, '0')
    return generate(seed, encoded)
}

private fun generate(key: String, time: String, algorithm: String = "HmacSHA1", length: Int = 6): String {
    val padding = '0'
    val padded = time.padStart(16, padding)
    val keyHex = key.toHexBytes()
    val messageHex = padded.toHexBytes()
    val hash = hash(keyHex, messageHex, algorithm)
    
    val offset = (hash[hash.lastIndex] and 0xf).toInt()
    val binary = ((hash[offset] and 0x7f).toInt() shl 24) or
            ((hash[offset + 1]).toInt() and 0xff shl 16) or
            ((hash[offset + 2]).toInt() and 0xff shl 8) or
            ((hash[offset + 3]).toInt() and 0xff)
    
    return binary.toString().takeLast(length).padStart(length, padding)
}

private fun hash(key: ByteArray, text: ByteArray, algorithm: String): ByteArray {
    return Mac.getInstance(algorithm).apply { init(SecretKeySpec(key, "RAW")) }.doFinal(text)
}

private fun ByteArray.toHexString(): String {
    val radix = 0x10
    val shift = 0x4
    val mask = 0xf
    val separator = ""
    
    return this.map { byte -> byte.toInt() }
        .map { bits -> (bits shr shift and mask) to (bits and mask) }
        .joinToString(separator) { (high, low) -> high.toString(radix) + low.toString(radix) }
}

private fun String.toHexBytes(): ByteArray {
    val radix = 0x10
    val padding = '0'
    
    val length = this.length - this.length / 2 + (this.length + 1) / 2
    val data = this.padStart(length, padding)
    
    return data.indices.asSequence()
        .filter { index -> index % 2 == 0 }
        .map { index -> data[index] to data[index + 1] }
        .map { (high, low) -> high.toString() + low.toString() }
        .map { value -> value.toInt(radix) }
        .map { value -> value.toByte() }
        .toList()
        .toByteArray()
}

private fun ByteArray.encodeBase32(): String {
    val radix = 0x2
    val mask = 0xff
    val byteLength = 0x8
    val baseLength = 0x5
    
    val byteExtension = '0'
    val baseExtension = '0'
    val byteSeparator = ""
    val baseSeparator = ""
    val basePadding = '='
    
    val characterRange = 0..25
    val characterOffset = 65
    val digitOffset = 24
    
    val encoded = this.toList()
        .map { byte -> byte.toInt() and mask }
        .joinToString(byteSeparator) { byte -> byte.toString(radix).padStart(byteLength, byteExtension) }
        .chunked(baseLength)
        .asSequence()
        .map { bits -> bits.padEnd(baseLength, baseExtension) }
        .map { bits -> bits.toInt(radix) }
        .map { base -> if (base in characterRange) base + characterOffset else base + digitOffset }
        .map { base -> base.toChar() }
        .joinToString(baseSeparator)
    
    val lengthMultiple = 8
    val paddedLength = when {
        encoded.length % lengthMultiple != 0 -> encoded.length - (encoded.length % lengthMultiple) + lengthMultiple
        else -> encoded.length
    }
    
    return encoded.padEnd(paddedLength, basePadding)
}

private fun String.decodeBase32(): ByteArray {
    val radix = 0x2
    val byteLength = 0x8
    val baseLength = 0x5
    
    val baseExtension = '0'
    val baseSeparator = ""
    val basePadding = '='
    
    val letterOffset = 65
    val digitOffset = 24
    
    return this.toUpperCase(Locale.ROOT)
        .dropLastWhile { base -> base == basePadding }
        .map { base -> if (base.isLetter()) base - letterOffset else base - digitOffset }
        .map { base -> base.toInt() }
        .map { base -> base.toString(radix) }
        .joinToString(baseSeparator) { base -> base.padStart(baseLength, baseExtension) }
        .chunked(byteLength)
        .dropLastWhile { bits -> bits.length != byteLength }
        .map { bits -> bits.toInt(radix) }
        .map { bits -> bits.toByte() }
        .toByteArray()
}
