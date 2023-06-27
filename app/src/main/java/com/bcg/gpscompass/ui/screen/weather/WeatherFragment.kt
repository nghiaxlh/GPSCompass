package com.bcg.gpscompass.ui.screen.weather

import android.os.Bundle
import android.view.*
import android.view.MenuItem.OnMenuItemClickListener
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.Toolbar
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.bcg.gpscompass.R
import com.bcg.gpscompass.repository.GpsCompassRepositoryImpl
import com.bcg.gpscompass.repository.model.*
import com.bcg.gpscompass.repository.remote.ApiState
import com.bcg.gpscompass.ui.screen.AppViewModelFactory
import com.bumptech.glide.Glide
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [WeatherFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeatherFragment : Fragment(), Toolbar.OnMenuItemClickListener {
    private var address: String? = null
    private var latitude: Double? = null
    private var longitude: Double? = null
    private var mToolbar: Toolbar? = null
    private var mLoadingView: ProgressBar? = null
    private var mContentView: NestedScrollView? = null
    private var mIvCurrentTemp: AppCompatImageView? = null
    private var tvError: AppCompatTextView? = null
    private var mTvAddress: AppCompatTextView? = null
    private var mTvSunrice: AppCompatTextView? = null
    private var mTvSunset: AppCompatTextView? = null
    private var mTvForestAddress: AppCompatTextView? = null
    private var mTvForestTime: AppCompatTextView? = null
    private var mTvForestTemp: AppCompatTextView? = null
    private var mTvForestDesc: AppCompatTextView? = null
    private var mRecyclerViewForestHour: RecyclerView? = null
    private var mRecyclerViewForestDay: RecyclerView? = null
    lateinit var mViewModel: WeatherViewModel
    lateinit var mHourAdapter: WeatherHourAdapter
    lateinit var mDayAdapter: WeatherDayAdapter
    var isTempC: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            address = arguments!!.getString(ARG_ADDRESS)
            latitude = arguments!!.getDouble(ARG_LATITUDE)
            longitude = arguments!!.getDouble(ARG_LONGITUDE)
        }
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mViewModel = ViewModelProvider(this, AppViewModelFactory(GpsCompassRepositoryImpl())).get(
            WeatherViewModel::class.java
        )
        if (longitude != null && latitude != null) {
            mViewModel.getWeather(latitude!!, longitude!!)
        }
        mViewModel.getWeather(latitude!!, longitude!!)
        mHourAdapter = WeatherHourAdapter(ArrayList<Hour>())
        mDayAdapter = WeatherDayAdapter(ArrayList<ForecastDay>())
        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mToolbar = view.findViewById(R.id.toolBar)
        mToolbar?.inflateMenu(R.menu.weather_menu)
        mToolbar?.setOnMenuItemClickListener(this)
        mLoadingView = view.findViewById(R.id.loadingView)
        mContentView = view.findViewById(R.id.nestScrollView)
        mToolbar!!.setNavigationIcon(R.drawable.ic_back_white)
        mToolbar!!.setNavigationOnClickListener(View.OnClickListener { activity!!.onBackPressedDispatcher.onBackPressed() })
        mIvCurrentTemp = view.findViewById(R.id.iv_forest)
        tvError = view.findViewById(R.id.tvError)
        mTvAddress = view.findViewById(R.id.tv_address)
        mTvSunrice = view.findViewById(R.id.tv_sunrice)
        mTvSunset = view.findViewById(R.id.tv_sunset)
        mTvForestAddress = view.findViewById(R.id.tv_location_forest)
        mTvForestTime = view.findViewById(R.id.tv_date_time)
        mTvForestTemp = view.findViewById(R.id.tv_forest_temp)
        mTvForestDesc = view.findViewById(R.id.tv_forest_description)
        mRecyclerViewForestHour = view.findViewById(R.id.recycleViewHour)
        mRecyclerViewForestDay = view.findViewById(R.id.recycleViewDay)
        mTvAddress!!.text = address
        mRecyclerViewForestHour!!.adapter = mHourAdapter
        mRecyclerViewForestDay!!.adapter = mDayAdapter
        loadData()
    }


    override fun onMenuItemClick(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.temp_type -> {
                true
            }
            else -> {
                false
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.weather_menu, menu);
        super.onCreateOptionsMenu(menu, inflater)
    }

    private fun loadData() {
        lifecycleScope.launch {
            mViewModel.weatherData.collect { it ->
                when (it) {
                    is ApiState.Loading -> {
                        mLoadingView!!.visibility = View.VISIBLE
                        mContentView!!.visibility = View.GONE
                        tvError!!.visibility = View.GONE
                    }
                    is ApiState.Failure -> {
                        it.e.printStackTrace()
                        tvError!!.visibility = View.VISIBLE
                    }
                    is ApiState.Success -> {
                        mLoadingView!!.visibility = View.GONE
                        mContentView!!.visibility = View.VISIBLE
                        tvError!!.visibility = View.GONE
                        val weather = it.data as Weather
                        val currentWeather: Current? = weather.current
                        val location: Location? = weather.location
                        "${location?.name} ${location?.country}".also {
                            mTvForestAddress!!.text = it
                        }
                        mTvForestTime!!.text = location?.localtime
                        currentWeather.let { current ->
                            mTvForestTemp!!.text = if (isTempC) {
                                current?.tempC.toString() + "\u00B0C"
                            } else {
                                current?.tempF.toString() + "\u00B0F"
                            }
                            Glide.with(this@WeatherFragment)
                                .load("https:${current?.condition?.icon}")
                                .into(
                                    mIvCurrentTemp!!
                                )
                            mTvForestDesc?.text = current?.condition?.text
                        }
                        weather.forecast?.forecastday.let { days ->
                            mDayAdapter.setData(days ?: ArrayList<ForecastDay>())
                            if (days?.first() != null) {
                                mTvSunrice!!.text = days.first().astro?.sunrise
                                mTvSunset!!.text = days.first().astro?.sunset
                                mHourAdapter.setData(days.first().hour)
                            }
                        }

                    }
                    is ApiState.Empty -> {
                        tvError!!.visibility = View.VISIBLE
                        tvError!!.text = getString(R.string.data_empty)
                    }
                }

            }
        }
    }

    companion object {
        private const val ARG_ADDRESS = "ARG_ADDRESS"
        private const val ARG_LATITUDE = "ARG_LATITUDE"
        private const val ARG_LONGITUDE = "ARG_LONGITUDE"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WeatherFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(address: String?, latitude: Double?, longitude: Double?): WeatherFragment {
            val fragment = WeatherFragment()
            val args = Bundle()
            args.putString(ARG_ADDRESS, address)
            args.putDouble(ARG_LATITUDE, latitude!!)
            args.putDouble(ARG_LONGITUDE, longitude!!)
            fragment.arguments = args
            return fragment
        }
    }
}