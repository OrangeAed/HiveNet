package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.pieces.HivePiece

data class HexSpace(
    val row: Int,
    val col: Int,
    val board: Board? = null,
    var hivePiece: HivePiece? = null,
    var top: HexSpace? = null,
    var bottom: HexSpace? = null,
    var topLeft: HexSpace? = null,
    var topRight: HexSpace? = null,
    var bottomLeft: HexSpace? = null,
    var bottomRight: HexSpace? = null,
    var isSelected: Boolean = false,
    var imageView: android.widget.ImageView? = null
) {
    fun getTouchingPieces(): List<HivePiece> {
        return listOfNotNull(
            top?.hivePiece,
            bottom?.hivePiece,
            topLeft?.hivePiece,
            topRight?.hivePiece,
            bottomLeft?.hivePiece,
            bottomRight?.hivePiece
        )
    }

    fun getAdjacentSpaces(): List<HexSpace> {
        return listOfNotNull(
            top,
            bottom,
            topLeft,
            topRight,
            bottomLeft,
            bottomRight
        )
    }
}