package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.Game
import com.bignerdranch.andriod.hivenet.pieces.HivePiece
import com.bignerdranch.andriod.hivenet.pieces.QueenBee

data class Player(
    val game: Game,
    val unplayedPieces: List<HivePiece>,
    val playedPieces: MutableList<HivePiece> = mutableListOf(),
    val isWhite: Boolean,
    var isTurn: Boolean,
    var queenBee: QueenBee = unplayedPieces[0] as QueenBee
)