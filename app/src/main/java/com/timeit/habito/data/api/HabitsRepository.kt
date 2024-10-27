package com.timeit.habito.data.api

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.timeit.habito.data.RetrofitClient
import com.timeit.habito.data.dataModels.AddHabitRequest
import com.timeit.habito.data.dataModels.HabitDataModel
import com.timeit.habito.data.dataModels.LeaderboardResponse
import com.timeit.habito.data.dataModels.LogHabitResponse
import com.timeit.habito.data.dataModels.MyHabitsListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part

class HabitsRepository {

    fun fetchHabits(authToken: String?, callback: (List<HabitDataModel>?, String?) -> Unit) {

        val call = RetrofitClient.apiService.getHabits("Bearer $authToken")

        call.enqueue(object : Callback<List<HabitDataModel>> {
            override fun onResponse(
                call: Call<List<HabitDataModel>>,
                response: Response<List<HabitDataModel>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    println("Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<HabitDataModel>>, t: Throwable) {
                callback(null, t.message)
            }
        })
    }


    fun addHabitToUserProfile(context: Context, authToken: String?, habitId: String) {
        val request = AddHabitRequest(habit_id = habitId)

        val call = RetrofitClient.apiService.addHabitToProfile("Bearer $authToken", request)
        call.enqueue(object : Callback<Void> {
            override fun onResponse(
                call: Call<Void>,
                response: Response<Void>
            ) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Habit Added Successfully", Toast.LENGTH_SHORT).show()
                    LocalBroadcastManager.getInstance(context).sendBroadcast(Intent("habitsAdded"))

                } else {
                    Toast.makeText(context, "Failed to add habit", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun getUserHabits(authToken: String?, callback: (List<MyHabitsListResponse>?, String?) -> Unit) {
        val call =RetrofitClient.apiService.getUserHabits("Bearer $authToken")
        call.enqueue(object : Callback<List<MyHabitsListResponse>> {
            override fun onResponse(
                call: Call<List<MyHabitsListResponse>>,
                response: Response<List<MyHabitsListResponse>>
            ) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    callback(null, "Error: ${response.code()} - ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<MyHabitsListResponse>>, t: Throwable) {
                callback(null, t.message)
            }
        })
    }

    interface ApiCallback {
        fun onLoading(isLoading: Boolean)
    }

    fun postUserHabitLog(
        token: String,
        context: Context,
        imagePart: MultipartBody.Part,
        habitUserId: RequestBody,
        callback: ApiCallback
    ) {
        callback.onLoading(true)

        val call = RetrofitClient.apiService.uploadHabitImage("Bearer $token", habitUserId, imagePart)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                callback.onLoading(false)

                if (response.isSuccessful) {
                    try {
                        val responseBody = response.body()?.string()
                        if (responseBody != null) {
                            val jsonObject = JSONObject(responseBody)
                            val detailMessage = jsonObject.getString("detail")
                            Toast.makeText(context, detailMessage, Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Response is empty", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Error parsing response", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    try {
                        val errorBody = response.errorBody()?.string()
                        val jsonObject = JSONObject(errorBody)
                        val errorMessage = jsonObject.getString("detail")
                        Toast.makeText(context, errorMessage ?: "Upload failed", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Error parsing error response", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                callback.onLoading(false)
                Toast.makeText(context, "Network error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    fun getLeaderBoardData(context: Context, token:String,habitId:String, callback: (List<LeaderboardResponse>?, String?) -> Unit){
        RetrofitClient.apiService.getLeaderBoardData("Bearer $token", habitId).enqueue(object : Callback<List<LeaderboardResponse>> {
            override fun onResponse(call: Call<List<LeaderboardResponse>>, response: Response<List<LeaderboardResponse>>) {
                if (response.isSuccessful) {
                    callback(response.body(), null)
                } else {
                    Toast.makeText(context, "Failed to fetch leaderboard data: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<LeaderboardResponse>>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}



