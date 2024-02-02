package com.example.game.teraya

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.RelativeLayout
import android.widget.TextView

class Grid @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {
    private val TEXT_SIZE = 40.dp
    private lateinit var mTextArrays: MutableList<List<TextView>>

    init {
        init(context)
    }

    private fun init(context: Context) {
        val padding = 1.dp
        setPadding(padding, padding, padding, padding)
        mTextArrays = ArrayList()
        for (i in 0..2) {
            val viewList: MutableList<TextView> = ArrayList()
            for (j in 0..2) {
                val textView = TextView(context)
                textView.width = TEXT_SIZE
                textView.height = TEXT_SIZE
                textView.setBackgroundColor(context.getColor(R.color.wb))
                textView.id = generateViewId()
                textView.gravity = Gravity.CENTER
                //                textView.setText(String.format("(%1$d,%2$d)", i, j));
                addView(textView)
                viewList.add(textView)
                val params = textView.layoutParams as LayoutParams
                when (j) {
                    0 -> {
                        when (i) {
                            0 -> {
                                params.addRule(ALIGN_PARENT_START)
                                params.addRule(ALIGN_PARENT_LEFT)
                            }

                            1 -> {
                                params.addRule(BELOW, mTextArrays[0][0].id)
                                params.topMargin = 1.dp
                            }

                            else -> {
                                params.addRule(BELOW, mTextArrays[1][0].id)
                                params.topMargin = 1.dp
                            }
                        }
                    }

                    1 -> {
                        params.addRule(RIGHT_OF, viewList[j - 1].id)
                        params.addRule(ALIGN_TOP, viewList[j - 1].id)
                        params.leftMargin = 1.dp
                    }

                    else -> {
                        params.addRule(RIGHT_OF, viewList[j - 1].id)
                        params.addRule(ALIGN_TOP, viewList[j - 1].id)
                        params.leftMargin = 1.dp
                    }
                }
            }
            mTextArrays.add(viewList)
        }
        setBackgroundColor(context.getColor(R.color.bw))
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = (6 + 3 * 40).dp
        super.onMeasure(size, size)
        //        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
    }

    val textArrays: List<List<TextView>>
        get() = mTextArrays
}
