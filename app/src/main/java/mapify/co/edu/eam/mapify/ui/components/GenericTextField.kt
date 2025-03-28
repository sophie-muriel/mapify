package mapify.co.edu.eam.mapify.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun GenericTextField(
    modifier: Modifier = Modifier,
    value: String,
    supportingText: String,
    label: String,
    onValueChange: (String) -> Unit,
    onValidate: (String) -> Boolean,
    leadingIcon: @Composable (() -> Unit)? = null,
    isSingleLine: Boolean = true,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    isPassword: Boolean = false
){
    var isError by rememberSaveable { mutableStateOf(false) }

    OutlinedTextField(
        modifier = modifier.then(
            Modifier
            .fillMaxWidth()
            .padding(
                start = 24.dp,
                end = 24.dp
            )
        ),
        leadingIcon = leadingIcon,
        singleLine = isSingleLine,
        isError = isError,
        supportingText = {
            if(isError){
                Text(text = supportingText)
            }
        },
        keyboardOptions = keyboardOptions,
        visualTransformation = if(isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        label = {
            Text(text = label)
        },
        value = value,
        onValueChange = {
            onValueChange(it)
            isError = onValidate(it)
        },
    )
}