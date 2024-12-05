package com.bignerdranch.andriod.hivenet

import MyReceiver
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.res.Configuration.ORIENTATION_LANDSCAPE
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.marginLeft
import androidx.core.view.updateLayoutParams
import androidx.customview.widget.ViewDragHelper
import com.bignerdranch.andriod.hivenet.databinding.ActivityGameBinding
import kotlin.math.min

class GameActivity : AppCompatActivity() {
    private lateinit var hexagonGridLayout: HexagonGridLayout
    private lateinit var binding: ActivityGameBinding
    private lateinit var dragHelper: ViewDragHelper
    private lateinit var service: ConnectionService
    private lateinit var receiver: BroadcastReceiver
    private var isBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            if (binder is ConnectionService.LocalBinder) {
                service = binder.getService()
                isBound = true
                receiver = MyReceiver(service)
                registerReceiver(
                    receiver,
                    IntentFilter(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
                )
            }
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
            unregisterReceiver(receiver)
        }
    }
    companion object {
        private const val TAG = "GameActivity"
    }

    fun movePiece(startRow: Int, startCol: Int, endRow: Int, endCol: Int) {
        val piece = hexagonGridLayout.getHex(startRow, startCol)?.hexSpace?.hivePiece
        val newLocation = hexagonGridLayout.getHex(endRow, endCol)?.image
        if (piece != null && newLocation != null) {
//            val layoutParams = piece.layoutParams as RelativeLayout.LayoutParams

            // Update the position using the closest cell's coordinates
//            layoutParams.leftMargin = newLocation.x.toInt()
//            layoutParams.topMargin = newLocation.y.toInt()
//
//            // Apply the updated layout params to the view
//            piece.layoutParams = layoutParams
            binding.root.invalidate()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Initialize hexagonal grid layout
        // Initialize hexagonal grid layout
        hexagonGridLayout = HexagonGridLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }
        binding.root.addView(hexagonGridLayout)
        // Add an initial image to be draggable
        addPieces()
        // Set up drag helper for interaction
        dragHelper = ViewDragHelper.create(binding.root as ViewGroup, 1.0f, DragHelperCallback())


    }
    override fun onStart() {
        super.onStart()
        // Bind to the same ConnectionService instance
        val intent = Intent(this, ConnectionService::class.java)
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
        }
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let { dragHelper.processTouchEvent(it) }
        return super.dispatchTouchEvent(event)
    }

    private fun createCopyOfImage(original: View, x: Float, y: Float) {
        // Create the copy
        val copy = ImageView(this).apply {
            setImageDrawable((original as ImageView).drawable)
            layoutParams = RelativeLayout.LayoutParams((hexagonGridLayout.hexagonWidth * .8).toInt(), (hexagonGridLayout.hexagonHeight * .8).toInt()).apply {
                topMargin = y.toInt() + (hexagonGridLayout.hexagonWidth * .1).toInt()
                leftMargin = x.toInt() + (hexagonGridLayout.hexagonHeight * .1).toInt()
            }
            tag = "copy" // Mark as a copy
        }

        // Add the copy to the parent layout
        binding.root.addView(copy)
        //copy.bringToFront()
    }

    // Adds draggable images to the bottom of the screen, spaced evenly
    private fun addPieces() {
        val screenHeight = resources.displayMetrics.heightPixels
        val screenWidth = resources.displayMetrics.widthPixels
        val boardHeight = min(screenHeight, screenWidth)
        val pieceHeight = min(450, (boardHeight - 50 * 2 - 40)/5 ).toInt()
        val drawableIds = listOf(R.drawable.ant, R.drawable.bee, R.drawable.beetle, R.drawable.grasshopper, R.drawable.spider)
        val landscape = resources.configuration.orientation == ORIENTATION_LANDSCAPE
        if (landscape) {
            addPiecesLandscape(screenHeight, pieceHeight, drawableIds)
            return
        }
        val spaceBetweenImages = screenWidth / 5 // To space the images evenly

        drawableIds.forEachIndexed { index, drawableId ->
            val image = ImageView(this).apply {
                setImageResource(drawableId)
                layoutParams = RelativeLayout.LayoutParams(pieceHeight, pieceHeight).apply {
                    leftMargin = ((index * spaceBetweenImages + 50f)).toInt()
                    topMargin = screenHeight - pieceHeight - 50 // Fixed bottom margin to align the images at the bottom
                }
            }
            binding.root.addView(image)
        }
    }
    private fun addPiecesLandscape(screenHeight: Int, pieceHeight: Int, drawableIds: List<Int>) {
        val spaceBetweenImages = screenHeight / 5
        drawableIds.forEachIndexed { index, drawableId ->
            val image = ImageView(this).apply {
                setImageResource(drawableId)
                layoutParams = ConstraintLayout.LayoutParams(pieceHeight, pieceHeight).apply {
                    // Constrain images to the bottom, spaced evenly
                    topToTop = binding.root.id
                    rightToRight = binding.root.id
                    marginStart = (index * spaceBetweenImages + 50f).toInt()
                    setMargins(0, (index * spaceBetweenImages + 50f).toInt(), 0, 0)
                    rightMargin = 64
                }
            }
            binding.root.addView(image)
        }
    }
    private inner class DragHelperCallback : ViewDragHelper.Callback() {
        private var originalX = 0f
        private var originalY = 0f
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            // Only capture images that are outside the hexagon grid
            if (child.tag == "hex") {
                return false
            }
            return child.tag == "copy" || child is ImageView
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            // Constrain dragging horizontally within the bounds of the root layout
            return left.coerceIn(0, binding.root.width - child.width)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            // Constrain dragging vertically within the bounds of the root layout
            return top.coerceIn(0, binding.root.height - child.height)
        }

        override fun onViewCaptured(capturedChild: View, pointerId: Int) {
            // Save the original position before the drag begins
            originalX = capturedChild.x
            originalY = capturedChild.y
            capturedChild.bringToFront()
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            Log.d(TAG, "onViewReleased: Released child at (${releasedChild.x}, ${releasedChild.y})")

            val closestCell = hexagonGridLayout.findClosestCell(releasedChild)

            if (closestCell != null) {
                if (releasedChild.tag == "copy") {
                    val layoutParams = releasedChild.layoutParams as RelativeLayout.LayoutParams

                    // Update the position using the closest cell's coordinates
                    layoutParams.leftMargin = closestCell.x.toInt()
                    layoutParams.topMargin = closestCell.y.toInt()

                    // Apply the updated layout params to the view
                    releasedChild.layoutParams = layoutParams
                    binding.root.invalidate()
                }
                else {
                    createCopyOfImage(releasedChild, closestCell.x, closestCell.y)
                    dragHelper.settleCapturedViewAt(originalX.toInt(), originalY.toInt())
                }
            }
            //releasedChild.bringToFront()
            // Redraw the layout to reflect position changes

        }
    }

}