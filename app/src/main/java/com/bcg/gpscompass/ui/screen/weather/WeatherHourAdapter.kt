package com.bcg.gpscompass.ui.screen.weather

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.bcg.gpscompass.R
import com.bcg.gpscompass.repository.model.Hour
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.*

class WeatherHourAdapter(private val items: ArrayList<Hour>, private var isTempC: Boolean = true) :
    RecyclerView.Adapter<WeatherHourAdapter.HourViewHolder>() {

    class HourViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTemp: AppCompatTextView
        val tvHour: AppCompatTextView
        val ivIcon: AppCompatImageView

        init {
            tvTemp = view.findViewById(R.id.tvTempHour)
            tvHour = view.findViewById(R.id.tvHourTime)
            ivIcon = view.findViewById(R.id.iconForestHour)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_forest_hour, viewGroup, false)

        return HourViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: HourViewHolder, position: Int) {
        val temp = if (isTempC) {
            items[position].tempC?.toInt()
        } else {
            items[position].tempF?.toInt()
        }
        val symbol = if (isTempC){"C"}else{"F"}
        "${temp}°${symbol}".also { viewHolder.tvTemp.text = it }
        val simpleDateFormat = SimpleDateFormat(/* pattern = */ "yyyy-MM-dd HH:mm")
        val hourFormat = SimpleDateFormat(/* pattern = */ "HH:mm")
        val time: Date? = items[position].time?.let { simpleDateFormat.parse(it) }
        viewHolder.tvHour.text = hourFormat.format(time)
        Glide.with(viewHolder.itemView).load("https:${items[position].condition?.icon}")
            .into(
                viewHolder.ivIcon
            )
    }

    override fun getItemCount() = items.size

    fun setData(data: List<Hour>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    fun changeTempSymbol(state: Boolean) {
        isTempC = state
        notifyDataSetChanged()
    }
}
