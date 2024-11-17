package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.pieces.HivePiece

data class Board(
    val whitePlayer: Player,
    val blackPlayer: Player
) {
    var centerHexSpace: HexSpace? = null
    val board: Array<Array<HexSpace?>>

    init {
        board = arrayOf(
            arrayOfNulls(5),
            arrayOfNulls(6),
            arrayOfNulls(7),
            arrayOfNulls(8),
            arrayOfNulls(9),
            arrayOfNulls(8),
            arrayOfNulls(7),
            arrayOfNulls(6),
            arrayOfNulls(5)
        )
    }

    fun initializeBoard() {
        val centerRow = 4
        val centerCol = 4

        // Initialize center hex space
        centerHexSpace = HexSpace(centerRow, centerCol, this)
        board[centerRow][centerCol] = centerHexSpace

        // Create hex spaces in the ragged array
        for (row in board.indices) {
            for (col in board[row].indices) {
                if (row == centerRow && col == centerCol) continue // Skip the center hex space
                board[row][col] = HexSpace(row, col, this)
            }
        }

        // Connect hex spaces
        for (row in board.indices) {
            for (col in board[row].indices) {
                if (row < centerRow) {
                    val hexSpace = board[row][col] ?: continue
                    hexSpace.top = getHexSpace(row - 1, col)
                    hexSpace.bottom = getHexSpace(row + 1, col)
                    hexSpace.topLeft = getHexSpace(row - 1, col - 1)
                    hexSpace.topRight = getHexSpace(row - 1, col + 1)
                    hexSpace.bottomLeft = getHexSpace(row + 1, col - 1)
                    hexSpace.bottomRight = getHexSpace(row + 1, col + 1)
                }

            }
        }
    }

    private fun getHexSpace(row: Int, col: Int): HexSpace? {
        return if (row in board.indices && col in board[row].indices) {
            board[row][col]
        } else {
            null
        }
    }

//    fun isHiveIntactAfterMove(piece: HivePiece, destination: HexSpace): Boolean {
//        val originalHexSpace = piece.currentHexSpace
//        originalHexSpace?.hivePiece = null
//        destination.hivePiece = piece
//
//        val allPieces = getAllPieces()
//        val visited = mutableSetOf<HivePiece>()
//        val startPiece = allPieces.firstOrNull() ?: return true
//
//        dfs(startPiece, visited)
//
//        originalHexSpace?.hivePiece = piece
//        destination.hivePiece = null
//
//        return visited.size == allPieces.size
//    }

    private fun dfs(piece: HivePiece, visited: MutableSet<HivePiece>) {
        visited.add(piece)
        for (neighbor in piece.currentHexSpace?.getAdjacentSpaces() ?: emptyList()) {
            if (neighbor?.hivePiece != null && neighbor.hivePiece !in visited) {
                dfs(neighbor.hivePiece!!, visited)
            }
        }
    }

//    private fun getAllPieces(): List<HivePiece> {
//        val pieces = mutableListOf<HivePiece>()
//        val visited = mutableSetOf<HexSpace>()
//        centerHexSpace?.let { collectPieces(it, pieces, visited) }
//        return pieces
//    }
//
//    private fun collectPieces(hexSpace: HexSpace, pieces: MutableList<HivePiece>, visited: MutableSet<HexSpace>) {
//        if (hexSpace in visited) return
//        visited.add(hexSpace)
//        hexSpace.hivePiece?.let { pieces.add(it) }
//        for (neighbor in hexSpace.getAdjacentSpaces()) {
//            collectPieces(neighbor, pieces, visited)
//        }
//    }
}