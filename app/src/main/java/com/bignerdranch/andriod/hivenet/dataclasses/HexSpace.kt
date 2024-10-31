package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.pieces.HivePiece

data class HexSpace(
    val x: Int,
    val y: Int,
    val board: Board,
    var hivePiece: HivePiece? = null,
    var top: HexSpace? = null,
    var bottom: HexSpace? = null,
    var topLeft: HexSpace? = null,
    var topRight: HexSpace? = null,
    var bottomLeft: HexSpace? = null,
    var bottomRight: HexSpace? = null
) {
    fun getTouchingPieces(): List<HivePiece?> {
        val touchingPieces = mutableListOf<HivePiece?>()
        touchingPieces.add(top?.hivePiece)
        touchingPieces.add(bottom?.hivePiece)
        touchingPieces.add(topLeft?.hivePiece)
        touchingPieces.add(topRight?.hivePiece)
        touchingPieces.add(bottomLeft?.hivePiece)
        touchingPieces.add(bottomRight?.hivePiece)
        return touchingPieces
    }
}