package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.pieces.HivePiece

data class HexSpace(
    val x: Int,
    val y: Int,
    val board: Board,
    var hivePiece: HivePiece? = null,
    var neighborTopLeft: HexSpace? = null,
    var neighborTopRight: HexSpace? = null,
    var neighborRight: HexSpace? = null,
    var neighborBottomRight: HexSpace? = null,
    var neighborBottomLeft: HexSpace? = null,
    var neighborLeft: HexSpace? = null
)