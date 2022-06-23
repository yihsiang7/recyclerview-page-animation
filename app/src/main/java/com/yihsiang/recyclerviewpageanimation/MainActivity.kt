package com.yihsiang.recyclerviewpageanimation

import android.content.Context
import android.graphics.Point
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.yihsiang.recyclerviewpageanimation.MainActivity.Companion.EDGE_SPACING
import com.yihsiang.recyclerviewpageanimation.MainActivity.Companion.ITEM_SPACING
import com.yihsiang.recyclerviewpageanimation.databinding.ActivityMainBinding
import kotlin.math.abs
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sign

class MainActivity : AppCompatActivity() {

    companion object {
        const val EDGE_SPACING = 20 * 3
        const val ITEM_SPACING = (5 * 3) / 2f
    }

    private val images = listOf(
        "https://images.unsplash.com/photo-1648737119247-e93f56878edf?ixlib=rb-1.2.1&ixid=MnwxMjA3fDF8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1772&q=80",
        "https://images.unsplash.com/photo-1655439190997-e4c90e5a2526?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=2970&q=80",
        "https://images.unsplash.com/photo-1655499320562-1681aaec6fba?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=928&q=80",
        "https://images.unsplash.com/photo-1655671051139-e206ea9f1888?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=928&q=80",
        "https://images.unsplash.com/photo-1655712091148-f12e2e855d0a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80",
        "https://images.unsplash.com/photo-1655720358066-2aab2a903ef8?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80",
        "https://images.unsplash.com/photo-1655703959912-07678032223a?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1770&q=80",
        "https://images.unsplash.com/photo-1655740933618-b210796141bb?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80",
        "https://images.unsplash.com/photo-1655742039371-019042b2b0c6?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=1770&q=80",
        "https://images.unsplash.com/photo-1655735678729-1fde3a626c2f?ixlib=rb-1.2.1&ixid=MnwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8&auto=format&fit=crop&w=774&q=80")

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = ImageAdapter()
        with(binding.contents) {
            this.adapter = adapter
            layoutManager = ScaleLayoutManager(this@MainActivity)
            PagerSnapHelper()
                .attachToRecyclerView(this)
            addItemDecoration(SpacingItemDecoration())
        }

        adapter.submitList(images)
    }

}

class ScaleLayoutManager(context: Context) : LinearLayoutManager(context, HORIZONTAL, false) {

    private var isLayoutCompleted = false

    companion object {
        // 縮放倍數
        private const val SHRINK_AMOUNT = 0.1f
    }

    override fun scrollHorizontallyBy(
        dx: Int,
        recycler: RecyclerView.Recycler?,
        state: RecyclerView.State?
    ): Int {
        scaleChild()
        return super.scrollHorizontallyBy(dx, recycler, state)
    }

    override fun onLayoutCompleted(state: RecyclerView.State?) {
        super.onLayoutCompleted(state)
        if (!isLayoutCompleted) {
            scaleChild()
            isLayoutCompleted = true
        }
    }

    private fun scaleChild() {
        val containerCenter = width / 2f

        (0 until childCount).forEach { count ->
            val child = getChildAt(count) ?: return@forEach

            val childCenter = (child.left + child.right) / 2f
            val distanceToCenter = abs(containerCenter - childCenter)

            // 距離容器中心點(containerCenter)越遠縮小程度越大
            val scaleAmount = (distanceToCenter / containerCenter).coerceAtMost(1f)
            // 0.9~1.0
            val scale = 1 - SHRINK_AMOUNT * scaleAmount

            child.scaleX = scale
            child.scaleY = scale


            // 當前位置不需要做偏移
            if (childCenter == containerCenter) return@forEach

            // -1向左偏移,1向右偏移
            val translationDirection = if (childCenter > containerCenter) -1 else 1
            // 當元件縮小時會出現多餘的空白間距，所以元件要向右/向左偏移填滿多餘空白間距
            val translationXFromScale = translationDirection * child.width * (1 - scale) / 2f
            child.translationX = translationXFromScale
        }
    }
}

class SpacingItemDecoration : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val itemWidth = parent.width - EDGE_SPACING * 2
        view.layoutParams = view.layoutParams.apply { width = itemWidth }

        when (parent.getChildAdapterPosition(view)) {
            0 -> {
                outRect.left = EDGE_SPACING
                outRect.right = ITEM_SPACING.roundToInt()
            }
            parent.adapter?.itemCount?.minus(1) -> {
                outRect.left = ITEM_SPACING.roundToInt()
                outRect.right = EDGE_SPACING
            }
            else -> {
                outRect.left = ITEM_SPACING.roundToInt()
                outRect.right = ITEM_SPACING.roundToInt()
            }
        }
    }
}