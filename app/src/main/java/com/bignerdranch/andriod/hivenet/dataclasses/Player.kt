package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.Game
import com.bignerdranch.andriod.hivenet.pieces.HivePiece

data class Player(
    val game: Game,
    val unplayedPieces: List<HivePiece>,
    val playesPieces: MutableList<HivePiece> = mutableListOf(),
    val isWhite: Boolean,
    var isTurn: Boolean
)