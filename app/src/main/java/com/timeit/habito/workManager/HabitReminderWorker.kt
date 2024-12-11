package com.timeit.habito.workManager

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.timeit.habito.R
import java.util.concurrent.TimeUnit

class HabitReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("HabitPrefs", Context.MODE_PRIVATE)

    override fun doWork(): Result {
        val habitId = inputData.getString("user_habit_id") ?: return Result.failure()
        val habitName = inputData.getString("habit_name") ?: "your habit"

        showNotification(habitName)
        rescheduleNextDay(habitId)

        return Result.success()
    }


    private fun showNotification(habitName: String) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "habit_reminder_channel"

        // Create notification channel for Android O and above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Habit Reminder",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        // Build the notification
        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.timeit_icon_round)
            .setContentTitle("Habit Reminder")
            .setContentText("It's time to complete your habit: $habitName")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(habitName.hashCode(), notification)
    }

    private fun rescheduleNextDay(habitId: String) {
        val workManager = WorkManager.getInstance(applicationContext)
        val workRequest = OneTimeWorkRequestBuilder<HabitReminderWorker>()
            .setInitialDelay(24, TimeUnit.HOURS)
            .setInputData(inputData)
            .build()

        workManager.enqueueUniqueWork(habitId, ExistingWorkPolicy.REPLACE, workRequest)
    }

}
