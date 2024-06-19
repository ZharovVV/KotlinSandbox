package com.github.zharovvv.leetcode

fun main() {
    val nums = intArrayOf(0, 1, 2, 2, 3, 0, 4, 2)
    val result = Solution().removeElement(nums, 2)
    println("result = $result")
    println("nums = ${nums.toList()}")
}

class Solution {
    fun removeElement(nums: IntArray, `val`: Int): Int {
        var counter = 0
        var shift = 0
        for (i in 0 until nums.size) {
            val number = nums[i]
            if (number != `val`) {
                counter++
                if (shift != 0) {
                    nums[i - shift] = number
                }
            } else {
                shift++
            }
        }
        return counter
    }
}