package com.example.duocardsapplication2.core.utiluties.ui

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import kotlinx.serialization.Serializable

/*
Ne işe yarar ?
res/values/strings den multilanguage appler için kullanılacak.
örnek kullanım :
State ler içinde string yerine uitext kullan .
https://aistudio.google.com/prompts/1VL9Pg5MqdhMxP6Cndain6Tf_6oVI1sEm
 */
@Serializable
sealed class UiText {
    @Serializable
    data class DynamicString(val value: String) : UiText()
    @Serializable
    data class StringResource(
        @param:StringRes val resId: Int,
        val args: List<String> = emptyList()
    ) : UiText() {
        constructor(@StringRes resId: Int, vararg args: String) : this(resId, args.toList())
    }
    @Composable
    fun asString(): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> stringResource(resId, *args.toTypedArray())
        }
    }
    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args.toTypedArray())
        }
    }
}