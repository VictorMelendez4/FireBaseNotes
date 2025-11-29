package com.example.firebasenotes.views.login

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebasenotes.components.Alert
import com.example.firebasenotes.viewModels.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterView(navController: NavController, loginVM: LoginViewModel) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        // Variables de estado
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }

        // --- TÍTULO ---

        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Crear Cuenta",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(30.dp))

        // --- INPUTS ---
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(text = "Nombre de usuario") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(text = "Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            shape = RoundedCornerShape(10.dp)
        )

        Spacer(modifier = Modifier.height(30.dp))

        // --- BOTÓN REGISTRAR ---
        Button(
            onClick = {

                if (username.isBlank() || email.isBlank() || password.isBlank()) {

                }
                loginVM.createUser(email, password, username) {
                    navController.navigate("Home")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .height(50.dp), // Botón alto para mejor tacto
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Registrarse", fontSize = 16.sp)
        }

        //  BOTÓN VOLVER
        Spacer(modifier = Modifier.height(10.dp))
        TextButton(onClick = { navController.popBackStack() }) {
            Text(text = "¿Ya tienes cuenta? Inicia sesión", color = MaterialTheme.colorScheme.secondary)
        }

        // --- MANEJO DE ALERTAS ---
        if (loginVM.showAlert) {
            Alert(title = "Error de Registro",
                message = "No se pudo crear el usuario. Verifica que el correo no exista ya y la contraseña tenga 6+ caracteres.",
                confirmText = "Entendido",
                onConfirmClick = { loginVM.closeAlert() }) {
            }
        }
    }
}