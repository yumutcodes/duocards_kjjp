package com.example.duocardsapplication2.features.auth.commoncomposables

import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AuthConfirmButton(
    onClick:() ->Unit,
    buttonText:String,
    enabled:Boolean ,


){
    if(enabled){
        Button(
            onClick = onClick,


        ) {
            Text(buttonText)
        }
    }
    else{
        CircularProgressIndicator()
    }
}
@Preview
@Composable
fun AuthButtonPreview(){
    AuthConfirmButton(
        onClick = {},
        buttonText = "Register",
        enabled = true
    )

}
@Preview
@Composable
fun AuthButtonPreviewNotEnabled(){
    AuthConfirmButton(
        onClick = {},
        buttonText = "Register",
        enabled = false
    )

}