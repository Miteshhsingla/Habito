package com.timeit.habito.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timeit.habito.R
import com.timeit.habito.data.dataModels.HabitAnalyticsData
import com.timeit.habito.databinding.ActivityStreakAnalyticsBinding
import com.timeit.habito.ui.adapters.HabitAnalyticsAdapter
import kotlin.random.Random

class StreakAnalytics : AppCompatActivity() {
    private lateinit var binding:ActivityStreakAnalyticsBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAnalyticsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreakAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)


        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Streak Analytics"
            setDisplayHomeAsUpEnabled(true)
            setSupportActionBar(binding.toolbar)
            setHomeAsUpIndicator(R.drawable.ic_android_back)
        }


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val habits = generateDummyData()
        habitAdapter = HabitAnalyticsAdapter(habits)

        recyclerView.adapter = habitAdapter

    }

    fun generateDummyData(): List<HabitAnalyticsData> {
        val habitNames = listOf("Jogging", "Coding", "Reading")
        val random = Random

        return habitNames.map { name ->
            HabitAnalyticsData(name, List(7) { random.nextInt(0, 2) })
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}