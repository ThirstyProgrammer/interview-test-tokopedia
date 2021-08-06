package com.tokopedia.filter.view

import android.graphics.Paint
import android.view.View
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.tokopedia.filter.R
import com.tokopedia.filter.view.data.Product
import com.tokopedia.filter.view.util.toRupiah

class ProductViewHolder(private var view: View) : RecyclerView.ViewHolder(view) {

    private val imageView: AppCompatImageView = view.findViewById(R.id.image)
    private val titleView: AppCompatTextView = view.findViewById(R.id.title)
    private val percentageDiscountView: AppCompatTextView = view.findViewById(R.id.percentage_discount)
    private val slashedPrice: AppCompatTextView = view.findViewById(R.id.slashed_price)
    private val price: AppCompatTextView = view.findViewById(R.id.price)
    private val city: AppCompatTextView = view.findViewById(R.id.city)

    fun bind(product: Product) {
        Glide.with(view)
            .load(product.imageUrl)
            .fitCenter()
            .into(imageView)
        titleView.text = product.name
        percentageDiscountView.text = view.context.getString(R.string.percentage, product.discountPercentage)
        slashedPrice.text = product.slashedPriceInt.toRupiah()
        slashedPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
        price.text = product.priceInt.toRupiah()
        city.text = product.shop.city
        percentageDiscountView.visibility = if (product.discountPercentage > 0) View.VISIBLE else View.GONE
        slashedPrice.visibility = if (product.slashedPriceInt > 0) View.VISIBLE else View.GONE
    }
}