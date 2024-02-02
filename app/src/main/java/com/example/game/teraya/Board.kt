package com.example.game.teraya

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
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
    private val mGridArray: MutableList<List<Grid>> = ArrayList()
    private lateinit var mCellArray: MutableList<List<TextView>>
    private lateinit var mCurrentCell: TextView
    private var mGameOverCallBack: GameOverCallBack? = null
    private var inputRow = -1
    private var inputColumn = -1

    init {
        init(context, attrs, defStyleAttr)
    }

    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        val padding = 1.dp
        setPadding(padding, padding, padding, padding)
        setBackgroundColor(context.getColor(R.color.bw))
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
                                params.topMargin = 1.dp
                            }

                            else -> {
                                params.addRule(BELOW, mGridArray[1][0].id)
                                params.topMargin = 1.dp
                            }
                        }
                    }

                    1 -> {
                        params.addRule(RIGHT_OF, gridList[j - 1].id)
                        params.addRule(ALIGN_TOP, gridList[j - 1].id)
                        params.leftMargin = 1.dp
                    }

                    else -> {
                        params.addRule(RIGHT_OF, gridList[j - 1].id)
                        params.addRule(ALIGN_TOP, gridList[j - 1].id)
                        params.leftMargin = 1.dp
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
                cell.setTag(R.id.teraya, " ")
                cell.setTextColor(context.getColor(R.color.bw))
                cell.setBackgroundColor(context.getColor(R.color.wb))
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
        if ((mCurrentCell.getTag(R.id.isEnable) as Boolean) && !(mCurrentCell.getTag(R.id.isDone) as Boolean)) {
            inputRow = v.getTag(R.id.row) as Int
            inputColumn = v.getTag(R.id.column) as Int
            lightRowAndColumn(inputRow, inputColumn)
        }
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
                    } else {
                        mCellArray[i][j].foreground = context.getDrawable(R.drawable.cell_cv)
                    }
                }
            }
            return
        }

        for (i in 0..8) {
            for (j in 0..8) {
                if (!(mCellArray[i][j].getTag(R.id.isFinish) as Boolean))
                    mCellArray[i][j].setBackgroundColor(context.getColor(R.color.wb))
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
        if ((mCellArray[inputRow][inputColumn].getTag(R.id.isEnable) as Boolean)
            && !(mCellArray[inputRow][inputColumn].getTag(R.id.isDone) as Boolean)
            && !(mCellArray[inputRow][inputColumn].getTag(R.id.isFinish) as Boolean)
        ) {
            mCellArray[inputRow][inputColumn]
                .setBackgroundColor(context.getColor(R.color.wb))
            mCellArray[inputRow][inputColumn].text = str
            mCellArray[inputRow][inputColumn].paint.isFakeBoldText = true
            mCellArray[inputRow][inputColumn].setTextColor(
                if (str == "X") Color.RED
                else Color.BLUE
            )
            mCellArray[inputRow][inputColumn].setTag(R.id.isDone, true)
            val checkGrid = checkGirdFinish(inputRow, inputColumn)
            revert(inputRow, inputColumn)
            mGameOverCallBack?.changeView(str)
            if (checkGrid) {
                checkBoardFinish()
            }
        }
    }

    private fun checkBoardFinish() {
        val board = ArrayList<String>()
        board.add(
            "${mCellArray[0][0].getTag(R.id.teraya)}${mCellArray[0][3].getTag(R.id.teraya)}${
                mCellArray[0][6].getTag(
                    R.id.teraya
                )
            }"
        )
        board.add(
            "${mCellArray[3][0].getTag(R.id.teraya)}${mCellArray[3][3].getTag(R.id.teraya)}${
                mCellArray[3][6].getTag(
                    R.id.teraya
                )
            }"
        )
        board.add(
            "${mCellArray[6][0].getTag(R.id.teraya)}${mCellArray[6][3].getTag(R.id.teraya)}${
                mCellArray[6][6].getTag(
                    R.id.teraya
                )
            }"
        )

        val isFinish: String = isFinish(board)
        if (isFinish == "X" || isFinish == "O") {
            mGameOverCallBack?.gameOver(isFinish)
        }
    }

    fun loadMap() {
        for (i in mCellArray.indices) {
            val array = mCellArray[i]
            for (j in array.indices) {
                val cell = array[j]
                cell.text = " "
                cell.setTag(R.id.isEnable, true)
                cell.setTag(R.id.isDone, false)
                cell.setTag(R.id.isFinish, false)
                cell.setTag(R.id.teraya, " ")
                cell.setBackgroundColor(context.getColor(R.color.wb))
                cell.foreground = null
            }
        }
    }

    private fun checkGirdFinish(rawRow: Int, rawColumn: Int): Boolean {
        val rowStart = when (rawRow + 1) {
            in 1..3 -> 1 - 1
            in 4..6 -> 4 - 1
            in 7..9 -> 7 - 1
            else -> throw IllegalArgumentException("invalid rawRow: $rawRow")
        }
        val columnStart = when (rawColumn + 1) {
            in 1..3 -> 1 - 1
            in 4..6 -> 4 - 1
            in 7..9 -> 7 - 1
            else -> throw IllegalArgumentException("invalid rawColumn: $rawColumn")
        }
        val grid = ArrayList<String>()
        grid.add("${mCellArray[rowStart][columnStart].text}${mCellArray[rowStart][columnStart + 1].text}${mCellArray[rowStart][columnStart + 2].text}")
        grid.add("${mCellArray[rowStart + 1][columnStart].text}${mCellArray[rowStart + 1][columnStart + 1].text}${mCellArray[rowStart + 1][columnStart + 2].text}")
        grid.add("${mCellArray[rowStart + 2][columnStart].text}${mCellArray[rowStart + 2][columnStart + 1].text}${mCellArray[rowStart + 2][columnStart + 2].text}")

        val isFinish: String = isFinish(grid)

        if (isFinish == "X" || isFinish == "O") {
            mCellArray[rowStart][columnStart].setTag(R.id.teraya, if (isFinish == "X") "X" else "O")
            for (i in rowStart..rowStart + 2) {
                for (j in columnStart..columnStart + 2) {
                    mCellArray[i][j].apply {
                        setTag(R.id.isFinish, true)
                        setTag(R.id.isEnable, false)
                        setBackgroundColor(
                            if (isFinish == "X" && this.text.toString() == "X") Color.RED
                            else if (isFinish == "O" && this.text.toString() == "O") Color.BLUE
                            else context.getColor(R.color.wb)
                        )
                    }
                }
            }
        }
        return !(isFinish != "X" && isFinish != "O")
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
                    mCellArray[i][j].setBackgroundColor(context.getColor(R.color.wb))
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

    private fun check(row: Int, column: Int) {
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
        fun gameOver(str: String)
        fun changeView(str: String)
        fun check(str: String)
    }

}
