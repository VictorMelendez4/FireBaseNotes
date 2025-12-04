package com.example.firebasenotes.views.notes

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebasenotes.viewModels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditNoteView(navController: NavController, notesVM: NotesViewModel, idDoc: String){

    LaunchedEffect(Unit){
        notesVM.getNoteById(idDoc)
    }

    val state = notesVM.state
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    val currentColor = notesVM.getColor(notesVM.selectedColorIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.Black)
                    }
                },
                actions = {
                    if (!notesVM.isLoading) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Borrar", tint = Color.Red.copy(alpha = 0.7f))
                        }
                    }

                    if (notesVM.isLoading) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.padding(end = 10.dp).size(24.dp))
                    } else {
                        IconButton(onClick = {
                            notesVM.updateNote(idDoc) {
                                Toast.makeText(context, "Nota actualizada", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Guardar", tint = Color.Black)
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = currentColor)
            )
        },
        containerColor = currentColor
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
        ) {

            // selector de colores
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(12.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                itemsIndexed(notesVM.colorPalette) { index, color ->
                    val isSelected = index == notesVM.selectedColorIndex
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .size(if (isSelected) 32.dp else 24.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(1.dp, Color.Black.copy(alpha = 0.2f), CircleShape)
                            .clickable { notesVM.onColorChange(index) }
                    )
                }
            }

            // titulo grande
            TextField(
                value = state.title,
                onValueChange = { notesVM.onValue(it,"title") },
                placeholder = { Text("Título", fontSize = 28.sp, fontWeight = FontWeight.Bold) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                textStyle = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.Black),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                enabled = !notesVM.isLoading
            )

            // FECHA SUTIL (Mejora de UX)
            Text(
                text = "Editado: ${state.date}",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 28.dp, vertical = 4.dp)
            )

            // NOTA CUERPO
            TextField(
                value = state.note,
                onValueChange = { notesVM.onValue(it, "note") },
                placeholder = { Text("Escribe aquí...", fontSize = 18.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                textStyle = TextStyle(fontSize = 18.sp, lineHeight = 28.sp, color = Color.Black.copy(alpha = 0.8f)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    cursorColor = Color.Black
                ),
                enabled = !notesVM.isLoading
            )
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("¿Borrar nota?") },
                text = { Text("No podrás recuperarla después.") },
                containerColor = Color.White,
                titleContentColor = Color.Black,
                textContentColor = Color.Gray,
                confirmButton = {
                    Button(
                        onClick = {
                            notesVM.deleteNote(idDoc) {
                                Toast.makeText(context, "Nota eliminada", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                            showDeleteDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF4D4D))
                    ) {
                        Text("Eliminar", color = Color.White)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text("Cancelar", color = Color.Black)
                    }
                }
            )
        }
    }
}