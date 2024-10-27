package com.timeit.habito.ui.adapters


import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.timeit.habito.R
import com.timeit.habito.data.dataModels.HabitDataModel
import com.timeit.habito.data.dataModels.HabitsListDataModel
import com.timeit.habito.ui.activities.HabitDetailsActivity

class HabitsListAdapter(private var context: Context,private val habitsList: MutableList<HabitDataModel>): RecyclerView.Adapter<HabitsListAdapter.HabitViewHolder>() {
    class HabitViewHolder(itemView:View) :RecyclerView.ViewHolder(itemView){
        val habitNameTextView: TextView = itemView.findViewById(R.id.taskTitle)
        val habitImageView: ImageView = itemView.findViewById(R.id.imageIcon)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.habit_item, parent, false)
        return HabitViewHolder(view)
    }

    override fun getItemCount(): Int {
        return habitsList.size
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habitsList[position]
        holder.habitNameTextView.text = habit.habit_name

        val imageRes = getHabitImageResource(habit.habit_id, context)
        holder.habitImageView.setImageResource(imageRes)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, HabitDetailsActivity::class.java)
            intent.putExtra("habitName", habit.habit_name)
            intent.putExtra("habitImage", imageRes)
            intent.putExtra("habitId", habit.habit_id)
            intent.putExtra("habitDesc", habit.description)
            context.startActivity(intent)
        }
    }

    fun updateVehicleList(newList: List<HabitDataModel>) {
        habitsList.clear()
        habitsList.addAll(newList)
        notifyDataSetChanged()
    }

    fun getHabitImageResource(habitId: String, context: Context): Int {
        return when (habitId) {
            "7041c11c-2dfb-41b7-a371-721800a5b1f0" -> R.drawable.exercise
            "31d8b2d3-9a2b-4431-bc6b-ca863d7eb0ae" -> R.drawable.jogging
            "46bb5294-fafa-4435-addd-4a17390edb49" -> R.drawable.reading2
            "4ce4e2d4-3074-4793-82a7-908d5b67ad31" -> R.drawable.coding1
            "d6fe8c6a-c46c-4fe3-90e0-6c2575e01670" -> R.drawable.journaling
            "be8bb47e-652e-4f5c-9a3f-561c7244f66c" -> R.drawable.medicine2
            else -> R.drawable.timeit_icon_round
        }
    }
}