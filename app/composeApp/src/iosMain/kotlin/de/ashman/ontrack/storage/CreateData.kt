package de.ashman.ontrack.storage

import dev.gitlive.firebase.storage.Data
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import platform.Foundation.NSData
import platform.Foundation.dataWithBytes

@OptIn(ExperimentalForeignApi::class)
actual fun createDataFromBytes(bytes: ByteArray): Data {
    val nsData = bytes.usePinned {
        NSData.dataWithBytes(it.addressOf(0), bytes.size.toULong())
    }

    return Data(nsData)
}
