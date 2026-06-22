package com.example.presentation.workout.viewmodels

import androidx.lifecycle.ViewModel
import com.example.domain.manager.WorkoutManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class WorkoutManagerViewModel @Inject constructor(
    val workoutManager: WorkoutManager
) : ViewModel()