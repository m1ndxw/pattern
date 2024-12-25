package com.example.pattern

class TaskPresenter(private val view: TaskContract.View) : TaskContract.Presenter {
    private val tasks = mutableListOf<Task>()
    private var nextId = 1

    override fun addTask(description: String) {
        tasks.add(Task(id = nextId++, description = description))
        getAllTasks()
    }

    override fun toggleTaskCompletion(taskId: Int) {
        val task = tasks.find { it.id == taskId }
        task?.let {
            it.isCompleted = !it.isCompleted
            getAllTasks()
        }
    }

    override fun getAllTasks() {
        view.displayTasks(tasks)
    }
}