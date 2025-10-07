package com.example.duocardsapplication2.features.auth.commoncomposables

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.duocardsapplication2.core.utiluties.ui.UiState

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
fun AuthConfirmButton(
    onClick:() ->Unit,
    buttonText:String,
    uiState: UiState<Nothing>,


    ){

    if(UiState.Idle == uiState){
        Button(
            onClick = onClick,


        ) {
            Text(buttonText)
        }
    }
    if(UiState.Loading == uiState){
        CircularProgressIndicator()
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