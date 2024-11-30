package com.example.doodler

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.doodler.ui.theme.DoodlerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DoodlerTheme {
                DoodleApp()
            }
        }
    }
}

@Composable
fun DoodleApp() {
    var brushSize by remember { mutableStateOf(10f) }
    var brushColor by remember { mutableStateOf(Color.Black) }
    var brushOpacity by remember { mutableStateOf(1f) } // Default to fully opaque
    val doodleViewReference = remember { mutableStateOf<DoodleView?>(null) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            ToolPanel(
                onClear = { doodleViewReference.value?.clear() },
                onUndo = { doodleViewReference.value?.undo() },
                onRedo = { doodleViewReference.value?.redo() },
                onBrushSizeChange = { size ->
                    brushSize = size
                    doodleViewReference.value?.setBrushSize(size)
                },
                onColorChange = { color ->
                    brushColor = color
                    doodleViewReference.value?.setBrushColor(color.toArgb())
                },
                onOpacityChange = { opacity ->
                    brushOpacity = opacity
                    doodleViewReference.value?.setOpacity(opacity)
                },
                currentBrushSize = brushSize,
                currentOpacity = brushOpacity
            )
        }
    ) { padding ->
        AndroidView(
            factory = { context ->
                DoodleView(context).apply {
                    setBrushSize(brushSize)
                    setBrushColor(brushColor.toArgb())
                    setOpacity(brushOpacity)
                    doodleViewReference.value = this
                }
            },
            update = { view ->
                view.setBrushSize(brushSize)
                view.setBrushColor(brushColor.toArgb())
                view.setOpacity(brushOpacity)
            },
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        )
    }
}

@Composable
fun ToolPanel(
    onClear: () -> Unit,
    onUndo: () -> Unit,
    onRedo: () -> Unit,
    onBrushSizeChange: (Float) -> Unit,
    onOpacityChange: (Float) -> Unit,
    onColorChange: (Color) -> Unit,
    currentBrushSize: Float,
    currentOpacity: Float
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(8.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onUndo) {
                Text("Undo")
            }
            Button(onClick = onRedo) {
                Text("Redo")
            }
            Button(onClick = onClear) {
                Text("Clear Sketch")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Brush Size Slider
        Text("Brush Size: ${currentBrushSize.toInt()}")
        Slider(
            value = currentBrushSize,
            onValueChange = { size -> onBrushSizeChange(size) },
            valueRange = 5f..50f
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Opacity Slider
        Text("Brush Opacity: ${(currentOpacity * 100).toInt()}%")
        Slider(
            value = currentOpacity,
            onValueChange = { opacity -> onOpacityChange(opacity) },
            valueRange = 0f..1f // 0 = Transparent, 1 = Opaque
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Color Picker
        Text("Brush Color")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Color.Red, Color.Green, Color.Blue, Color.Black).forEach { color ->
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(color, shape = CircleShape)
                        .clickable { onColorChange(color) }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DoodleAppPreview() {
    DoodlerTheme {
        DoodleApp()
    }
}
