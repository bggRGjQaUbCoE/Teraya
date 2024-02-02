package com.example.game.teraya

import android.content.res.Resources

val Number.dp get() = (toFloat() * Resources.getSystem().displayMetrics.density).toInt()
