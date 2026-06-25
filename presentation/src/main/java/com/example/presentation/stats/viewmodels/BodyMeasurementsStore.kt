package com.example.presentation.stats.viewmodels

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDate

object BodyMeasurementsStore {
    data class Entry(
        val id: Long,
        val date: LocalDate,
        val value: Double
    )

    private val _data = MutableStateFlow<Map<String, MutableList<Entry>>>(emptyMap())
    val data: StateFlow<Map<String, List<Entry>>> = _data.asStateFlow()

    private var nextId = 1L

    fun getEntries(type: String): List<Entry> {
        return (_data.value[type] ?: emptyList()).sortedByDescending { it.date }
    }

    fun add(type: String, value: Double, date: LocalDate = LocalDate.now()) {
        val current = _data.value.toMutableMap()
        val list = current.getOrPut(type) { mutableListOf() }
        list.add(Entry(nextId++, date, value))
        _data.value = current
    }

    fun delete(type: String, id: Long) {
        val current = _data.value.toMutableMap()
        current[type]?.removeAll { it.id == id }
        _data.value = current
    }
}
