package com.timeit.habito.data.dataModels

data class HabitAnalyticsData(
    val habit_name: String,
    val breakdown: Map<String, Boolean>
)
