package com.timeit.habito.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timeit.habito.R
import com.timeit.habito.data.dataModels.HabitAnalyticsData

class HabitAnalyticsAdapter(
    private var habits: List<HabitAnalyticsData>
) : RecyclerView.Adapter<HabitAnalyticsAdapter.HabitViewHolder>() {

    // ViewHolder to represent each habit item
    class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val streakIcons: List<ImageView> = listOf(
            itemView.findViewById(R.id.streak_icon_monday),
            itemView.findViewById(R.id.streak_icon_tuesday),
            itemView.findViewById(R.id.streak_icon_wednesday),
            itemView.findViewById(R.id.streak_icon_thursday),
            itemView.findViewById(R.id.streak_icon_friday),
            itemView.findViewById(R.id.streak_icon_saturday),
            itemView.findViewById(R.id.streak_icon_sunday)
        )

        // Bind the data to the views
        fun bind(habit: HabitAnalyticsData) {
            // Set habit ID as title (or replace it with actual name if available)
            titleTextView.text = habit.habit_name

            // Map breakdown values to streak icons
            val streakValues = listOf(
                habit.breakdown["Monday"] ?: false,
                habit.breakdown["Tuesday"] ?: false,
                habit.breakdown["Wednesday"] ?: false,
                habit.breakdown["Thursday"] ?: false,
                habit.breakdown["Friday"] ?: false,
                habit.breakdown["Saturday"] ?: false,
                habit.breakdown["Sunday"] ?: false
            )

            // Set streak icons based on streakValues
            streakValues.forEachIndexed { index, isCompleted ->
                if (index < streakIcons.size) {
                    val icon = streakIcons[index]
                    if (isCompleted) {
                        icon.setImageResource(R.drawable.streak) // Set completed icon
                        icon.alpha = 1.0f // Fully opaque
                    } else {
                        icon.setImageResource(R.drawable.streak) // Set default icon
                        icon.alpha = 0.5f // 50% opacity
                    }
                }
            }
        }
    }

    // Inflate the item layout and create ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.streak_data_item, parent, false)
        return HabitViewHolder(view)
    }

    // Bind the data at the specified position
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
    }

    // Return the size of the habit list
    override fun getItemCount(): Int = habits.size

    // Update data dynamically
    fun updateData(newHabits: List<HabitAnalyticsData>) {
        habits = newHabits
        notifyDataSetChanged()
    }
}
