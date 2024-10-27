package com.timeit.habito.ui.activities

import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.timeit.habito.R
import com.timeit.habito.data.api.HabitsRepository
import com.timeit.habito.databinding.ActivityHabitDetailsBinding
import com.timeit.habito.utils.TokenManager
import com.timeit.habito.workManager.HabitReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HabitDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHabitDetailsBinding
    private lateinit var habitId: String
    private lateinit var habitDescription: String
    private lateinit var habitsRepository: HabitsRepository
    private var selectedTimeInMillis: Long = -1L

    @Inject
    lateinit var tokenManager: TokenManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHabitDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        habitsRepository = HabitsRepository()
        tokenManager = TokenManager(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }


        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Create Habit"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_android_back)
        }

        val habitName = intent.getStringExtra("habitName").toString()
        val habitImage = intent.getIntExtra("habitImage", R.drawable.timeit_icon_round)
        habitId = intent.getStringExtra("habitId").toString()
        habitDescription = intent.getStringExtra("habitDesc").toString()

        binding.habitName.text = habitName
        binding.habitImage.setImageResource(habitImage)
        binding.habitDesc.text = habitDescription

        // Time picker dialog to set reminder time
        binding.dateAndTime.setOnClickListener {
            showTimePickerDialog(binding.dateAndTime)
        }

        val token = tokenManager.getToken()

        binding.AddHabitBtn.setOnClickListener {
            if (selectedTimeInMillis != -1L) {
                saveHabitTimeInPreferences(habitId, selectedTimeInMillis)
                setupWorkManagerForHabit(habitId, habitName, selectedTimeInMillis)
                habitsRepository.addHabitToUserProfile(this, token, habitId)

                Toast.makeText(this, "Habit Created Successfully", Toast.LENGTH_SHORT).show()
                val intent = Intent(this,MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Please select a time for reminder.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showTimePickerDialog(editText: EditText) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                editText.setText(formattedTime)

                selectedTimeInMillis = calculateTimeInMillis(selectedHour, selectedMinute)
            },
            hour,
            minute,
            true
        )

        timePickerDialog.show()
    }

    private fun calculateTimeInMillis(hour: Int, minute: Int): Long {
        val currentCalendar = Calendar.getInstance()
        val reminderCalendar = Calendar.getInstance()

        reminderCalendar.set(Calendar.HOUR_OF_DAY, hour)
        reminderCalendar.set(Calendar.MINUTE, minute)
        reminderCalendar.set(Calendar.SECOND, 0)

        if (reminderCalendar.before(currentCalendar)) {
            reminderCalendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return reminderCalendar.timeInMillis
    }

    private fun saveHabitTimeInPreferences(userHabitId: String, timeInMillis: Long) {
        val sharedPreferences = getSharedPreferences("HabitPrefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putLong(userHabitId, timeInMillis)
        editor.apply()
    }

    private fun getHabitTimeFromPreferences(userHabitId: String): Long {
        val sharedPreferences = getSharedPreferences("HabitPrefs", MODE_PRIVATE)
        return sharedPreferences.getLong(userHabitId, -1)
    }

    private fun setupWorkManagerForHabit(habitId: String, habitName: String, reminderTimeInMillis: Long) {
        val currentTime = System.currentTimeMillis()
        val delay = reminderTimeInMillis - currentTime

        val habitData = Data.Builder()
            .putString("user_habit_id", habitId)
            .putString("habit_name", habitName)
            .build()

        val workRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(habitData)
            .addTag(habitId)
            .build()

        WorkManager.getInstance(this)
            .enqueueUniqueWork(habitId, ExistingWorkPolicy.REPLACE, workRequest)
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
