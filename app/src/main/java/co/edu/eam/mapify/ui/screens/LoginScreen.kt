package co.edu.eam.mapify.ui.screens

import android.util.Patterns
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.sp
import co.edu.eam.mapify.ui.components.GenericTextField

@Composable
fun LoginScreen(){

    var email by rememberSaveable { mutableStateOf("") }
    var emailError by rememberSaveable { mutableStateOf(false) }

    var password by rememberSaveable { mutableStateOf("") }
    var passwordError by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            GenericTextField(
                value = email,
                supportingText = "Please type a valid email",
                label = "Email",
                onValueChange = {
                    email = it
                },
                onValidate = {
                    !Patterns.EMAIL_ADDRESS.matcher(email).matches()
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Email,
                        contentDescription = "Email icon"
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
                    password.length < 6
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Lock,
                        contentDescription = "Password icon"
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
                    if(email == "sebas@gmail.com" && password == "123"){
                        Toast.makeText(context, "Welcome!", Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(context, "Incorrect credentials", Toast.LENGTH_SHORT).show()
                    }
                }
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