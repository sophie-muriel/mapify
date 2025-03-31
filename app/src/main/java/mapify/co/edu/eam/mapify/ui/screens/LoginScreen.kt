package mapify.co.edu.eam.mapify.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import mapify.co.edu.eam.mapify.ui.components.GenericTextField
import mapify.co.edu.eam.mapify.ui.components.LogoTitle
import mapify.co.edu.eam.mapify.ui.theme.MapifyTheme
import mapify.co.edu.eam.mapify.ui.theme.Spacing

@Composable
fun LoginScreen(navController: NavController) {

    var email by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    MapifyTheme {
        Scaffold { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))

                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // logo + name
                    LogoTitle(2f)

                    Spacer(modifier = Modifier.weight(1f))

                    // text form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GenericTextField(
                            value = email,
                            supportingText = "Please type a valid email",
                            label = "Email",
                            onValueChange = {
                                email = it
                                emailError =
                                    !(email == "root" || Patterns.EMAIL_ADDRESS.matcher(email)
                                        .matches())
                            },
                            onValidate = {
                                emailError
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Email,
                                    contentDescription = "Email icon",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

                        GenericTextField(
                            value = password,
                            supportingText = "Password has to be 6 characters long at least",
                            label = "Password",
                            onValueChange = {
                                password = it
                                passwordError = !(password == "root" || password.length >= 6)
                            },
                            onValidate = {
                                passwordError
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Lock,
                                    contentDescription = "Password icon",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            isPassword = true
                        )

                        Text(
                            modifier = Modifier
                                .padding(end = 36.dp)
                                .align(Alignment.End),
                            text = "Forgot your password?",
                            style = MaterialTheme.typography.labelSmall,
                        )

                        Spacer(modifier = Modifier.padding(Spacing.Inline))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 24.dp, end = 24.dp
                                )
                                .height(40.dp),
                            enabled = email.isNotEmpty() && password.isNotEmpty() && !emailError && !passwordError,
                            onClick = {
                                if (email == "root" && password == "root") {
                                    Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(
                                        context, "Incorrect credentials", Toast.LENGTH_SHORT
                                    ).show()
                                }
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            ),
                        ) {
                            Text(
                                text = "Login",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        Spacer(modifier = Modifier.padding(Spacing.Inline))

                        TextButton(
                            onClick = { navController.navigate("registration") }
                        ) {
                            Text(
                                text = "Don't have an account? Register",
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))
            }
        }
    }
}