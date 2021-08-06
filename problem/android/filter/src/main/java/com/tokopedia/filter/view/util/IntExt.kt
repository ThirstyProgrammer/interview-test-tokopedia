package com.tokopedia.filter.view.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

fun Int?.toRupiah() : String {
    val locale = Locale("in", "id")
    val symbols = DecimalFormatSymbols.getInstance(locale)
    symbols.groupingSeparator = '.'
    symbols.monetaryDecimalSeparator = ','
    symbols.currencySymbol = symbols.currencySymbol

    val df = DecimalFormat.getCurrencyInstance(locale) as DecimalFormat
    val kursIndonesia = DecimalFormat(df.toPattern(), symbols)
    kursIndonesia.maximumFractionDigits = 0

    return kursIndonesia.format(this)
}