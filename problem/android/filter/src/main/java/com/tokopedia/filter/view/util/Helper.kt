package com.tokopedia.filter.view.util

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

fun readTextFile(inputStream: InputStream): String {
    val outputStream = ByteArrayOutputStream()
    val buf = ByteArray(1024)
    var len: Int
    try {
        while (inputStream.read(buf).also { len = it } != -1) {
            outputStream.write(buf, 0, len)
        }
        outputStream.close()
        inputStream.close()
    } catch (e: IOException) {
    }
    return outputStream.toString()
}