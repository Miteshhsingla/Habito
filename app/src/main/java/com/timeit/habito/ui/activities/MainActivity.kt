package com.timeit.habito.ui.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.timeit.habito.R
import com.timeit.habito.data.api.HabitsRepository
import com.timeit.habito.data.dataModels.MyHabitsListResponse
import com.timeit.habito.databinding.ActivityMainBinding
import com.timeit.habito.ui.adapters.HabitsListAdapter
import com.timeit.habito.ui.adapters.MyHabitsAdapter
import com.timeit.habito.ui.fragments.BottomSheetFragment
import com.timeit.habito.utils.TokenManager
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    @Inject
    lateinit var tokenManager: TokenManager
    private lateinit var myHabitsAdapter: MyHabitsAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var habitsRepository: HabitsRepository
    private lateinit var habitsReceiver: BroadcastReceiver
    private lateinit var authToken: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        habitsRepository = HabitsRepository()
        tokenManager = TokenManager(this)
        authToken = tokenManager.getToken().toString()
        val username = tokenManager.getUsername()

        binding.welcomeText.text = username
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        myHabitsAdapter = MyHabitsAdapter(this@MainActivity, mutableListOf())
        recyclerView.adapter = myHabitsAdapter

        setupSwipeToMarkAsDone()

        binding.fab.setOnClickListener {
            val bottomSheetFragment = BottomSheetFragment()
            val bundle = Bundle()
            bundle.putString("token", tokenManager.getToken())
            bottomSheetFragment.arguments = bundle
            bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
        }
        binding.todayDate.text = getTodayDate()

        binding.profilePicture.setOnClickListener{
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }
        binding.streakAnalytics.setOnClickListener{
            val intent = Intent(this,StreakAnalytics::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        getUserHabitsData()
    }

    private fun getUserHabitsData() {
        habitsRepository.getUserHabits(authToken) { userHabitsList, errorMessage ->
            if (userHabitsList != null) {
                displayUserHabits(userHabitsList)
            } else if (errorMessage != null) {
                println("Error: $errorMessage")
            }
        }
    }

    private fun displayUserHabits(userHabitsList: List<MyHabitsListResponse>) {
        myHabitsAdapter.updateList(userHabitsList)

        if (userHabitsList.isEmpty()) {
            showEmptyState()
        } else {
            hideEmptyState()
        }
    }

    private fun showEmptyState() {
        binding.emptyState.visibility = View.VISIBLE
        binding.recyclerView.visibility = View.GONE
    }

    private fun hideEmptyState() {
        binding.emptyState.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    override fun onPause() {
        super.onPause()

    }


    private fun setupSwipeToMarkAsDone() {
        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false // We are not implementing move functionality
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val habitToMarkDone = myHabitsAdapter.getItem(position)

                // Mark the habit as done (update your data model or database)
//                markHabitAsDone(habitToMarkDone)

                // Update the UI (for instance, notify the adapter of the change)
                myHabitsAdapter.notifyItemChanged(position)

                // Optionally, show a toast message or some user feedback
                Toast.makeText(this@MainActivity, "Habit marked as done!", Toast.LENGTH_SHORT).show()
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                val itemView = viewHolder.itemView

                // Create a light gray background
                val background = ColorDrawable(Color.LTGRAY)

                // Draw the background only when swiping right
                if (dX > 0) { // Swiping to the right
                    background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                } else { // Clean up the background
                    background.setBounds(0, 0, 0, 0)
                }
                background.draw(c)

                // Draw the tick icon
                if (isCurrentlyActive && dX > 0) {
                    val drawable: Drawable? = VectorDrawableCompat.create(resources, R.drawable.ic_tick, null) // Replace with your tick icon drawable
                    val iconMargin = 16 // Margin between the edge and the icon
                    val iconSize = 40 // Size of the tick icon

                    // Position the icon
                    drawable?.setBounds(
                        itemView.left + iconMargin,
                        itemView.top + (itemView.height - iconSize) / 2,
                        itemView.left + iconMargin + iconSize,
                        itemView.top + (itemView.height + iconSize) / 2
                    )
                    drawable?.draw(c)
                }

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }

        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(recyclerView)
    }

    fun getTodayDate(): String {
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, d MMM yyyy", Locale.ENGLISH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val daySuffix = getDayOfMonthSuffix(dayOfMonth)
        val formattedDate = dateFormat.format(calendar.time)

        return formattedDate.replaceFirst("\\d+".toRegex(), "$dayOfMonth$daySuffix")
    }

    fun getDayOfMonthSuffix(day: Int): String {
        return when {
            day in 11..13 -> "th"
            day % 10 == 1 -> "st"
            day % 10 == 2 -> "nd"
            day % 10 == 3 -> "rd"
            else -> "th"
        }
    }


}