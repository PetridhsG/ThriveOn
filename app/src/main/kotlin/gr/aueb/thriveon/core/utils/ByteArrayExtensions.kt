package gr.aueb.thriveon.core.utils

import android.util.Base64

fun ByteArray.encodeToPemBase64String(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP).splitToLines(64)
}
