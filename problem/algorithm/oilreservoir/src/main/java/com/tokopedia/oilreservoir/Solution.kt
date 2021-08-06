package com.tokopedia.oilreservoir

/**
 * Created by fwidjaja on 2019-09-24.
 */
object Solution {
    fun collectOil(height: IntArray): Int {
        // TODO, return the amount of oil blocks that could be collected
        val leftMax: Array<Int> = getLeftMax(height)
        val rightMax: Array<Int> = getRightMax(height)
        var answer = 0

        for (i in 1 until height.size) {
            val oilHeight = leftMax[i].coerceAtMost(rightMax[i])
            if (oilHeight >= height[i]) answer += oilHeight - height[i]
        }

        return answer
    }

    private fun getLeftMax(height: IntArray): Array<Int> {
        val leftMax: Array<Int> = Array(height.size) {0}
        for (i in 1 until height.size){
            leftMax[i] = height[i - 1].coerceAtLeast(leftMax[i - 1])
        }
        return leftMax
    }

    private fun getRightMax(height: IntArray): Array<Int> {
        val rightMax: Array<Int> = Array(height.size) {0}
        for (i in height.size-2 downTo 0){
            rightMax[i] = height[i + 1].coerceAtLeast(rightMax[i + 1])
        }
        return rightMax
    }
}
