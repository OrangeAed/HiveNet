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
import androidx.core.view.ViewCompat
import androidx.customview.widget.ViewDragHelper
import coil3.load
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

    /*fun movePiece(startRow: Int, startCol: Int, endRow: Int, endCol: Int) {
        val piece = hexagonGridLayout.getHex(startRow, startCol)?.piece
        val newLocation = hexagonGridLayout.getHex(endRow, endCol)?.image
        if (piece != null && newLocation != null) {
            val layoutParams = piece.layoutParams as RelativeLayout.LayoutParams

            layoutParams.leftMargin = newLocation.x.toInt()
            layoutParams.topMargin = newLocation.y.toInt()

            piece.layoutParams = layoutParams
            binding.root.invalidate()
        }
    }*/


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        hexagonGridLayout = HexagonGridLayout(this).apply {
            layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }
        binding.root.addView(hexagonGridLayout)
        addPieces()
        dragHelper = ViewDragHelper.create(binding.root as ViewGroup, 1.0f, DragHelperCallback())


    }

    override fun onStart() {
        super.onStart()
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

    fun onGameOver() {

    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let { dragHelper.processTouchEvent(it) }
        return super.dispatchTouchEvent(event)
    }

    private fun createCopyOfImage(original: View, x: Float, y: Float) {
        val copy = ImageView(this).apply {
            setImageDrawable((original as ImageView).drawable)
            layoutParams = RelativeLayout.LayoutParams((hexagonGridLayout.hexagonWidth * .8).toInt(), (hexagonGridLayout.hexagonHeight * .8).toInt()).apply {
                topMargin = y.toInt() + (hexagonGridLayout.hexagonWidth * .1).toInt()
                leftMargin = x.toInt() + (hexagonGridLayout.hexagonHeight * .1).toInt()
            }
            tag = "copy"
        }

        binding.root.addView(copy)
    }

    private fun addPieces() {
        val screenHeight = resources.displayMetrics.heightPixels
        val screenWidth = resources.displayMetrics.widthPixels
        val boardHeight = min(screenHeight, screenWidth)
        val pieceHeight = min(450, (boardHeight - 50 * 2 - 40)/5 ).toInt()
        val drawableIds = listOf(R.drawable.ant, R.drawable.bee, R.drawable.beetle, R.drawable.grasshopper, R.drawable.spider)
        val landscape = resources.configuration.orientation == ORIENTATION_LANDSCAPE
        val pieceMargin = screenHeight / 12
        if (landscape) {
            addPiecesLandscape(screenHeight, pieceHeight, drawableIds)
            return
        }
        val spaceBetweenImages = screenWidth / 5

        val antimage = ImageView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(pieceHeight, pieceHeight).apply {
                leftMargin = 0
                topMargin = screenHeight - pieceHeight - pieceMargin
            }
            tag="ant"
        }
        antimage.load("https://live.staticflickr.com/65535/54185775751_d4f032bff7_o.png")
        binding.root.addView(antimage)
        val beeimage = ImageView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(pieceHeight, pieceHeight).apply {
                leftMargin = ((1 * spaceBetweenImages + 50f)).toInt()
                topMargin = screenHeight - pieceHeight - pieceMargin
            }
            tag="bee"
        }
        beeimage.load("https://live.staticflickr.com/65535/54186070154_624f975422_o.png")
        binding.root.addView(beeimage)
        val beetleimage = ImageView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(pieceHeight, pieceHeight).apply {
                leftMargin = ((2 * spaceBetweenImages + 50f)).toInt()
                topMargin = screenHeight - pieceHeight - pieceMargin
            }
            tag="beetle"
        }
        beetleimage.load("https://live.staticflickr.com/65535/54186070149_53971b6722_o.png")
        binding.root.addView(beetleimage)
        val spiderimage = ImageView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(pieceHeight, pieceHeight).apply {
                leftMargin = ((3 * spaceBetweenImages + 50f)).toInt()
                topMargin = screenHeight - pieceHeight - pieceMargin
            }
            tag="spider"
        }
        spiderimage.load("https://live.staticflickr.com/65535/54186070144_b4d5a18628_o.png")
        binding.root.addView(spiderimage)
        val grasshopperimage = ImageView(this).apply {
            layoutParams = RelativeLayout.LayoutParams(pieceHeight, pieceHeight).apply {
                leftMargin = ((4 * spaceBetweenImages + 50f)).toInt()
                topMargin = screenHeight - pieceHeight - pieceMargin
            }
            tag="grasshopper"
        }
        grasshopperimage.load("https://live.staticflickr.com/65535/54184902322_e4f08c8428_o.png")
        binding.root.addView(grasshopperimage)
    }
    private fun addPiecesLandscape(screenHeight: Int, pieceHeight: Int, drawableIds: List<Int>) {
        val spaceBetweenImages = screenHeight / 5
        drawableIds.forEachIndexed { index, drawableId ->
            val image = ImageView(this).apply {
                setImageResource(drawableId)
                layoutParams = ConstraintLayout.LayoutParams(pieceHeight, pieceHeight).apply {
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
        override fun tryCaptureView(child: View, pointerId: Int): Boolean {
            if (child.tag == "hex") {
                return false
            }
            return child.tag == "copy" || child is ImageView
        }

        override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
            return left.coerceIn(0, binding.root.width - child.width)
        }

        override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
            return top.coerceIn(0, binding.root.height - child.height)
        }

        override fun onViewCaptured(capturedChild: View, pointerId: Int) {
            capturedChild.bringToFront()
        }

        override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
            Log.d(TAG, "onViewReleased: Released child at (${releasedChild.x}, ${releasedChild.y})")
            val closestCell = hexagonGridLayout.findClosestCell(releasedChild)

            if (closestCell != null) {
                if (releasedChild.tag == "copy") {
                    val layoutParams = releasedChild.layoutParams as RelativeLayout.LayoutParams

                    layoutParams.leftMargin = closestCell.x.toInt() + (hexagonGridLayout.hexagonWidth * .1).toInt()
                    layoutParams.topMargin = closestCell.y.toInt() + (hexagonGridLayout.hexagonWidth * .1).toInt()

                    releasedChild.layoutParams = layoutParams
                    binding.root.invalidate()
                }
                else {
                    createCopyOfImage(releasedChild, closestCell.x, closestCell.y)
                }
            } else {
                binding.root.requestLayout()
                binding.root.invalidate()
            }
        }
    }
}