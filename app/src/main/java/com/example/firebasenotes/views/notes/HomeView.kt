package com.example.firebasenotes.views.notes

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebasenotes.components.CardNote
import com.example.firebasenotes.viewModels.NotesViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeView(navController: NavController, notesVM: NotesViewModel) {

    LaunchedEffect(Unit) {
        notesVM.fetchNotes()
    }

    var showExitDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = "Mis Notas", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent
                ),
                actions = {
                    // BOTÓN RECARGAR
                    IconButton(onClick = {
                        notesVM.fetchNotes()
                        Toast.makeText(context, "Sincronizando...", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Recargar", tint = Color(0xFF1E293B))
                    }

                    IconButton(onClick = { showExitDialog = true }) {
                        Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Salir", tint = Color(0xFF1E293B))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("AddNoteView") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Agregar Nota")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            OutlinedTextField(
                value = notesVM.searchQuery,
                onValueChange = { notesVM.onSearchChange(it) },
                label = { Text("Buscar nota...", color = Color.Gray) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                shape = RoundedCornerShape(30.dp),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = "", tint = Color.Gray)
                },
                trailingIcon = {
                    if (notesVM.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { notesVM.onSearchChange("") }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "", tint = Color.Gray)
                        }
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    textColor = Color.Black,
                    cursorColor = MaterialTheme.colorScheme.primary
                )
            )

            // LOGICA DE CARGA VISUAL
            if (notesVM.isLoading) {
                // si esta cargando, mostramos el círculo en el centro
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            } else {
                // si no esta cargando, mostramos la lista o el vacío
                val datos by notesVM.notesData.collectAsState()
                val filteredNotes = notesVM.getFilteredNotes()

                if (filteredNotes.isEmpty()) {
                    EmptyState(isSearching = notesVM.searchQuery.isNotEmpty())
                } else {
                    LazyVerticalStaggeredGrid(
                        columns = StaggeredGridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(filteredNotes) { item ->
                            CardNote(
                                title = item.title,
                                note = item.note,
                                date = item.date,
                                colorIndex = item.colorIndex,
                                onClick = {
                                    navController.navigate("EditNoteView/${item.idDoc}")
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showExitDialog) {
            AlertDialog(
                onDismissRequest = { showExitDialog = false },
                title = { Text("Cerrar Sesión") },
                text = { Text("¿Estás seguro de que quieres salir?") },
                confirmButton = {
                    Button(onClick = {
                        showExitDialog = false
                        notesVM.signOut()
                        navController.popBackStack()
                    }) {
                        Text("Sí, salir")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExitDialog = false }) {
                        Text("Cancelar")
                    }
                }
            )
        }
    }
}

@Composable
fun EmptyState(isSearching: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 100.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isSearching) Icons.Default.Search else Icons.Default.Refresh,
            contentDescription = "",
            modifier = Modifier
                .padding(bottom = 16.dp)
                .fillMaxSize(0.2f),
            tint = Color(0xFFCBD5E1)
        )
        Text(
            text = if (isSearching) "No se encontraron notas" else "Cargando notas...",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF94A3B8),
            fontSize = 18.sp
        )
        if (!isSearching){
            Text(
                text = "Si no aparecen, pulsa el botón de arriba 🔄",
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                fontSize = 14.sp
            )
        }
    }
}