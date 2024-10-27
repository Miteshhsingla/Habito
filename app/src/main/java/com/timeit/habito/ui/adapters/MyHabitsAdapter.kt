package com.timeit.habito.ui.adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.timeit.habito.R
import com.timeit.habito.data.dataModels.MyHabitsListResponse
import com.timeit.habito.ui.activities.VerifyHabitActivity


class MyHabitsAdapter( private var context: Context, private var habitsList: MutableList<MyHabitsListResponse>) : RecyclerView.Adapter<MyHabitsAdapter.tasksViewHolder>() {

    class tasksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val habitName: TextView = itemView.findViewById(R.id.habit_name)
        val habitStreak: TextView = itemView.findViewById(R.id.habit_streak)
        val habitImage: ImageView = itemView.findViewById(R.id.habit_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): tasksViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_habit, parent, false)
        return tasksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return habitsList.size
    }

    fun getItem(position: Int): MyHabitsListResponse {
        return habitsList[position]
    }

    fun removeItem(position: Int) {
        habitsList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: tasksViewHolder, position: Int) {
        val task = habitsList[position]

        holder.habitName.text = task.habit_name
        holder.habitStreak.text = task.current_streak.toString()
        val imageRes = getHabitImageResource(task.habit_id, context)
        holder.habitImage.setImageResource(imageRes)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, VerifyHabitActivity::class.java)
            intent.putExtra("habitId", task.habit_id)
            intent.putExtra("habitUserId", task.user_habit_id)
            intent.putExtra("habitName", task.habit_name)
            intent.putExtra("habitDesc", task.description)
            intent.putExtra("habitStartDate", task.start_date.toString())
            intent.putExtra("habitStreak", task.current_streak.toString())
            intent.putExtra("habitImage", imageRes)
            context.startActivity(intent)
        }


    }

    fun updateList(tasksList: List<MyHabitsListResponse>) {
        habitsList.clear()
        habitsList.addAll(tasksList)
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