package com.tokopedia.minimumpathsum

object Solution {
    fun minimumPathSum(matrix: Array<IntArray>): Int {
        // TODO, find a path from top left to bottom right which minimizes the sum of all numbers along its path, and return the sum
        // below is stub
        val column = matrix[0].size
        val row = matrix.size

        for (i in 1 until row){
            matrix[i][0] += matrix[i-1][0]
        }

        for (j in 1 until column) {
            matrix[0][j] += matrix[0][j-1]
        }

        for (i in 1 until row) {
            for (j in 1 until column) {
                matrix[i][j] += matrix[i - 1][j - 1].coerceAtMost(
                    matrix[i - 1][j].coerceAtMost(matrix[i][j - 1])
                )
            }
        }

        return matrix[row - 1][column -1]
    }

}
