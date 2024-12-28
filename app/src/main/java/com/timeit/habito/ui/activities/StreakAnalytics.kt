package com.timeit.habito.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.timeit.habito.R
import com.timeit.habito.data.RetrofitClient
import com.timeit.habito.data.dataModels.HabitAnalyticsData
import com.timeit.habito.databinding.ActivityStreakAnalyticsBinding
import com.timeit.habito.ui.adapters.HabitAnalyticsAdapter
import com.timeit.habito.utils.TokenManager
import kotlinx.coroutines.launch
import javax.inject.Inject

class StreakAnalytics : AppCompatActivity() {
    private lateinit var binding: ActivityStreakAnalyticsBinding
    private lateinit var habitAdapter: HabitAnalyticsAdapter
    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStreakAnalyticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        // Toolbar setup
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Streak Analytics"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_android_back)
        }

        // RecyclerView setup
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        habitAdapter = HabitAnalyticsAdapter(emptyList())
        binding.recyclerView.adapter = habitAdapter

        // Fetch data from API
        fetchHabitAnalyticsData()
    }

    private fun fetchHabitAnalyticsData() {
        lifecycleScope.launch {
            val token = tokenManager.getToken()

            try {
                val response = RetrofitClient.apiService.getHabitAnalytics("Bearer $token")
                if (response.isSuccessful && response.body() != null) {
                    val habitAnalytics = response.body()!!
                    habitAdapter.updateData(habitAnalytics)
                } else {
                    Log.e("StreakAnalytics", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("StreakAnalytics", "Exception: ${e.message}")
            }
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
