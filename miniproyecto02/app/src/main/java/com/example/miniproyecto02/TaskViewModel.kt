package com.example.miniproyecto02

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.miniproyecto02.data.Task
import com.example.miniproyecto02.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TaskViewModel(private val taskDao: TaskDao) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    private val _sortByStatus = MutableStateFlow(false)

    init {
        loadTasks()
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _sortByStatus.collect { sortByStatus ->
                if (sortByStatus) {
                    taskDao.getAllTasksSortedByStatus().collect { _tasks.value = it }
                } else {
                    taskDao.getAllTasks().collect { _tasks.value = it }
                }
            }
        }
    }

    fun toggleSortOrder() {
        _sortByStatus.value = !_sortByStatus.value
    }

    fun addTask(title: String, description: String) {
        viewModelScope.launch {
            taskDao.insertTask(Task(title = title, description = description))
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task)
        }
    }

    fun toggleTaskCompletion(task: Task) {
        viewModelScope.launch {
            taskDao.updateTask(task.copy(isCompleted = !task.isCompleted))
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            taskDao.deleteTask(task)
        }
    }

    fun deleteCompletedTasks() {
        viewModelScope.launch {
            taskDao.deleteCompletedTasks()
        }
    }
}

class TaskViewModelFactory(private val taskDao: TaskDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskViewModel(taskDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
