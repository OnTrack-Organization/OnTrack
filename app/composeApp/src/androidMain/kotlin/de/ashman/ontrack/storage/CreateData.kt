package de.ashman.ontrack.storage

import dev.gitlive.firebase.storage.Data

actual fun createDataFromBytes(bytes: ByteArray): Data {
    return Data(bytes)
}