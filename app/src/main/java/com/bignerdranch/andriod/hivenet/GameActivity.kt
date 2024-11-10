package com.bignerdranch.andriod.hivenet

import MyReceiver
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.customview.widget.ViewDragHelper
import com.bignerdranch.andriod.hivenet.databinding.ActivityGameBinding

class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private lateinit var dragHelper: ViewDragHelper
    private lateinit var draggableImageView: ImageView
    private var originalX = 0f
    private var originalY = 0f
    private lateinit var originalConstraints: ConstraintSet

    private lateinit var service: ConnectionService
    private var isBound = false
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, binder: IBinder?) {
            val localBinder = binder as ConnectionService.LocalBinder
            service = localBinder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }
    private lateinit var receiver: MyReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        originalX = binding.draggableImageView.x
        originalY = binding.draggableImageView.y
        dragHelper = ViewDragHelper.create(binding.root, 1.0f, object : ViewDragHelper.Callback() {
            override fun tryCaptureView(child: View, pointerId: Int): Boolean {
                return child == binding.draggableImageView
            }

            override fun clampViewPositionHorizontal(child: View, left: Int, dx: Int): Int {
                return left.coerceIn(0, (binding.root.width - child.width))
            }

            override fun clampViewPositionVertical(child: View, top: Int, dy: Int): Int {
                return top.coerceIn(0, (binding.root.height - child.height))
            }

            override fun onViewReleased(releasedChild: View, xvel: Float, yvel: Float) {
                // Get the current position of the released child
                val currentX = releasedChild.x
                val currentY = releasedChild.y

                // Create a copy of the ImageView at the released position
                createCopyOfImage(releasedChild, currentX, currentY)

                // Reset the original ImageView to its original position

                val layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    // Set the ImageView to the bottom of the parent
                    bottomToBottom = binding.ConstraintLayout.id
                    startToStart = binding.ConstraintLayout.id // Left constraint
                    bottomMargin = 16 // 16dp margin from the bottom
                    marginStart = 16 // 16dp margin from the start (left)
                }
                releasedChild.layoutParams = layoutParams
                releasedChild.requestLayout()
            }
        })
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
    override fun onTouchEvent(event: MotionEvent): Boolean {
        dragHelper.processTouchEvent(event)
        return true
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        event?.let { dragHelper.processTouchEvent(it) }
        return super.dispatchTouchEvent(event)
    }

    private fun createCopyOfImage(original: View, x: Float, y: Float) {
        val copy = ImageView(this)
        copy.setImageDrawable((original as ImageView).drawable)
        copy.x = x
        copy.y = y

        // Add the copy to the parent layout
        binding.root.addView(copy)

        // Make the copy unmovable
        copy.isClickable = false
        copy.isFocusable = false
    }
}