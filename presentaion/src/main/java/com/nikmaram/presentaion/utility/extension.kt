package com.nikmaram.presentaion.utility

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.os.Parcelable
import androidx.navigation.findNavController
import com.google.android.material.appbar.MaterialToolbar
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
private const val PATTERN_1 = "m:ss"
private const val PATTERN_2 = "mm:ss"
private const val PATTERN_3 = "H:mm:ss"
private const val PATTERN_4 = "HH:mm:ss"
private const val TEN_MINUTES: Long = 600000
private const val ONE_HOUR: Long = 3600000
private const val TEN_HOURS: Long = 36000000
inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelable(key: String): T? = when {
    SDK_INT >= 33 -> getParcelable(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelable(key) as? T
}

inline fun <reified T : Parcelable> Bundle.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayList(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayList(key)
}

inline fun <reified T : Parcelable> Intent.parcelableArrayList(key: String): ArrayList<T>? = when {
    SDK_INT >= 33 -> getParcelableArrayListExtra(key, T::class.java)
    else -> @Suppress("DEPRECATION") getParcelableArrayListExtra(key)
}


internal fun Long.formatAsDate(): String {
    return when (this) {
        in 0..TEN_MINUTES -> SimpleDateFormat(PATTERN_1, Locale.getDefault()).format(Date(this))
        in TEN_MINUTES..ONE_HOUR -> SimpleDateFormat(PATTERN_2, Locale.getDefault()).format(Date(this))
        in ONE_HOUR..TEN_HOURS -> SimpleDateFormat(PATTERN_3, Locale.getDefault()).format(Date(this))
        else -> SimpleDateFormat(PATTERN_4, Locale.getDefault()).format(Date(this))
    }
}

internal fun CharSequence?.formatForDisplaying(): CharSequence {
    return if(this.isNullOrBlank())
        "Unknown"
    else if(this.isNullOrEmpty())
        "Unknown"
    else
        this
}

internal fun Short.toSliderValue(): Float {
    return (this / 100).toFloat()
}

internal fun Float.toAdjustableValue(): Short {
    return (this * 100).toInt().toShort()
}
internal fun MaterialToolbar.setOnBackFragmentNavigation() {
    this.setNavigationOnClickListener {
        it.findNavController().popBackStack()
    }
}
