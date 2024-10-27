package com.timeit.habito.ui.activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timeit.habito.R
import com.timeit.habito.data.api.HabitsRepository
import com.timeit.habito.data.dataModels.LeaderboardResponse
import com.timeit.habito.databinding.ActivityVerifyHabitBinding
import com.timeit.habito.ui.adapters.LeaderBoardAdapter
import com.timeit.habito.ui.adapters.MyHabitsAdapter
import com.timeit.habito.utils.TokenManager
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import javax.inject.Inject

class VerifyHabitActivity : AppCompatActivity() {
    private lateinit var binding: ActivityVerifyHabitBinding
    private lateinit var habitId: String
    private lateinit var habitUserId: String
    private lateinit var habitName: String
    private lateinit var habitDesc: String
    private lateinit var habitStartDate: String
    private lateinit var currentPhotoPath: String
    private lateinit var habitStreak: String
    private lateinit var habitsRepository: HabitsRepository
    private lateinit var leaderBoardAdapter: LeaderBoardAdapter
    private lateinit var recyclerView : RecyclerView
    @Inject
    lateinit var tokenManager: TokenManager

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
    }
    private val CAMERA_REQUEST_CODE = 100


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVerifyHabitBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tokenManager = TokenManager(this)
        habitsRepository = HabitsRepository()

        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            title = "Log a Habit"
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_android_back)
        }

        habitId = intent.getStringExtra("habitId").toString()
        habitUserId = intent.getStringExtra("habitUserId").toString()
        habitName = intent.getStringExtra("habitName").toString()
        habitDesc = intent.getStringExtra("habitDesc").toString()
        habitStartDate = intent.getStringExtra("habitStartDate").toString()
        habitStreak = intent.getStringExtra("habitStreak").toString()
        val habitImage = intent.getIntExtra("habitImage", R.drawable.timeit_icon_round)

        binding.habitImage.setImageResource(habitImage)
        binding.habitName.text = habitName
        binding.tooltip.text = habitDesc

        binding.userNameLeaderboard.text = tokenManager.getUsername()
        binding.userStreakLeaderboard.text = habitStreak

        binding.VerifyHabitBtn.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), CAMERA_REQUEST_CODE)
            } else {
                dispatchTakePictureIntent()
            }
        }

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        leaderBoardAdapter = LeaderBoardAdapter(this, mutableListOf())
        recyclerView.adapter = leaderBoardAdapter


        val authToken = tokenManager.getToken()
        if(authToken != null){
            habitsRepository.getLeaderBoardData(this,authToken,habitId) { leaderBoardList, errorMessage ->
                if (leaderBoardList != null) {
                    displayLeaderBoardData(leaderBoardList)
                } else if (errorMessage != null) {
                    println("Error: $errorMessage")
                }
            }
        }

    }

    private fun displayLeaderBoardData(leaderBoardList: List<LeaderboardResponse>) {
        leaderBoardAdapter.updateList(leaderBoardList)
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File? = try {
                createImageFile()
            } catch (ex: IOException) {
                null
            }
            photoFile?.let {
                val photoURI: Uri = FileProvider.getUriForFile(
                    this,
                    "${packageName}.fileprovider",
                    it
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = externalCacheDir
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
            binding.previewImage.setImageBitmap(bitmap)
            binding.previewImage.visibility = View.VISIBLE
            uploadImageWithHabitId(currentPhotoPath)
        }
    }

    private fun uploadImageWithHabitId(imagePath: String) {

        val habitUserIdRequestBody = RequestBody.create("text/plain".toMediaTypeOrNull(), habitUserId)

        val imageFile = File(imagePath)
        val imageRequestBody = RequestBody.create("image/jpeg".toMediaTypeOrNull(), imageFile)
        val imagePart = MultipartBody.Part.createFormData("image_file", imageFile.name, imageRequestBody)


        val token = tokenManager.getToken()
        if(token != null){
            habitsRepository.postUserHabitLog(token, this, imagePart, habitUserIdRequestBody, object :
                HabitsRepository.ApiCallback {
                override fun onLoading(isLoading: Boolean) {
                    if (isLoading) {
                        binding.loadingOverlay.visibility = View.VISIBLE
                    } else {
                        binding.loadingOverlay.visibility = View.GONE
                    }
                }
            })
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent()
            } else {
                Toast.makeText(this, "Camera permission is required to take pictures", Toast.LENGTH_SHORT).show()
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
