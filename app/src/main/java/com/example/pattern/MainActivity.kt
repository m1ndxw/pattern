package com.example.pattern

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.pattern.ui.theme.PatternTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border

class MainActivity : ComponentActivity(), TaskContract.View {
    private lateinit var presenter: TaskContract.Presenter
    private val tasksState = mutableStateListOf<Task>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = TaskPresenter(this)

        setContent {
            PatternTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TaskListScreen(
                        tasks = tasksState,
                        onAddTask = { presenter.addTask(it) },
                        onToggleCompletion = { presenter.toggleTaskCompletion(it) }
                    )
                }
            }
        }
    }

    override fun displayTasks(tasks: List<Task>) {
        tasksState.clear()
        tasksState.addAll(tasks)
    }
}

@Composable
fun TaskListScreen(
    tasks: List<Task>,
    onAddTask: (String) -> Unit,
    onToggleCompletion: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        var newTaskDescription by remember { mutableStateOf("") }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
                    .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                    .padding(4.dp)
            ) {
                BasicTextField(
                    value = newTaskDescription,
                    onValueChange = { newTaskDescription = it },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Button(onClick = {
                onAddTask(newTaskDescription)
                newTaskDescription = ""
            }) {
                Text("Добавить")
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        tasks.forEach { task ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = task.description)
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleCompletion(task.id) }
                )
            }
        }
    }
}