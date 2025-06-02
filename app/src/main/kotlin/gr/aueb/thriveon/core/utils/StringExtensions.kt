package gr.aueb.thriveon.core.utils

import android.util.Base64

/**
 * Splits this instance to multiple lines that have a maximum length of [lineLength] each.
 *
 * @param lineLength The maximum allowed characters per line.
 *
 * @return Returns a [String] that is transformed by creating lines that have a maximum length of
 * [lineLength] each.
 */
fun String.splitToLines(lineLength: Int): String {
    return if (lineLength <= 0 || this.length <= lineLength) this
    else {
        var result = ""
        var index = 0
        while (index < length) {
            val line = substring(index, (index + lineLength).coerceAtMost(length))
            result += (if (result.isEmpty()) line else "\n$line")
            index += lineLength
        }
        result
    }
}

/**
 * Decodes this PEM Base64 string into a byte array.
 */
fun String.decodeFromPemBase64String(): ByteArray? {
    return Base64.decode(this.replace("\n", ""), Base64.NO_WRAP)
}
