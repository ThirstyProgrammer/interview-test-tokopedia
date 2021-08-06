package com.tokopedia.filter.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.ViewCompat
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import com.tokopedia.filter.R
import com.tokopedia.filter.view.data.ProductFilter
import com.tokopedia.filter.view.util.toRupiah

class FilterBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var mlistener: FilterListener
    private lateinit var productFilter: ProductFilter

    private val locationOptionSize: Int = 3
    private var chipGroup: ChipGroup? = null
    private var tvMinPrice: AppCompatTextView? = null
    private var tvMaxPrice: AppCompatTextView? = null
    private var sliderMinPrice: Slider? = null
    private var sliderMaxPrice: Slider? = null
    private var btnSubmit: AppCompatButton? = null
    private var btnReset: AppCompatButton? = null
    private var indexChip: ArrayList<Int> = arrayListOf()

    private var arrCityFilter: Array<String> = emptyArray()
    private var minPrice: Int = 0
    private var maxPrice: Int = 0

    companion object {
        fun newInstance(bundle: Bundle): FilterBottomSheetFragment {
            val fragment = FilterBottomSheetFragment()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chipGroup = getView()?.findViewById(R.id.chip_group)
        tvMinPrice = getView()?.findViewById(R.id.min_price)
        sliderMinPrice = getView()?.findViewById(R.id.slider_min_price)
        tvMaxPrice = getView()?.findViewById(R.id.max_price)
        sliderMaxPrice = getView()?.findViewById(R.id.slider_max_price)
        btnSubmit = getView()?.findViewById(R.id.submit)
        btnReset = getView()?.findViewById(R.id.reset)

        initFilterLocation()
        initFilterPrice()

        sliderMinPrice?.setOnChangeListener { _, value ->
            productFilter.minPrice = value.toInt()
            tvMinPrice?.text = productFilter.minPrice.toRupiah()
        }
        sliderMaxPrice?.setOnChangeListener { _, value ->
            productFilter.maxPrice = value.toInt()
            tvMaxPrice?.text = productFilter.maxPrice.toRupiah()
        }

        btnSubmit?.setOnClickListener {
            submitFilter()
        }
        btnReset?.setOnClickListener {
            reset()
        }
    }

    fun setupListener(listener: FilterListener) {
        mlistener = listener
    }

    fun setupFilter(arrCityFilter: Array<String>, minPrice: Int, maxPrice: Int) {
        this.arrCityFilter = arrCityFilter
        this.minPrice = minPrice
        this.maxPrice = maxPrice
        initFilterValue()
    }

    fun setupFilter(productFilter: ProductFilter?) {
        if (productFilter != null) this.productFilter = productFilter
    }

    private fun initFilterValue() {
        productFilter = ProductFilter(
            emptyList(),
            minPrice,
            maxPrice
        )
    }

    /**
     * For price filter, i'm using 2 slider
     * because rangeSlider is only available on higher version of
     * material component library
     */
    private fun initFilterPrice() {
        tvMinPrice?.text = minPrice.toRupiah()
        tvMaxPrice?.text = maxPrice.toRupiah()
        sliderMinPrice.apply {
            this?.valueFrom = minPrice.toFloat()
            this?.valueTo = maxPrice.toFloat()
            this?.value = minPrice.toFloat()
        }
        sliderMaxPrice.apply {
            this?.valueFrom = minPrice.toFloat()
            this?.valueTo = maxPrice.toFloat()
            this?.value = maxPrice.toFloat()
        }
    }

    private fun initFilterLocation() {
        indexChip.clear()
        val tempList: List<String> = arrCityFilter.toList()
        tempList.subList(0, (locationOptionSize - 1)).forEach {
            val chip = View.inflate(requireContext(), R.layout.location_item, null) as Chip
            chip.id = ViewCompat.generateViewId()
            chip.text = it
            chip.isChecked = productFilter.cities.contains(it)
            indexChip.add(chip.id)
            chipGroup?.addView(chip)
        }

        val chip = View.inflate(requireContext(), R.layout.location_item, null) as Chip
        chip.id = ViewCompat.generateViewId()
        chip.text = getString(R.string.other)
        chip.isChecked = productFilter.cities.size > 1
        indexChip.add(chip.id)
        chipGroup?.addView(chip)
    }

    private fun reset() {
        initFilterPrice()
        chipGroup?.apply {
            for (index in 0 until childCount) {
                val chip = getChildAt(index) as Chip
                chip.isChecked = false
            }
        }
        initFilterValue()
    }

    private fun validatePrice(): Boolean {
        if (productFilter.minPrice >= productFilter.maxPrice) {
            Toast.makeText(
                requireContext(),
                getString(R.string.message_price_invalid),
                Toast.LENGTH_LONG
            ).show()
            return false
        }
        return true
    }

    private fun submitFilter() {
        if (validatePrice()) {
            productFilter.cities = generateSelectedCities().toList()
            mlistener.onFilterSubmitted(productFilter)
            dismiss()
        }
    }

    private fun generateSelectedCities(): Array<String> {
        return when (val index: Int = indexChip.indexOf(chipGroup?.checkedChipId)) {
            -1 -> emptyArray()
            (locationOptionSize - 1) -> {
                val tempList: List<String> = arrCityFilter.toList()
                tempList.subList(index, arrCityFilter.size - 1).toTypedArray()
            }
            else -> arrayOf(arrCityFilter[index])
        }
    }
}