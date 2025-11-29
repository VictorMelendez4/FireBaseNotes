package com.example.firebasenotes.views.login

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.firebasenotes.components.Alert
import com.example.firebasenotes.viewModels.LoginViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(navController: NavController, loginVM: LoginViewModel) {
    val context = LocalContext.current

    // Estados del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Estado para controlar si mostramos la ventana de recuperación
    var showResetDialog by remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        // --- TÍTULO ---
        // UX: Un título ayuda a saber dónde estamos
        Spacer(modifier = Modifier.height(50.dp))
        Text(
            text = "Iniciar Sesión",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(30.dp))

        // --- INPUTS ---
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp),
            shape = RoundedCornerShape(10.dp) // Diseño más suave
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

        // --- RECUPERAR CONTRASEÑA (Mejorado) ---
        // UX: Alineado a la derecha, sutil pero accesible
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "¿Olvidaste tu contraseña?",
            modifier = Modifier
                .padding(end = 30.dp)
                .align(Alignment.End)
                .clickable { showResetDialog = true }, // Al hacer clic, abrimos el Dialog
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
            textDecoration = TextDecoration.Underline
        )

        Spacer(modifier = Modifier.height(20.dp))

        // --- BOTÓN LOGIN ---
        Button(
            onClick = {
                loginVM.login(email, password) {
                    navController.navigate("Home")
                }
            }, modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 30.dp)
                .height(50.dp), // Botón más alto para dedos grandes
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Entrar", fontSize = 16.sp)
        }

        // --- ALERTAS DE ERROR ---
        if (loginVM.showAlert) {
            Alert(title = "Error",
                message = "Usuario y/o Contraseña Incorrectos",
                confirmText = "Aceptar",
                onConfirmClick = { loginVM.closeAlert() }) {
            }
        }

        // --- DIALOG DE RECUPERACIÓN (NUEVO) ---
        if (showResetDialog) {
            ForgotPasswordDialog(
                onDismiss = { showResetDialog = false },
                onSend = { emailRecuperacion ->
                    loginVM.resetPassword(emailRecuperacion) {
                        Toast.makeText(context, "Correo enviado. Revisa SPAM si no llega.", Toast.LENGTH_LONG).show()
                        showResetDialog = false
                    }
                },
                // UX PRO: Si el usuario ya escribió su correo en el login, se lo ponemos automático
                initialEmail = email
            )
        }
    }
}

// COMPONENTE EXTRA: Ventana flotante especializada
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordDialog(
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
    initialEmail: String
) {
    var resetEmail by remember { mutableStateOf(initialEmail) }
    var error by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Recuperar Contraseña") },
        text = {
            Column {
                Text("Ingresa tu correo para enviarte las instrucciones:")
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedTextField(
                    value = resetEmail,
                    onValueChange = {
                        resetEmail = it
                        error = false // Quitamos error si escribe
                    },
                    label = { Text("Email") },
                    isError = error,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    singleLine = true
                )
                if (error) {
                    Text("El correo es obligatorio", color = Color.Red, fontSize = 12.sp)
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                if (resetEmail.isNotBlank()) {
                    onSend(resetEmail)
                } else {
                    error = true
                }
            }) {
                Text("Enviar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}