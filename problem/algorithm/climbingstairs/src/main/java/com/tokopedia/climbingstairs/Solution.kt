package com.tokopedia.climbingstairs

object Solution {

    private lateinit var arr: Array<Long>

    fun climbStairs(n: Int): Long {
        // TODO, return in how many distinct ways can you climb to the top. Each time you can either climb 1 or 2 steps.
        // 1 <= n < 90
        arr = Array(n+1) {0L}
        return climbStairsHelper(n)
    }

    private fun climbStairsHelper(n: Int): Long {
        return when {
            n < 0 -> 0
            n == 0 -> 1
            arr[n] != 0L -> arr[n]
            else -> {
                arr[n] = climbStairsHelper(n-2) + climbStairsHelper(n-1)
                climbStairsHelper(n-2) + climbStairsHelper(n-1)
            }
        }
    }

}