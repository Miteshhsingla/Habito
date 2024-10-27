package com.timeit.habito.data

import com.timeit.habito.data.dataModels.AddHabitRequest
import com.timeit.habito.data.dataModels.HabitDataModel
import com.timeit.habito.data.dataModels.LeaderboardResponse
import com.timeit.habito.data.dataModels.LoginRequest
import com.timeit.habito.data.dataModels.LoginResponse
import com.timeit.habito.data.dataModels.MyHabitsListResponse
import com.timeit.habito.data.dataModels.SignUpRequest
import okhttp3.MultipartBody
import okhttp3.MultipartBody.*
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {

    @POST("user/login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @POST("user/signup")
    fun signup(
        @Body signupRequest: SignUpRequest
    ): Call<Void>

    @GET("habits")
    fun getHabits(
        @Header("Authorization") authToken: String,
    ): Call<List<HabitDataModel>>


    @POST("user/habit")
    fun addHabitToProfile(
        @Header("Authorization") authToken: String,
        @Body habitAddRequest: AddHabitRequest
    ): Call<Void>

    @GET("user/habits")
    fun getUserHabits(
        @Header("Authorization") authToken: String
    ): Call<List<MyHabitsListResponse>>

    @Multipart
    @POST("user/habit/log")
    fun uploadHabitImage(
        @Header("Authorization") authToken: String,
        @Part("user_habit_id") habitUserId: RequestBody,
        @Part imageFile: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("leaderboard")
    fun getLeaderBoardData(
        @Header("Authorization") authToken: String,
        @Query("habit_id") habitId: String
        ): Call<List<LeaderboardResponse>>

}

