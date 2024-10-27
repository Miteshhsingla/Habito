package com.timeit.habito.ui.adapters

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.timeit.habito.data.dataModels.Day
import com.timeit.habito.R

class DateAdapter(var dateItemList: List<Day>) : RecyclerView.Adapter<DateAdapter.DateViewHolder>() {

    private var selectedPosition = -1
    private var onItemClickListener: ((Int) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DateViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_date, parent, false)
        return DateViewHolder(view)
    }

    @SuppressLint("ResourceAsColor")
    override fun onBindViewHolder(holder: DateViewHolder, position: Int) {
        val day = dateItemList[position]
        holder.dayOfWeekText.text = day.dayOfWeek
        holder.dayNumberText.text = day.dayNumber.toString()

        if (position == selectedPosition) {
            holder.block.setBackgroundResource(R.drawable.selected_day_bg)
            holder.dayOfWeekText.setTextColor(Color.WHITE)
            holder.dayNumberText.setTextColor(Color.WHITE)
        }
        else {
            holder.block.setBackgroundColor(Color.TRANSPARENT)
            holder.dayOfWeekText.setTextColor(Color.GRAY)
            holder.dayNumberText.setTextColor(Color.GRAY)
        }

        holder.block.setOnClickListener {
            updateSelected(position)
            onItemClickListener?.invoke(position)
        }
    }

    override fun getItemCount(): Int {
        return dateItemList.size
    }

    fun updateSelected(position: Int) {
        if (selectedPosition != position) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }

    fun updateData(newDateItemList: List<Day>) {
        dateItemList = newDateItemList
        notifyDataSetChanged()
    }

    class DateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dayOfWeekText: TextView = itemView.findViewById(R.id.weekdayText)
        val dayNumberText: TextView = itemView.findViewById(R.id.weekdayNumber)
        val todayDot: ImageView = itemView.findViewById(R.id.todayDot)
        val block: ConstraintLayout = itemView as ConstraintLayout
    }
}
