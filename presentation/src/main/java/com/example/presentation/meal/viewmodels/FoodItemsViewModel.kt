package com.example.presentation.meal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.meal.FoodItem
import com.example.domain.usecase.meal.AddFoodItemsUseCase
import com.example.domain.usecase.meal.DeleteFoodItemUseCase
import com.example.domain.usecase.meal.GetFoodItemsUseCase
import com.example.domain.usecase.meal.UpdateFoodItemUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FoodItemsViewModel @Inject constructor(
    private val getFoodItems: GetFoodItemsUseCase,
    private val addFoodItemUseCase: AddFoodItemsUseCase,
    private val updateFoodItemUseCase: UpdateFoodItemUseCase,
    private val deleteFoodItemUseCase: DeleteFoodItemUseCase
) : ViewModel() {

    data class UiState(
        val foodItems: List<FoodItem> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val selectedFoodItem: FoodItem? = null,
        val isEditDialogOpen: Boolean = false,
        val searchQuery: String = ""
    ) {
        val filteredItems: List<FoodItem>
            get() = if (searchQuery.isBlank()) {
                foodItems.filter { !it.isDeleted }
            } else {
                foodItems.filter {
                    !it.isDeleted && it.name.contains(searchQuery, ignoreCase = true)
                }
            }
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadFoodItems()
    }

    private fun loadFoodItems() {
        getFoodItems()
            .onEach { items ->
                _uiState.update { it.copy(foodItems = items, isLoading = false) }
            }
            .launchIn(viewModelScope)
    }

    fun selectFoodItem(item: FoodItem?) {
        _uiState.update { it.copy(selectedFoodItem = item, isEditDialogOpen = item != null) }
    }

    fun dismissDialog() {
        _uiState.update { it.copy(isEditDialogOpen = false, selectedFoodItem = null) }
    }

    fun createFoodItem(
        name: String,
        description: String,
        calories: Double,
        protein: Double,
        fats: Double,
        carbs: Double,
        serving: Double
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val foodItem = FoodItem(
                id = UUID.randomUUID().toString(),
                name = name,
                description = description,
                caloriesPer100g = calories,
                proteinPer100g = protein,
                fatsPer100g = fats,
                carbsPer100g = carbs,
                servingDefaultGrams = serving
            )
            addFoodItemUseCase(foodItem).fold(
                onSuccess = {
                    dismissDialog()
                    loadFoodItems()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
            )
        }
    }

    fun updateFoodItem(
        id: String,
        name: String,
        description: String,
        calories: Double,
        protein: Double,
        fats: Double,
        carbs: Double,
        serving: Double
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val foodItem = FoodItem(
                id = id,
                name = name,
                description = description,
                caloriesPer100g = calories,
                proteinPer100g = protein,
                fatsPer100g = fats,
                carbsPer100g = carbs,
                servingDefaultGrams = serving
            )
            updateFoodItemUseCase(id, foodItem).fold(
                onSuccess = {
                    dismissDialog()
                    loadFoodItems()
                },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
            )
        }
    }

    fun deleteFoodItemById(id: String) {
        viewModelScope.launch {
            deleteFoodItemUseCase(id).fold(
                onSuccess = { loadFoodItems() },
                onFailure = { e ->
                    _uiState.update { it.copy(error = e.message) }
                }
            )
        }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
