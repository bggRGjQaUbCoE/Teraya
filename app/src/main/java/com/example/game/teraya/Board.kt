package com.example.game.teraya

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.widget.ThemeUtils
import com.example.game.teraya.Util.isFinish

class Board @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr), View.OnClickListener {
    private val TAG = Board::class.java.simpleName
    private val mGridArray: MutableList<List<Grid>> = ArrayList()
    private lateinit var mCellArray: MutableList<List<TextView>>
    private lateinit var mCurrentCell: TextView
    private val mErrorTextColor = "#ff0000"
    private val mLightTextColor = "#ffffff"
    private val mDefaultTextColor = "#000000"
    private val mLightBgColor = "#607d8b"
    private val mDefaultBgColor = "#ffffff"
    private val mDisableTextColor = "#e2e2e2"
    private var mGameOverCallBack: GameOverCallBack? = null

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val padding = DensityUtils.dp2px(context, 1f)
        setPadding(padding, padding, padding, padding)
        setBackgroundColor(Color.BLACK)
        for (i in 0..2) {
            val gridList: MutableList<Grid> = ArrayList()
            for (j in 0..2) {
                val grid = Grid(context, attrs, defStyleAttr)
                grid.id = generateViewId()
                addView(grid)
                val params = grid.layoutParams as LayoutParams
                when (j) {
                    0 -> {
                        when (i) {
                            0 -> {
                                params.addRule(ALIGN_PARENT_START)
                                params.addRule(ALIGN_PARENT_LEFT)
                            }

                            1 -> {
                                params.addRule(BELOW, mGridArray[0][0].id)
                                params.topMargin = DensityUtils.dp2px(context, 1f)
                            }

                            else -> {
                                params.addRule(BELOW, mGridArray[1][0].id)
                                params.topMargin = DensityUtils.dp2px(context, 1f)
                            }
                        }
                    }

                    1 -> {
                        params.addRule(RIGHT_OF, gridList[j - 1].id)
                        params.addRule(ALIGN_TOP, gridList[j - 1].id)
                        params.leftMargin = DensityUtils.dp2px(context, 1f)
                    }

                    else -> {
                        params.addRule(RIGHT_OF, gridList[j - 1].id)
                        params.addRule(ALIGN_TOP, gridList[j - 1].id)
                        params.leftMargin = DensityUtils.dp2px(context, 1f)
                    }
                }
                gridList.add(grid)
            }
            mGridArray.add(gridList)
        }
        mCellArray = ArrayList()
        for (i in 0..8) { //初始化Cell Array
            val cellArray: MutableList<TextView> = ArrayList()
            for (j in 0..8) {
                val x = if (i < 3) 0 else if (i < 6) 1 else 2 //3x3 的格子
                val y = if (j < 3) 0 else if (j < 6) 1 else 2
                val grid = mGridArray[x][y]
                val gridTextArrays = grid.textArrays
                val cell = gridTextArrays[i - x * 3][j - y * 3]
                cell.setTag(R.id.row, i)
                cell.setTag(R.id.column, j)
                cell.setTag(R.id.isEnable, true)
                cell.setTextColor(Color.parseColor(mDefaultTextColor))
                cell.setBackgroundColor(Color.parseColor(mDefaultBgColor))
                cell.setOnClickListener(this)
                cellArray.add(j, cell)
            }
            mCellArray.add(i, cellArray)
        }
    }

    fun setGameOverCallBack(mGameOverCallBack: GameOverCallBack?) {
        this.mGameOverCallBack = mGameOverCallBack
    }

    override fun onClick(v: View) {
        mCurrentCell = v as TextView
        check(v.getTag(R.id.row) as Int, v.getTag(R.id.column) as Int)
        if ((mCurrentCell.getTag(R.id.isEnable) as Boolean) && !(mCurrentCell.getTag(R.id.isDone) as Boolean))
            lightRowAndColumn(v.getTag(R.id.row) as Int, v.getTag(R.id.column) as Int)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun revert(rawRow: Int, rawColumn: Int) {
        val row = with((rawRow + 1) % 3) {
            if (this == 0) 3 else this
        }
        val column = with((rawColumn + 1) % 3) {
            if (this == 0) 3 else this
        }

        val rowStart = when (row) {
            1 -> 1 - 1
            2 -> 4 - 1
            3 -> 7 - 1
            else -> throw IllegalArgumentException("invalid row: $row")
        }
        val columnStart = when (column) {
            1 -> 1 - 1
            2 -> 4 - 1
            3 -> 7 - 1
            else -> throw IllegalArgumentException("invalid column: $column")
        }

        if (mCellArray[rowStart][columnStart].getTag(R.id.isFinish) as Boolean) {
            for (i in 0..8) {
                for (j in 0..8) {
                    if (!(mCellArray[i][j].getTag(R.id.isFinish) as Boolean)) {
                        mCellArray[i][j].setTag(R.id.isEnable, true)
                        mCellArray[i][j].foreground = null
                    }
                }
            }
            return
        }

        for (i in 0..8) {
            for (j in 0..8) {
                if (!(mCellArray[i][j].getTag(R.id.isFinish) as Boolean))
                    mCellArray[i][j].setBackgroundColor(Color.parseColor(mLightTextColor))
                if (i in rowStart..rowStart + 2 && j in columnStart..columnStart + 2) { // 当前区域
                    mCellArray[i][j].foreground = null
                    mCellArray[i][j].setTag(R.id.isEnable, true)
                } else {
                    mCellArray[i][j].foreground = context.getDrawable(R.drawable.cell_cv)
                    mCellArray[i][j].setTag(R.id.isEnable, false)
                }
            }
        }

    }

    fun inputText(str: String) {
        if (!::mCurrentCell.isInitialized)
            return
        val row = mCurrentCell.getTag(R.id.row) as Int
        val column = mCurrentCell.getTag(R.id.column) as Int
        if (!(mCurrentCell.getTag(R.id.isDone) as Boolean)) {
            mCellArray[row][column]
                .setBackgroundColor(Color.parseColor(mLightTextColor))
            mCurrentCell.text = str
            mCurrentCell.paint.isFakeBoldText = true
            mCurrentCell.setTextColor(
                if (str == "X") Color.RED
                else Color.BLUE
            )
            mCurrentCell.setTag(R.id.isDone, true)
            val checkGrid = checkGirdFinish(row, column)
            revert(row, column)
            if (checkGrid) {
                //  checkBoard      mGameOverCallBack?.gameOver()
            }
            mGameOverCallBack?.changeView(str)
        }
    }

    fun loadMap() {
        //if (TextUtils.isEmpty(map)) return
        for (i in mCellArray.indices) {
            val array = mCellArray[i]
            for (j in array.indices) {
                val cell = array[j]
                // val s = map.substring(9 * i + j, 9 * i + j + 1)
                //  if ("0" = s) {
                cell.text = " "
                cell.setTag(R.id.isEnable, true)
                cell.setTag(R.id.isDone, false)
                cell.setTag(R.id.isFinish, false)
                cell.setTextColor(Color.parseColor(mDisableTextColor))
                //}
            }
        }
    }

    private fun checkGirdFinish(rawRow: Int, rawColumn: Int): Boolean {
        var rowStart = when (rawRow + 1) {
            in 1..3 -> 1 - 1
            in 4..6 -> 4 - 1
            in 7..9 -> 7 - 1
            else -> throw IllegalArgumentException("invalid rawRow: $rawRow")
        }
        var columnStart = when (rawColumn + 1) {
            in 1..3 -> 1 - 1
            in 4..6 -> 4 - 1
            in 7..9 -> 7 - 1
            else -> throw IllegalArgumentException("invalid rawColumn: $rawColumn")
        }
        val boad = ArrayList<String>()
        boad.add("${mCellArray[rowStart][columnStart].text}${mCellArray[rowStart][columnStart + 1].text}${mCellArray[rowStart][columnStart + 2].text}")
        boad.add("${mCellArray[rowStart + 1][columnStart].text}${mCellArray[rowStart + 1][columnStart + 1].text}${mCellArray[rowStart + 1][columnStart + 2].text}")
        boad.add("${mCellArray[rowStart + 2][columnStart].text}${mCellArray[rowStart + 2][columnStart + 1].text}${mCellArray[rowStart + 2][columnStart + 2].text}")

        val isFinish: String = isFinish(boad)

        if (isFinish == "X" || isFinish == "O") {
            for (i in rowStart..rowStart + 2) {
                for (j in columnStart..columnStart + 2) {
                    mCellArray[i][j].apply {
                        setTag(R.id.isFinish, true)
                        setTag(R.id.isEnable, false)
                        setBackgroundColor(
                            if (isFinish == "X" && this.text.toString() == "X") Color.RED
                            else if (isFinish == "O" && this.text.toString() == "O") Color.BLUE
                            else Color.WHITE // TO DO
                        )
                    }
                }
            }
        }
        Log.d("dfgdfgdfgdddd", "isFinish: $isFinish")
        return !(isFinish != "X" && isFinish != "O")
    }

    private fun checkGameError(row: Int, column: Int): Boolean {
        var result: Boolean
        result = checkSection(row, column)
        if (result) return result
        //check row
        for (i in 0..8) {
            val value = mCellArray[i][column].text.toString()
            if (TextUtils.isEmpty(value)) continue
            for (j in i..8) {
                if (i == j) continue
                if (value == mCellArray[j][column].text.toString()) {
                    Log.d(
                        TAG,
                        String.format(
                            "row error,value:%1\$s in row:%2\$d and column:%3\$d",
                            value,
                            row,
                            column
                        )
                    )
                    result = true
                    break
                }
            }
        }
        if (result) return result

        //check column
        for (i in 0..8) {
            val value = mCellArray[row][i].text.toString()
            if (TextUtils.isEmpty(value)) continue
            for (j in i..8) {
                if (i == j) continue
                if (value == mCellArray[row][j].text.toString()) {
                    Log.d(
                        TAG,
                        String.format(
                            "column error,value:%1\$s in row:%2\$d and column:%3\$d",
                            value,
                            row,
                            column
                        )
                    )
                    result = true
                    break
                }
            }
        }
        return result
    }

    private fun checkSection(row: Int, column: Int): Boolean {
        var result = false
        val value = mCellArray[row][column].text.toString()
        if (TextUtils.isEmpty(value)) {
            return result
        }
        val start_i = if (row < 3) 0 else if (row < 6) 3 else 6 //3x3 格子的边界
        val start_j = if (column < 3) 0 else if (column < 6) 3 else 6
        val end_i = start_i + 3
        val end_j = start_j + 3
        for (i in start_i until end_i) {
            for (j in start_j until end_j) {
                if (i == row && j == column) continue
                if (value == mCellArray[i][j].text.toString()) { //如果3x3格子的内容有重复的数字则返回错误
                    Log.d(
                        TAG,
                        String.format(
                            "section error,value:%1\$s in row:%2\$d and column:%3\$d",
                            value,
                            row,
                            column
                        )
                    )
                    result = true
                    break
                }
            }
        }
        return result
    }

    @SuppressLint("RestrictedApi", "UseCompatLoadingForDrawables")
    private fun lightRowAndColumn(rawRow: Int, rawColumn: Int) {
        val row = with((rawRow + 1) % 3) {
            if (this == 0) 3 else this
        }
        val column = with((rawColumn + 1) % 3) {
            if (this == 0) 3 else this
        }

        val rowStart = when (row) {
            1 -> 1 - 1
            2 -> 4 - 1
            3 -> 7 - 1
            else -> throw IllegalArgumentException("invalid row: $row")
        }
        val columnStart = when (column) {
            1 -> 1 - 1
            2 -> 4 - 1
            3 -> 7 - 1
            else -> throw IllegalArgumentException("invalid column: $column")
        }

        for (i in 0..8) {
            for (j in 0..8) {
                if (!(mCellArray[i][j].getTag(R.id.isFinish) as Boolean))
                    mCellArray[i][j].setBackgroundColor(Color.parseColor(mLightTextColor))
                if (i in rowStart..rowStart + 2 && j in columnStart..columnStart + 2) {// 下一步的区域
                    if (!(mCellArray[i][j].getTag(R.id.isFinish) as Boolean)) {
                        mCellArray[i][j].foreground =
                            if (mCellArray[i][j].getTag(R.id.isEnable) as Boolean) context.getDrawable(
                                R.drawable.cell_cv
                            )
                            else context.getDrawable(R.drawable.cell_cv_cv)
                    }
                } else {
                    mCellArray[i][j].foreground =
                        if (mCellArray[i][j].getTag(R.id.isEnable) as Boolean) null
                        else context.getDrawable(R.drawable.cell_cv)
                }
            }
        }

        mCellArray[rawRow][rawColumn].setBackgroundColor(
            ThemeUtils.getThemeAttrColor(
                context,
                com.google.android.material.R.attr.colorPrimary
            )
        )
    }

    fun check(row: Int, column: Int) {
        val str = """
            row: $row ,, column: $column
            text: ${mCellArray[row][column].text}
            isEnable: ${mCellArray[row][column].getTag(R.id.isEnable)}
            isDone: ${mCellArray[row][column].getTag(R.id.isDone)}
            isFinish: ${mCellArray[row][column].getTag(R.id.isFinish)}
        """.trimIndent()
        mGameOverCallBack?.check(str)
    }

    interface GameOverCallBack {
        fun gameOver()
        fun changeView(str: String)
        fun check(str: String)
    }

}
