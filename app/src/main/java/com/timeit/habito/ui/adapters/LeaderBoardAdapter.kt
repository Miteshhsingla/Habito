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
import com.timeit.habito.data.dataModels.LeaderboardResponse
import com.timeit.habito.data.dataModels.MyHabitsListResponse
import kotlin.random.Random


class LeaderBoardAdapter( private var context: Context, private var habitsList: MutableList<LeaderboardResponse>) : RecyclerView.Adapter<LeaderBoardAdapter.tasksViewHolder>() {

    class tasksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.player_name)
        val userStreak: TextView = itemView.findViewById(R.id.player_streak)
        val userImage: ImageView = itemView.findViewById(R.id.profile_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): tasksViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.rank_item, parent, false)
        return tasksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return habitsList.size
    }

    fun removeItem(position: Int) {
        habitsList.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun onBindViewHolder(holder: tasksViewHolder, position: Int) {
        val task = habitsList[position]

        holder.userName.text = task.username
        holder.userStreak.text = task.current_streak.toString()

        val images = arrayOf(R.drawable.user_thumbnail, R.drawable.ut2, R.drawable.ut3)
        val randomIndex = Random.nextInt(images.size)
        holder.userImage.setImageResource(images[randomIndex])
    }

    fun updateList(tasksList: List<LeaderboardResponse>) {
        habitsList.clear()
        habitsList.addAll(tasksList)
        notifyDataSetChanged()
    }




}