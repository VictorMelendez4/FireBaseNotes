package com.example.firebasenotes.views.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebasenotes.viewModels.LoginViewModel
import kotlinx.coroutines.delay

@Composable
fun SplashView(navController: NavController, loginVM: LoginViewModel) {

    LaunchedEffect(Unit) {
        delay(2000)


        if (loginVM.getCurrentUser() != null) {
            // Si existe, vamos directo al HOME
            navController.navigate("Home") {

                popUpTo("Splash") { inclusive = true }
            }
        } else {

            navController.navigate("Login") {
                popUpTo("Splash") { inclusive = true }
            }
        }
    }


    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Icono Grande
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Logo",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(120.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            // Nombre de la App
            Text(
                text = "Firebase Notes",
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}