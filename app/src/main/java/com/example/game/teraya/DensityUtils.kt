package com.example.game.teraya

import android.content.Context
import android.graphics.Point
import android.os.Build
import android.util.TypedValue
import android.view.WindowManager

/**
 * 常用单位转换的辅助类
 */
class DensityUtils private constructor() {
    init {
        /* cannot be instantiated */
        throw UnsupportedOperationException("cannot be instantiated")
    }

    companion object {
        private var screenWidth = 0
        private var screenHeight = 0

        /**
         * dp转px
         */
        @JvmStatic
        fun dp2px(context: Context, dpVal: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dpVal,
                context.resources.displayMetrics
            ).toInt()
        }

        /**
         * sp转px
         */
        fun sp2px(context: Context, spVal: Float): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, spVal,
                context.resources.displayMetrics
            ).toInt()
        }

        /**
         * px转dp
         */
        fun px2dp(context: Context, pxVal: Float): Float {
            val scale = context.resources.displayMetrics.density
            return pxVal / scale
        }

        /**
         * px转sp
         */
        fun px2sp(context: Context, pxVal: Float): Float {
            return pxVal / context.resources.displayMetrics.scaledDensity
        }

        fun getScreenHeight(c: Context): Int {
            if (screenHeight == 0) {
                val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = wm.defaultDisplay
                val size = Point()
                display.getSize(size)
                screenHeight = size.y
            }
            return screenHeight
        }

        fun getScreenWidth(c: Context): Int {
            if (screenWidth == 0) {
                val wm = c.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = wm.defaultDisplay
                val size = Point()
                display.getSize(size)
                screenWidth = size.x
            }
            return screenWidth
        }

        val isAndroid5: Boolean
            get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
    }
}
