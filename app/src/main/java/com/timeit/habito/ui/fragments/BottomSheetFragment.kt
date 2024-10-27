package com.timeit.habito.ui.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.timeit.habito.R
import com.timeit.habito.data.api.HabitsRepository
import com.timeit.habito.data.dataModels.HabitDataModel
import com.timeit.habito.data.dataModels.HabitsListDataModel
import com.timeit.habito.databinding.FragmentBottomSheetBinding
import com.timeit.habito.ui.adapters.HabitsListAdapter
import com.timeit.habito.utils.TokenManager
import javax.inject.Inject

class BottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var habitsListAdapter: HabitsListAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var habitsRepository: HabitsRepository
    private lateinit var token: String


    private val dismissReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            dismiss()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        token = requireArguments().getString("token").toString()
        habitsRepository = HabitsRepository()

        habitsRepository.fetchHabits(token) { habitsList, errorMessage ->
            if (habitsList != null) {
                displayHabits(habitsList)
            } else if (errorMessage != null) {
                showError(errorMessage)
            }
        }

        binding.crossButton.setOnClickListener{
            dismiss()
        }

        recyclerView = binding.listhabitsrecycler
        recyclerView.layoutManager = LinearLayoutManager(context)

//        val habitsList = listOf(
//            HabitsListDataModel("Morning Jog", R.drawable.timeit_icon_round),
//            HabitsListDataModel("Read a Book", R.drawable.timeit_icon_round),
//            HabitsListDataModel("Meditation", R.drawable.timeit_icon_round),
//            HabitsListDataModel("Drink Water", R.drawable.timeit_icon_round),
//            HabitsListDataModel("Yoga", R.drawable.timeit_icon_round)
//        )

        habitsListAdapter = HabitsListAdapter(requireContext(), mutableListOf())
        recyclerView.adapter = habitsListAdapter

        view.post {
            val bottomSheet = dialog?.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            if (bottomSheet != null) {
                val behavior = BottomSheetBehavior.from(bottomSheet)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                behavior.isDraggable = false
            }
        }
    }

    private fun displayHabits(habitsList: List<HabitDataModel>) {
        habitsListAdapter.updateVehicleList(habitsList)
//        habitsList.forEach { habit ->
//            println("Habit ID: ${habit.id}, Title: ${habit.title}, Description: ${habit.description}")
//        }

    }

    private fun showError(errorMessage: String) {
        println("Error fetching habits: $errorMessage")
    }

    @androidx.annotation.RequiresApi(android.os.Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()
        val filter = IntentFilter("com.cuebit.io.ACTION_DISMISS_BOTTOM_SHEET")
        requireContext().registerReceiver(dismissReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        requireContext().unregisterReceiver(dismissReceiver)
    }
}
