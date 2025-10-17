package com.example.fitnessappandroid.features.auth.commoncomposables

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

@Composable
fun NavButton(
    infoText: String,
    onClick: () -> Unit,
    textButtonText: String
){
    Column(
horizontalAlignment = Alignment.Start
    ) {
        Text(infoText)
        TextButton(
            onClick = onClick

        ) {
            Text(text = textButtonText)
        }
    }
}