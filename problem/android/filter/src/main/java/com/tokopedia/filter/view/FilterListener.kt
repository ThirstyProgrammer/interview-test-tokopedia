package com.tokopedia.filter.view

import com.tokopedia.filter.view.data.ProductFilter

interface FilterListener {
    fun onFilterSubmitted(productFilter: ProductFilter)
}