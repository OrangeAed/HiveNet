package com.bignerdranch.andriod.hivenet

import com.bignerdranch.andriod.hivenet.dataclasses.Board
import com.bignerdranch.andriod.hivenet.dataclasses.Player
import com.bignerdranch.andriod.hivenet.pieces.*

class Game {
    val board: Board
    val whitePlayer: Player
    val blackPlayer: Player
    var iswhitePlayerTurn: Boolean

    init {
        whitePlayer = Player(this, createPieces(isWhite = true), isWhite = true, isTurn = true)
        blackPlayer = Player(this, createPieces(isWhite = false), isWhite = false, isTurn = false)
        board = Board(whitePlayer = whitePlayer, blackPlayer = blackPlayer)
        
        iswhitePlayerTurn = true
    }

    private fun createPieces(isWhite: Boolean): List<HivePiece> {
        return listOf(
            QueenBee(isWhite),
            Spider(isWhite), Spider(isWhite),
            Beetle(isWhite), Beetle(isWhite),
            Grasshopper(isWhite), Grasshopper(isWhite), Grasshopper(isWhite),
            Ant(isWhite), Ant(isWhite), Ant(isWhite)
        )
    }


}