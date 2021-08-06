package com.tokopedia.filter.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.tokopedia.core.dpToPx
import com.tokopedia.filter.R
import com.tokopedia.filter.view.data.Product
import com.tokopedia.filter.view.data.ProductFilter
import com.tokopedia.filter.view.data.Source
import com.tokopedia.filter.view.data.ViewState
import com.tokopedia.filter.view.util.GridSpacingItemDecoration
import com.tokopedia.filter.view.util.readTextFile
import kotlinx.android.synthetic.main.activity_product.*

class ProductActivity : AppCompatActivity(), FilterListener {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabFilter: FloatingActionButton
    private lateinit var viewState: RelativeLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyList: AppCompatTextView

    private val mAdapter = ProductAdapter()
    private var filterDialogFragment: FilterBottomSheetFragment? = null
    private var productFilter: ProductFilter? = null
    private var products: Array<Product> = emptyArray()
    private var arrCityFilter: Array<String> = emptyArray()
    private var minPrice: Int = 0
    private var maxPrice: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        bindViews()
        initListeners()

        val spanCount = 2
        val spacing = dpToPx(16F, this)
        val gridLayoutManager =
            GridLayoutManager(this, spanCount, GridLayoutManager.VERTICAL, false)
        val itemDecoration = GridSpacingItemDecoration(spanCount, spacing.toInt(), true)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = gridLayoutManager
            adapter = mAdapter
            addItemDecoration(itemDecoration)
        }

        products = getProductList()
        mAdapter.updateData(getProductList())
        generateMinAndMaxPrice()
        generateCityFilter()
    }

    private fun bindViews() {
        recyclerView = findViewById(R.id.product_list)
        fabFilter = findViewById(R.id.filter)
        viewState = findViewById(R.id.view_state)
        progressBar = findViewById(R.id.loading)
        emptyList = findViewById(R.id.empty_list)
    }

    private fun initListeners() {
        fabFilter.setOnClickListener {
            showBottomSheetFilter()
        }
    }

    override fun onFilterSubmitted(productFilter: ProductFilter) {
        this.productFilter = productFilter
        mAdapter.updateData(
            getFilteredProductList(
                productFilter.cities,
                productFilter.minPrice,
                productFilter.maxPrice
            )
        )
    }

    private fun showBottomSheetFilter() {
        if (filterDialogFragment == null) {
            filterDialogFragment = FilterBottomSheetFragment.newInstance(Bundle())
            filterDialogFragment?.setupListener(this)
        }
        if (productFilter != null) {
            filterDialogFragment?.setupFilter(productFilter)
        } else {
            filterDialogFragment?.setupFilter(arrCityFilter, minPrice, maxPrice)
        }
        filterDialogFragment?.show(supportFragmentManager, FILTER_DIALOG)
    }

    private fun getProductList(): Array<Product> {
        setupViewState(ViewState.Loading)
        val fileInputStream = resources.openRawResource(R.raw.products)
        val strFile = readTextFile(fileInputStream)

        val gson = Gson()
        val source = gson.fromJson(strFile, Source::class.java)
        setupViewState(ViewState.Success)
        return source.data.products.toTypedArray()
    }

    private fun getFilteredProductList(
        cities: List<String>,
        minPrice: Int,
        maxPrice: Int
    ): Array<Product> {
        val tempArr = products.filter { product ->
            product.priceInt in minPrice..maxPrice && if (cities.isNotEmpty()) cities.contains(
                product.shop.city
            ) else true
        }.toTypedArray()

        val state = if (tempArr.isEmpty()) ViewState.Empty else ViewState.Success
        setupViewState(state)

        return tempArr
    }

    private fun generateMinAndMaxPrice() {
        val arrPrice = Array(products.size) { 0 }
        products.forEachIndexed { index, product ->
            arrPrice[index] = product.priceInt
        }
        minPrice = arrPrice.min() ?: 0
        maxPrice = arrPrice.max() ?: 0
    }

    private fun generateCityFilter() {
        val tempCityArr: HashMap<String, Int> = hashMapOf()
        products.forEach { product ->
            val createdMap = tempCityArr.filterKeys { it == product.shop.city }
            if (createdMap.isEmpty()) {
                tempCityArr[product.shop.city] = 1
            } else {
                tempCityArr[product.shop.city] = createdMap.getValue(product.shop.city) + 1
            }
        }
        val sortedCityByValue =
            tempCityArr.toList().sortedByDescending { (_, value) -> value }.toMap()
        arrCityFilter = sortedCityByValue.keys.toTypedArray()
    }

    private fun setupViewState(state: ViewState) {
        when (state) {
            ViewState.Success -> {
                viewState.visibility = View.GONE
            }
            ViewState.Loading -> {
                viewState.visibility = View.VISIBLE
                loading?.visibility = View.VISIBLE
                emptyList.visibility = View.GONE
            }
            ViewState.Empty -> {
                viewState.visibility = View.VISIBLE
                loading?.visibility = View.GONE
                emptyList.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val FILTER_DIALOG = "filter_dialog_fragment"
    }
}
