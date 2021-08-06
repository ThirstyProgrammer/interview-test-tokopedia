package com.tokopedia.maps

import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tokopedia.maps.data.Country
import com.tokopedia.maps.network.ApiService
import com.tokopedia.maps.network.NetworkConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

open class MapsActivity : AppCompatActivity() {

    private var mapFragment: SupportMapFragment? = null
    private var googleMap: GoogleMap? = null

    private lateinit var textCountryName: TextView
    private lateinit var textCountryCapital: TextView
    private lateinit var textCountryPopulation: TextView
    private lateinit var textCountryCallCode: TextView

    private var editText: EditText? = null
    private var buttonSubmit: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        bindViews()
        initListeners()
        loadMap()
    }

    private fun bindViews() {
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        editText = findViewById(R.id.editText)
        buttonSubmit = findViewById(R.id.buttonSubmit)
        textCountryName = findViewById(R.id.txtCountryName)
        textCountryCapital = findViewById(R.id.txtCountryCapital)
        textCountryPopulation = findViewById(R.id.txtCountryPopulation)
        textCountryCallCode = findViewById(R.id.txtCountryCallCode)
    }

    private fun initListeners() {
        buttonSubmit?.setOnClickListener {
            getLocationOnMap()
            getCountryDetail()
        }
    }

    private fun loadMap() {
        mapFragment!!.getMapAsync { googleMap -> this@MapsActivity.googleMap = googleMap }
    }

    private fun getLocationOnMap() {
        val location = editText?.text.toString()
        var addressList: List<Address>? = null
        val geocoder = Geocoder(this)

        googleMap?.clear()
        try {
            addressList = geocoder.getFromLocationName(location, 1)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (addressList.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.country_not_found), Toast.LENGTH_LONG).show()
        } else {
            val address = addressList.first()
            val latLng = LatLng(address.latitude, address.longitude)
            googleMap?.addMarker(MarkerOptions().position(latLng).title(location))
            googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 3F))
        }
    }

    private fun getCountryDetail() {
        NetworkConfig().getService().getCountryDetail(
            ApiService.RAPID_API_KEY_VALUE,
            ApiService.RAPID_API_HOST_VALUE,
            editText?.text.toString()
        ).enqueue(object : Callback<List<Country>> {
            override fun onResponse(call: Call<List<Country>>, response: Response<List<Country>>) {
                if (response.body().isNullOrEmpty()) {
                    Toast.makeText(
                        this@MapsActivity,
                        getString(R.string.country_data_not_found),
                        Toast.LENGTH_LONG
                    ).show()
                }
                setupCountryValue(response.body()?.first())
            }

            override fun onFailure(call: Call<List<Country>>, t: Throwable) {
                Toast.makeText(
                    this@MapsActivity,
                    getString(R.string.network_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setupCountryValue(country: Country?) {
        var callCode = ""
        country?.callingCodes?.forEachIndexed { index, s ->
            callCode = if (index == (country.callingCodes.size -1)) s else "$s, "
        }
        textCountryName.text = getString(R.string.country_name, country?.name.defaultDash())
        textCountryCapital.text = getString(R.string.country_capital, country?.capital.defaultDash())
        textCountryPopulation.text = getString(R.string.country_population, country?.population.defaultZero())
        textCountryCallCode.text = getString(R.string.country_call_code, callCode.defaultDash())
    }
}