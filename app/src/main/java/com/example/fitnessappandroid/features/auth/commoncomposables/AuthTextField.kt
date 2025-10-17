package com.example.fitnessappandroid.features.auth.commoncomposables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AuthTextField(
    value: String,
    onInputChanged: (String) -> Unit,
    isValid: Boolean,
    labelText: String,
    ErrorText: String
){
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp)
    ){
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = value,
        onValueChange = onInputChanged, // Kullanıcı yazınca bu event tetiklenir
        label =  {Text(labelText)} ,
        isError = !isValid, // Hata durumu state'ten okunur
        singleLine = true,

        )

    // Eğer giriş geçerli değilse, hata mesajını göster
    if (!isValid) {
        Text(
            text = ErrorText,
            color = Color.Red,
            modifier = Modifier
                .padding(top = 4.dp)
                .align(Alignment.Start) // Metni sola hizala
        )
    }
    }
}