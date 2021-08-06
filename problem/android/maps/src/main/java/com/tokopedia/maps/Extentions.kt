package com.tokopedia.maps

fun String?.defaultDash(): String = if (this.isNullOrEmpty()) "-" else this
fun Int?.defaultZero(): Int = this ?: 0
