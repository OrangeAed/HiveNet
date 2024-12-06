package com.bignerdranch.andriod.hivenet

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.graphics.Bitmap
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import kotlin.math.min
import kotlin.math.sqrt

class HexagonGridLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : FrameLayout(context, attrs, defStyle) {

    inner class Hex(var image: ImageView, var piece: ImageView?, var black: Boolean?)

    private var hexagonSpacing: Float
    private val margin = 50
    private val topMargin: Int
    private val r = 7
    private val c = 7
    private val hexagonImages: Array<Array<Hex?>> = arrayOf(
        arrayOfNulls(7),
        arrayOfNulls(7),
        arrayOfNulls(7),
        arrayOfNulls(7),
        arrayOfNulls(7),
        arrayOfNulls(7),
        arrayOfNulls(7),
        arrayOfNulls(7),
        arrayOfNulls(7)
    )
    var hexagonHeight: Float
    var hexagonWidth: Float
    init {
        val windowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
        val height = windowManager.currentWindowMetrics.bounds.height()
        val width = windowManager.currentWindowMetrics.bounds.width()
        val boardHeight = min(height, width)
        topMargin = height / 15
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
        val xOffset = (0.759 * hexagonHeight) + hexagonSpacing
        val yOffset = hexagonHeight + hexagonSpacing
        for (row in 0 until rows ) {
            for (col in 0 until columns) {

                val x = col * xOffset + margin
                var y = row * yOffset + topMargin

                if (col % 2 == 1) {
                    y += yOffset / 2
                }

                val hexagonImage = ImageView(context).apply {
                    setImageResource(R.drawable.hexagon)
                    layoutParams = LayoutParams(hexagonWidth.toInt() , hexagonHeight.toInt())
                    this.x = x.toFloat()
                    this.y = y
                    tag="hex"
                }
                val hex = Hex(hexagonImage, null, null)
                hexagonImages[row][col] = hex
                addView(hexagonImage)
            }
        }
    }
    private fun initializeHexagonsLandscape() {
        val rows = c
        val columns = r
        val xOffset = (0.82 * hexagonHeight) + hexagonSpacing
        val yOffset = hexagonHeight

        for (row in 0 until rows) {
            for (col in 0 until columns ) {
                var x = col * xOffset + margin
                val y = row * yOffset + margin

                if (row % 2 == 1) {
                    x += yOffset / 2
                }

                val hexagonImage = ImageView(context).apply {
                    setImageResource(R.drawable.hexagonlandscape)
                    layoutParams = LayoutParams(hexagonWidth.toInt(), hexagonHeight.toInt())
                    this.x = x.toFloat()
                    this.y = y
                    tag="hex"
                }
                val hex = Hex(hexagonImage, null, null)
                hexagonImages[row][col] = hex
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

    fun getHex(row: Int, column: Int): Hex? {
        return hexagonImages[row][column]
    }
    fun findClosestCell(view: ImageView): Hex? {
        Log.d(TAG, "bug is at x: ${view.x}, y: ${view.y}")
        val closestHexagon = hexagonImages.flatten().filterNotNull().minByOrNull { hexagonImage ->
            val dx = view.x - hexagonImage.image.x
            val dy = view.y - hexagonImage.image.y
            val distance = sqrt(dx * dx + dy * dy)
            distance
        }

        if (closestHexagon != null) {
            val dx = view.x - closestHexagon.image.x
            val dy = view.y - closestHexagon.image.y
            val distance = sqrt(dx * dx + dy * dy)
            if (distance > hexagonHeight * 2 || closestHexagon.piece != null) {
                return null
            }
            Log.d(TAG, "x: ${closestHexagon.image.x}, y: ${closestHexagon.image.y}")
        }
        return closestHexagon
    }

    fun removePiece(releasedChild: ImageView): Boolean? {
        val hex = hexagonImages.flatten().filterNotNull().find {
            it.piece == releasedChild
        }
        val original = hex?.black
        hex?.piece = null
        hex?.black = null
        return original
    }
    fun placePiece(hex: Hex, piece: ImageView, black: Boolean) {
        hex.piece = piece
        hex.black = black
    }
}