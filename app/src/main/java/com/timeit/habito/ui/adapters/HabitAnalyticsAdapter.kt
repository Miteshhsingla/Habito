package com.timeit.habito.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timeit.habito.R
import com.timeit.habito.data.dataModels.HabitAnalyticsData


class HabitAnalyticsAdapter(private val habits: List<HabitAnalyticsData>) : RecyclerView.Adapter<HabitAnalyticsAdapter.HabitViewHolder>() {

    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.titleTextView)
        private val streakIcons: List<ImageView> = listOf(
            itemView.findViewById(R.id.streak_icon_monday),
            itemView.findViewById(R.id.streak_icon_tuesday),
            itemView.findViewById(R.id.streak_icon_wednesday),
            itemView.findViewById(R.id.streak_icon_thursday),
            itemView.findViewById(R.id.streak_icon_friday),
            itemView.findViewById(R.id.streak_icon_saturday),
            itemView.findViewById(R.id.streak_icon_sunday)
        )

        fun bind(habit: HabitAnalyticsData) {
            title.text = habit.name

            for (i in habit.streakValues.indices) {
                val icon = streakIcons[i]
                if (habit.streakValues[i] == 1) {
                    icon.setImageResource(R.drawable.streak)
                    icon.alpha = 1f
                } else {
                    icon.setImageResource(R.drawable.streak)
                    icon.alpha = 0.3f
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.streak_data_item, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    override fun getItemCount(): Int = habits.size
}
