
package com.example.duocardsapplication2.core.utiluties.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.duocardsapplication2.core.utiluties.theme.Typography

data class AppColors(
    val blue700: Color,
    val accent: Color,
    val textPrimary: Color,
)
private val LightAppColorPalette = AppColors(
    blue700 = Blue700,
    accent = Blue700,
    textPrimary = Color.Black,
)

// Bu da koyu tema için.
private val DarkAppColorPalette = AppColors(
    blue700 = Blue700,
    accent = Blue700,
    textPrimary = Color.White,
)
private val LocalAppColors = staticCompositionLocalOf { LightAppColorPalette }

// --- 2. ÖZEL RENK SINIFINI OLUŞTUR ---
// İstediğin tüm özel renkleri buraya ekle.

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40,



    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

@Composable
fun Duocardsapplication2Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val appColors = if (darkTheme) DarkAppColorPalette else LightAppColorPalette
    val colorScheme = when {
        //bura kaldırılabilir opsiyonel olarak
        //dynamicColor ekler
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    //AppTheme de LocalAppColors.current ile appColorsa erişiriz.
    CompositionLocalProvider(LocalAppColors provides appColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
// --- 4. KOLAY ERİŞİM İÇİN BİR OBJECT OLUŞTUR (ŞİDDETLE TAVSİYE EDİLİR) ---
// Bu sayede her seferinde LocalA   ppColors.current yazmak zorunda kalmazsın.
object AppTheme {
    val appColors: AppColors
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    // Standart Material renklerine de buradan erişebilirsin
    val materialColorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme
}