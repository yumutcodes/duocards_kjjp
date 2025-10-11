package com.example.duocardsapplication2.features.auth.commoncomposables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.tooling.preview.Preview
import com.example.duocardsapplication2.core.utiluties.ui.UiState
import com.example.duocardsapplication2.features.auth.data.dto.LoginResponse
import com.example.duocardsapplication2.features.auth.login.presentation.LoginUiState

/*
when(uiState){
        UiState.Loading -> CircularProgressIndicator()
        UiState.Idle -> Button(
            onClick = onClick,
        ) {
            Text(buttonText)
        }
        else -> {}
    }
    kullanılabilir belki
 */

@Composable
fun <T> AuthConfirmButton(
    onClick:() ->Unit,
    buttonText:String,
    uiState: UiState<T>
){
    if(UiState.Idle == uiState){
        Button(
            onClick = onClick
        ) {
            Text(buttonText)
        }
    }
    if(UiState.Loading == uiState){
        CircularProgressIndicator()
    }
    if(uiState is UiState.Error){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = onClick
            ) {
                Text(buttonText)
            }
            Text(uiState.message.asString())
        }
    }
}
@Preview
@Composable
fun AuthButtonPreview(){
    AuthConfirmButton(
        onClick = {},
        buttonText = "Register",
        uiState = UiState.Idle
    )

}
@Preview
@Composable
fun AuthButtonPreviewNotEnabled(){
    AuthConfirmButton(
        onClick = {},
        buttonText = "Register",
        uiState = UiState.Loading
    )

}