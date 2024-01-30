package com.example.game.teraya

object Util {

    fun isFinish(board: ArrayList<String>): String {
        var heng: Int // 记录横的和值， 局部变量必须先赋值再使用哦！
        var zong: Int // 记录纵的和值
        var left = 0 // 记录左撇的和值
        var right = 0 // 记录右捺的和值
        var includeempty = false // 含有空格，默认位没有，false
        val length = board.size // 记录字符串数组的长度
        for (i in 0 until length) {
            heng = 0 // 记录第0行（习惯，也就是第0行的总和，第二次的话就是第0行的总和，便于理解）
            zong = 0 // 记录第0列（习惯，也就是第1列的总和，第二次的话就是第1列的总和，便于理解）
            for (j in 0 until length) {
                heng += board[i][j].code // 把第0行，第0列的元素给heng，之后是第0行，第1列；之后是第0行第2列... ...
                zong += board[j][i].code // 把第0列，第0行的元素给zong，之后是第1列，第0行；之后是第2列第0行... ...
                if (board[i][j] == ' ') includeempty = true // 如果含有一个空格，则标注含有空格。 每一个位置均有走过。
            }
            // 至此，第i行列的综合出来了。做个判断，如果有胜出的就停止。如果有位空的就更新includeempty位true
            if (heng == 'X'.code * length || zong == 'X'.code * length) return "X" // 如果横或者纵全为X，则返回X，停止程序 解释：如果整行或者整列位同一个字符，则正好满足等式。
            if (heng == 'O'.code * length || zong == 'O'.code * length) return "O" // 如果横或者纵全为O，则返回O，停止程序

            // 左撇的和
            left += board[i][i].code
            // 右捺的和
            right += board[i][length - i - 1].code
        }

        // 位层for循环结束后左撇和右捺的和计算完毕，可以做判断了。
        if (left == 'X'.code * length || right == 'X'.code * length) return "X"
        if (left == 'O'.code * length || right == 'O'.code * length) return "O"
        return if (includeempty == true) "Pending" else "Draw"
    }

}