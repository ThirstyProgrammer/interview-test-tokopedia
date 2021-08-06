package com.tokopedia.maps.data

data class Country(
    var name: String,
    var capital: String,
    var population: Int,
    var callingCodes: List<String>
)