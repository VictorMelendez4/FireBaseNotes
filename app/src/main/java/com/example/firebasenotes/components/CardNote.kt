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

@Composable
fun CardNote(
    title: String,
    note: String,
    date: String,
    colorIndex: Int,
    onClick: () -> Unit
) {
    val colorPalette = listOf(
        Color(0xFFFFFFFF),
        Color(0xFFFEF3C7),
        Color(0xFFE0F2FE),
        Color(0xFFDCFCE7),
        Color(0xFFFAE8FF),
        Color(0xFFEDE9FE)
    )

    // Si el color es 0 es Blanco.
    val cardColor = if (colorIndex in colorPalette.indices) colorPalette[colorIndex] else Color.White

    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (colorIndex == 0) 2.dp else 0.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp)
        ) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp,
                color = Color(0xFF1F2937)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = date,
                fontSize = 11.sp,
                color = Color(0xFF9CA3AF)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = note,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                color = Color(0xFF4B5563)
            )
        }
    }
}