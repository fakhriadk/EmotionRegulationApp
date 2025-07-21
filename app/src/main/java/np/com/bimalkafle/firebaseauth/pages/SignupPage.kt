// file: pages/SignupPage.kt
package np.com.bimalkafle.firebaseauth.pages

import android.app.Activity
import android.util.Patterns
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import np.com.bimalkafle.firebaseauth.AuthState
import np.com.bimalkafle.firebaseauth.AuthViewModel
import np.com.bimalkafle.firebaseauthdemoapp.R

@Composable
fun SignupPage(navController: NavController, authViewModel: AuthViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by authViewModel.authState.observeAsState()
    val context = LocalContext.current

    // Input Validation
    val isEmailValid by remember(email) {
        mutableStateOf(Patterns.EMAIL_ADDRESS.matcher(email).matches())
    }
    val isPasswordValid by remember(password) {
        mutableStateOf(password.length >= 6)
    }
    val isFormValid = isEmailValid && isPasswordValid

    // --- FIX #1: Added the launcher for Google Sign-In, copied from LoginPage ---
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            authViewModel.handleGoogleSignInResult(result.data)
        } else {
            // Handle the case where the user cancels the Google Sign-In flow
            Toast.makeText(context, "Google Sign-In cancelled.", Toast.LENGTH_SHORT).show()
        }
    }

    // This handles navigation AFTER a successful sign-up or sign-in
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> navController.navigate("home") {
                // Clear the back stack so user can't go back to login/signup
                popUpTo(navController.graph.startDestinationId) { inclusive = true }
            }
            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            }
            else -> Unit
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Create an Account",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Email Input
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email address") }, // Using label is better for accessibility
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            isError = email.isNotEmpty() && !isEmailValid
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Password Input
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (min. 6 characters)") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            isError = password.isNotEmpty() && !isPasswordValid
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sign Up Button with Gradient
        Button(
            // --- FIX #2: The onClick logic is now correct ---
            onClick = {
                if (isFormValid) {
                    authViewModel.signup(email, password)
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            contentPadding = PaddingValues(), // Important for the gradient to fill the button
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            enabled = isFormValid // The button is only enabled if the form is valid
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.horizontalGradient(
                            colors = if (isFormValid) listOf(
                                Color(0xFF6DD5FA),
                                Color(0xFFAC32E4)
                            ) else listOf(Color.Gray, Color.Gray)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Sign Up", fontSize = 16.sp, color = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // "Or sign up with" divider
        Text(
            text = "Or sign up with",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )


        Spacer(modifier = Modifier.height(12.dp))

        // Google Sign-In Button
        OutlinedButton(
            // --- FIX #3: Added the correct onClick logic for Google Sign-In ---
            onClick = {
                val signInIntent = authViewModel.getGoogleSignInIntent()
                launcher.launch(signInIntent)
            },
            shape = RoundedCornerShape(25.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Continue with Google",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Login Link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Text("Already have an account? ")
            Text(
                text = "Log in",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { navController.navigate("login") }
            )
        }
    }
}