package com.bcg.gpscompass.ui.screen.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bcg.gpscompass.R
import com.bcg.gpscompass.repository.model.Day
import com.bcg.gpscompass.repository.model.ForecastDay
import com.bcg.gpscompass.repository.model.Hour
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class WeatherDayAdapter(private val items: ArrayList<ForecastDay>, private var isTempC: Boolean = true) :
    RecyclerView.Adapter<WeatherDayAdapter.HourViewHolder>() {

    class HourViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTimeDay: AppCompatTextView
        val tvTemp: AppCompatTextView
        val tvHumidity: AppCompatTextView
        val ivIcon: AppCompatImageView

        init {
            tvTimeDay = view.findViewById(R.id.tvTimeDay)
            tvTemp = view.findViewById(R.id.tvTempDay)
            tvHumidity = view.findViewById(R.id.tvHumidity)
            ivIcon = view.findViewById(R.id.iconForestDay)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_forest_day, viewGroup, false)

        return HourViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: HourViewHolder, position: Int) {
        val minTemp = if (isTempC) {
            items[position].day?.mintempC?.toInt()
        } else {
            items[position].day?.mintempF?.toInt()
        }
        val maxTemp = if (isTempC) {
            items[position].day?.maxtempC?.toInt()
        } else {
            items[position].day?.maxtempF?.toInt()
        }
        val symbol = if (isTempC){"C"}else{"F"}
        "${minTemp}°${symbol} | ${maxTemp}°${symbol}".also { viewHolder.tvTemp.text = it }
        val simpleDateFormat = SimpleDateFormat(/* pattern = */ "yyyy-MM-dd")
        val hourFormat = SimpleDateFormat(/* pattern = */ "EEE, dd/MM")
        val time: Date? = items[position].date?.let { simpleDateFormat.parse(it) }
        viewHolder.tvTimeDay.text = hourFormat.format(time)
        (items[position].day?.avghumidity.toString() + "%").also { viewHolder.tvHumidity.text = it }
        Glide.with(viewHolder.itemView).load("https:${items[position].day?.condition?.icon}")
            .into(
                viewHolder.ivIcon
            )
    }

    override fun getItemCount() = items.size

    fun setData(data: List<ForecastDay>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    fun changeTempSymbol(state: Boolean) {
        isTempC = state
        notifyDataSetChanged()
    }
}
