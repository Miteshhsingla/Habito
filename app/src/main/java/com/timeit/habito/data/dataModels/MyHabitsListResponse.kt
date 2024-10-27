package com.timeit.habito.data.dataModels

import java.util.Date

data class MyHabitsListResponse(
    val user_habit_id: String,
    val habit_id: String,
    val start_date : Date,
    val current_streak: Int,
    val habit_name: String,
    val description : String
)

