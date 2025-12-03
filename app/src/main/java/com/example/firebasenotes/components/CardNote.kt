package com.example.firebasenotes.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.firebasenotes.viewModels.NotesViewModel

@Composable
fun CardNote(
    title: String,
    note: String,
    date: String,
    colorIndex: Int, // Recibimos el índice del color
    onClick: () -> Unit
) {
    // Obtenemos el color real de la paleta (puedes instanciar VM temporalmente o pasar el color directo)
    // Para simplificar, hardcodeamos la paleta aquí igual que en el VM para visualización rápida
    val colorPalette = listOf(
        Color(0xFFFFFFFF), Color(0xFFFDE68A), Color(0xFFBFDBFE),
        Color(0xFFBBF7D0), Color(0xFFFBCFE8), Color(0xFFDDD6FE)
    )
    val cardColor = if (colorIndex in colorPalette.indices) colorPalette[colorIndex] else Color.White

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(
            containerColor = cardColor // Usamos el color dinámico
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF1E293B) // Texto oscuro siempre legible en pasteles
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = date,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = note,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF334155)
            )
        }
    }
}