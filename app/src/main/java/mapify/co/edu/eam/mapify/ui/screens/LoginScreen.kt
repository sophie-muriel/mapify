package mapify.co.edu.eam.mapify.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import co.edu.eam.mapify.R
import mapify.co.edu.eam.mapify.ui.components.GenericTextField
import mapify.co.edu.eam.mapify.ui.theme.MapifyTheme

@Composable
fun LoginScreen(){

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
                    .padding(padding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                Spacer(Modifier.height(16.dp))

                Image(
                    painter = painterResource(id = R.drawable.mapify_logo),
                    contentDescription = "Mapify Logo",
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(MaterialTheme.colorScheme.primary),
                    modifier = Modifier
                        .fillMaxWidth(0.3f)
                        .aspectRatio(1f)
                )

                Text(
                    text = "Mapify",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 25.dp, bottom = 80.dp)
                )

                GenericTextField(
                    value = email,
                    supportingText = "Please type a valid email",
                    label = "Email",
                    onValueChange = {
                        email = it
                    },
                    onValidate = {
                        if (email == "root") false
                        else !Patterns.EMAIL_ADDRESS.matcher(email).matches()
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
                    },
                    onValidate = {
                        if (password == "root") false
                        else !Patterns.EMAIL_ADDRESS.matcher(email).matches()
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

                //Spacer(modifier = Modifier.padding(8.dp))

                //Spacer(modifier = Modifier.padding(10.dp))

                Text(
                    modifier = Modifier
                        .padding(end = 36.dp)
                        .align(Alignment.End),
                    text = "Forgot your password?",
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.padding(4.dp))

                Button(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            end = 24.dp
                        ),
                    enabled = email.isNotEmpty() && password.isNotEmpty(), // pendiente && !emailError && !passwordError,
                    onClick = {
                        if (email == "root" && password == "root") {
                            Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Incorrect credentials", Toast.LENGTH_SHORT)
                                .show()
                        }
                    },
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                ) {
                    Text("Login")
                }

                Spacer(modifier = Modifier.padding(10.dp))

                Text(
                    text = "Don't have an account? Register"
                )

            }
        }
    }
}