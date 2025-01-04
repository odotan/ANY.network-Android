package com.anynetwork.app.ui.utils

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

val Int.xdph: Dp get() = this.toFloat().xdph
val Int.xdpv: Dp get() = this.toFloat().xdpv
val Double.xdph: Dp get() = this.toFloat().xdph
val Double.xdpv: Dp get() = this.toFloat().xdpv
val Float.xdph: Dp get() = if (this == 0f) Dp(0f) else Dp(this.toXdWidth()/AppConfig.density)
val Float.xdpv: Dp get() = if (this == 0f) Dp(0f) else Dp(this.toXdHeight()/AppConfig.density)

val xdWidth: Float = 375f
val xdHeight: Float = 812f

fun Float.toXdWidth() : Float {
    if (AppConfig.widthPixels == 1)  {
        return this
    }
    val dp: Float = AppConfig.density
    val screen: Int = AppConfig.widthPixels
    val current: Float = this
    val calc: Float = (screen * ((current / xdWidth)))
    return calc
}
fun Float.toXdHeight() : Float {
    if (AppConfig.heightPixels == 1)  {
        return this
    }
    val dp: Float = AppConfig.density
    val screen: Int = AppConfig.heightPixels
    val current: Float = this
    val calc: Float = (screen * ((current / xdHeight)))
    return calc
}

fun Float.toSize() : Float {
    if (AppConfig.heightPixels == 1)  {
        return this
    }
    val screen: Int = AppConfig.heightPixels
    val current: Float = this
    val calc: Float = ((screen * (current / xdHeight)) / AppConfig.fontDensity)
    return calc
}

val Int.csp: TextUnit get() = this.toFloat().csp
val Float.csp: TextUnit get() = when {
    this == 0f -> TextUnit(0f, TextUnitType.Sp)
    else -> TextUnit(this.toSize(), TextUnitType.Sp)
}

val fWidth: Float = 393f
val fHeight: Float = 852f

val Int.fdph: Dp get() = this.toFloat().fdph
val Int.fdpv: Dp get() = this.toFloat().fdpv
val Double.fdph: Dp get() = this.toFloat().fdph
val Double.fdpv: Dp get() = this.toFloat().fdpv
val Float.fdph: Dp get() = if (this == 0f) Dp(0f) else Dp(this.toFigmaWidth()/AppConfig.density)
val Float.fdpv: Dp get() = if (this == 0f) Dp(0f) else Dp(this.toFigmaHeight()/AppConfig.density)

fun Float.toFigmaWidth() : Float {
    if (AppConfig.widthPixels == 1)  {
        return this
    }
    val dp: Float = AppConfig.density
    val screen: Int = AppConfig.widthPixels
    val current: Float = this
    val calc: Float = (screen * ((current / fWidth)))
    return calc
}
fun Float.toFigmaHeight() : Float {
    if (AppConfig.heightPixels == 1)  {
        return this
    }
    val dp: Float = AppConfig.density
    val screen: Int = AppConfig.heightPixels
    val current: Float = this
    val calc: Float = (screen * ((current / fHeight)))
    return calc
}

fun Float.toFigmaSize() : Float {
    if (AppConfig.heightPixels == 1)  {
        return this
    }
    val screen: Int = AppConfig.heightPixels
    val current: Float = this
    val calc: Float = ((screen * (current / xdHeight)) / AppConfig.fontDensity)
    return calc
}

val Int.fsp: TextUnit get() = this.toFloat().csp
val Float.fsp: TextUnit get() = when {
    this == 0f -> TextUnit(0f, TextUnitType.Sp)
    else -> TextUnit(this.toFigmaSize(), TextUnitType.Sp)
}