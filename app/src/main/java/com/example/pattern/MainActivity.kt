package com.example.pattern

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.ui.Alignment
import com.example.pattern.ui.theme.PatternTheme
import kotlinx.coroutines.launch

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
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var newTaskDescription by remember { mutableStateOf("") }
    var showHint by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
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
                if (newTaskDescription.isBlank()) {
                    // Пустая задача
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Задача не может быть пустой!"
                        )
                    }
                } else {
                    // Успешное добавление
                    onAddTask(newTaskDescription)
                    newTaskDescription = ""
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = "Задача успешно добавлена!"
                        )
                    }
                }
            }) {
                Text("Добавить")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        HintButton {
            showHint = true
        }

        if (showHint) {
            AlertDialog(
                onDismissRequest = { showHint = false },
                title = { Text("Подсказка") },
                text = { Text("Введите текст задачи в поле и нажмите 'Добавить', чтобы добавить её в список.") },
                confirmButton = {
                    TextButton(onClick = { showHint = false }) {
                        Text("Закрыть")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        tasks.forEach { task ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = task.description)
                Checkbox(
                    checked = task.isCompleted,
                    onCheckedChange = { onToggleCompletion(task.id) }
                )
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            SnackbarHost(hostState = snackbarHostState)
        }
    }
}

@Composable
fun HintButton(showHint: () -> Unit) {
    val interactionSource = remember { MutableInteractionSource() }
    var isPressed by remember { mutableStateOf(false) }

    LaunchedEffect(interactionSource) {
        interactionSource.interactions.collect { interaction ->
            when (interaction) {
                is PressInteraction.Press -> isPressed = true
                is PressInteraction.Release, is PressInteraction.Cancel -> isPressed = false
            }
        }
    }


    val buttonColor = if (isPressed) Color.White else MaterialTheme.colorScheme.primary


    Button(
        onClick = { showHint() },
        colors = ButtonDefaults.buttonColors(containerColor = buttonColor),
        interactionSource = interactionSource,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text("Подсказка")
    }
}
