package com.bignerdranch.andriod.hivenet

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import com.bignerdranch.andriod.hivenet.dataclasses.HexSpace
import kotlin.math.min
import kotlin.math.sqrt

class HexagonGridLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    private val hexagonImages = mutableListOf<HexSpace>()
    private var hexagonSpacing: Float

    private val margin = 50
    private val r = 7
    private val c = 7

    private var hexagonHeight: Float


    private var hexagonWidth: Float
    init {
        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val height = windowManager.currentWindowMetrics.bounds.height()
        val width = windowManager.currentWindowMetrics.bounds.width()
        val boardHeight = min(height, width)
        hexagonHeight = min(450, (boardHeight - margin * 2)/5 ).toFloat()
        hexagonWidth = hexagonHeight
        hexagonSpacing = hexagonHeight * -0.0741f
        val landscape = context.resources.configuration.orientation == ORIENTATION_LANDSCAPE
        if (landscape) {
            initializeHexagonsLandscape()

        } else {
            initializeHexagons()
        }
    }
    companion object {
        private const val TAG = "HexagonGridLayout"
    }
    private fun initializeHexagons() {
        val rows = r
        val columns = c
        val xOffset = (0.759 * hexagonHeight) + hexagonSpacing // Horizontal offset for hexagons
        val yOffset = hexagonHeight + hexagonSpacing // Vertical offset for hexagons
        for (row in 0 until rows) {
            for (col in 0 until columns) {

                val x = col * xOffset + margin
                var y = row * yOffset + margin

                // Adjust every other column to create a hexagonal staggered layout
                if (col % 2 == 1) {
                    y += yOffset / 2
                }

                // Create ImageView for hexagon and add it to the layout
                val hexagonImage = ImageView(context).apply {
                    setImageResource(R.drawable.hexagon) // Use hexagon.png from drawables
                    layoutParams = LayoutParams(hexagonWidth.toInt(), hexagonHeight.toInt())
                    this.x = x.toFloat()
                    this.y = y
                }
                hexagonImages.add(hexagonImage)
                addView(hexagonImage)
            }
        }
    }
    private fun initializeHexagonsLandscape() {
        val rows = c
        val columns = r
        val xOffset = (0.82 * hexagonHeight) + hexagonSpacing // Horizontal offset for hexagons
        val yOffset = hexagonHeight // Vertical offset for hexagons

        for (row in 0 until rows) {
            for (col in 0 until columns) {
                var x = col * xOffset + margin
                val y = row * yOffset + margin

                // Adjust every other row to create a hexagonal staggered layout
                if (row % 2 == 1) {
                    x += yOffset / 2
                }

                // Create ImageView for hexagon and add it to the layout
                val hexagonImage = ImageView(context).apply {
                    setImageResource(R.drawable.hexagonlandscape) // Use hexagon.png from drawables
                    layoutParams = LayoutParams(hexagonWidth.toInt(), hexagonHeight.toInt())
                    this.x = x.toFloat()
                    this.y = y
                }
                hexagonImages.add(hexagonImage)
                addView(hexagonImage)
            }
        }
    }

    // Method to add image to a hexagon at a given index
    /*fun addImageToHexagon(imageView: ImageView, index: Int) {
        val hexagonImage = hexagonImages[index]
        // Position the image in the center of the hexagon
        imageView.x = hexagonImage.x + hexagonRadius - imageView.width / 2
        imageView.y = hexagonImage.y + hexagonHeight / 2 - imageView.height / 2
        addView(imageView) // Add the image to the layout
    }*/

    // Find the closest cell for an image being dragged
    fun findClosestCell(view: View): ImageView? {
        Log.d(TAG, "bug is at x: ${view.x}, y: ${view.y}")
        val closestHexagon = hexagonImages.minByOrNull { hexagonImage ->
            val dx = view.x - hexagonImage.x
            val dy = view.y - hexagonImage.y
            val distance = sqrt(dx * dx + dy * dy) // Calculate distance to each hexagon center
            distance
        }
        if (closestHexagon != null) {
            Log.d(TAG, "x: ${closestHexagon.x}, y: ${closestHexagon.y}")
        }
        return closestHexagon
    }
}


