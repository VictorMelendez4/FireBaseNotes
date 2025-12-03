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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.firebasenotes.viewModels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddNoteView(navController: NavController, notesVM: NotesViewModel) {
    var title by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        notesVM.resetState()
    }

    // Obtenemos el color actual seleccionado del VM para pintar el fondo de la pantalla
    val currentColor = notesVM.getColor(notesVM.selectedColorIndex)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Nueva Nota", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    if (notesVM.isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(end = 10.dp))
                    } else {
                        IconButton(onClick = {
                            if (title.isNotBlank()) {
                                notesVM.saveNewNote(title, note){
                                    Toast.makeText(context, "Nota guardada", Toast.LENGTH_SHORT).show()
                                    navController.popBackStack()
                                }
                            } else {
                                Toast.makeText(context, "Ponle un título", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Guardar", tint = MaterialTheme.colorScheme.primary)
                        }
                    }
                },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = currentColor) // El appbar toma el color
            )
        },
        containerColor = currentColor // El fondo toma el color seleccionado
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // SELECTOR DE COLORES
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(10.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                itemsIndexed(notesVM.colorPalette) { index, color ->
                    val isSelected = index == notesVM.selectedColorIndex
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) Color.Black else Color.LightGray,
                                shape = CircleShape
                            )
                            .clickable { notesVM.onColorChange(index) }
                    )
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(text = "Título") },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                singleLine = true,
                enabled = !notesVM.isLoading
            )

            Spacer(modifier = Modifier.height(10.dp))

            TextField(
                value = note,
                onValueChange = { note = it },
                placeholder = { Text("Escribe tu nota aquí...") },
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(horizontal = 20.dp),
                colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
                enabled = !notesVM.isLoading
            )
        }
    }
}