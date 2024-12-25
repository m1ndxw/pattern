package com.example.pattern

interface TaskContract {
    interface View {
        fun displayTasks(tasks: List<Task>)
    }

    interface Presenter {
        fun addTask(description: String)
        fun toggleTaskCompletion(taskId: Int)
        fun getAllTasks()
    }
}
