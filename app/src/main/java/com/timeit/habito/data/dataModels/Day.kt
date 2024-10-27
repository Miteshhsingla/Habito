package com.timeit.habito.data.dataModels

import java.io.Serializable

data class Day(
    val dayOfWeek: String,
    val dayNumber: Int,
    val dayMonth: String,
    val year: Int,
    val isToday: Boolean
) : Serializable