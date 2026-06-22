package com.example.presentation.meal.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.domain.model.meal.FoodItem
import com.example.domain.usecase.meal.GetFoodItemsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FoodItemsViewModel @Inject constructor(
    private val getFoodItemsUseCase: GetFoodItemsUseCase
) : ViewModel() {

    data class UiState(
        val items: List<FoodItem> = emptyList(),
        val filteredItems: List<FoodItem> = emptyList(),
        val recentItems: List<FoodItem> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadFoodItems()
    }

    fun loadFoodItems() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            getFoodItemsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message ?: "Ошибка загрузки") }
                }
                .onEach { items ->
                    // Последние 3-5 добавленных как "recent" (в реальном приложении - по дате использования)
                    val recent = items.take(4)
                    val other = items.drop(4)
                    _uiState.update {
                        it.copy(
                            items = items,
                            filteredItems = items,
                            recentItems = recent,
                            isLoading = false,
                            error = null
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    fun search(query: String) {
        _uiState.update { state ->
            val filtered = if (query.isBlank()) {
                state.items
            } else {
                state.items.filter { it.name.contains(query, ignoreCase = true) }
            }
            state.copy(filteredItems = filtered)
        }
    }

    fun refresh() {
        loadFoodItems()
    }
}
