package mapify.ui.screens

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
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import mapify.ui.components.GenericTextField
import mapify.ui.components.LogoTitle
import mapify.ui.theme.MapifyTheme
import mapify.ui.theme.Spacing

@Composable
fun RegistrationScreen() {

    var name by rememberSaveable { mutableStateOf("") }
    var nameError by rememberSaveable { mutableStateOf(false) }

    var email by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    var passwordConfirmation by rememberSaveable { mutableStateOf("") }
    var passwordConfirmationError by rememberSaveable { mutableStateOf(false) }

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
                    LogoTitle(3.5f)

                    Spacer(modifier = Modifier.weight(1f))

                    // text form
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            modifier = Modifier
                                .padding(start = 24.dp)
                                .align(Alignment.Start),
                            text = "Please enter your information",
                            style = MaterialTheme.typography.labelSmall
                        )

                        Spacer(modifier = Modifier.padding(Spacing.Inline))

                        GenericTextField(
                            value = name,
                            supportingText = "Please enter your name",
                            label = "Name",
                            onValueChange = {
                                name = it
                                nameError = name.isBlank()
                            },
                            onValidate = {
                                nameError
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Person,
                                    contentDescription = "User icon",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                        )

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

                        GenericTextField(
                            value = passwordConfirmation,
                            supportingText = "Passwords do not match",
                            label = "Confirm password",
                            onValueChange = {
                                passwordConfirmation = it
                                passwordConfirmationError = password != it
                            },
                            onValidate = {
                                passwordConfirmationError
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

                        Spacer(modifier = Modifier.padding(Spacing.Inline))

                        Button(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 24.dp,
                                    end = 24.dp
                                )
                                .height(40.dp),
                            enabled = name.isNotEmpty() &&
                                    email.isNotEmpty() &&
                                    password.isNotEmpty() &&
                                    passwordConfirmation.isNotEmpty() &&
                                    !emailError &&
                                    !passwordError &&
                                    !passwordConfirmationError,
                            onClick = {
                                if (email != "root" && password != "root") {
                                    Toast.makeText(context, "Confirm location", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(
                                        context,
                                        "The email is already taken",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
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

                        Text(
                            text = "Already have an account? Login",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Spacing.TopBottomScreen))
            }
        }
    }
}