package com.example.comptegouttes

import android.app.Activity
import android.content.Intent
import android.view.GestureDetector
import android.view.MotionEvent

object SwipeNav {

    fun create(activity: Activity, current: String): GestureDetector {
        val order = listOf("counter", "generator", "calc", "info")
        return GestureDetector(activity, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(e1: MotionEvent?, e2: MotionEvent, vx: Float, vy: Float): Boolean {
                if (e1 == null) return false
                val dx = e2.x - e1.x
                val dy = e2.y - e1.y
                if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 120f && Math.abs(vx) > 200f) {
                    val idx = order.indexOf(current)
                    val next = if (dx < 0) idx + 1 else idx - 1
                    if (next < 0 || next > order.size - 1 || next == idx) return false
                    val target = when (order[next]) {
                        "counter" -> MainActivity::class.java
                        "generator" -> GeneratorActivity::class.java
                        "calc" -> CalcActivity::class.java
                        else -> InfoActivity::class.java
                    }
                    activity.startActivity(
                        Intent(activity, target).addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
                    )
                    return true
                }
                return false
            }
        })
    }
}
