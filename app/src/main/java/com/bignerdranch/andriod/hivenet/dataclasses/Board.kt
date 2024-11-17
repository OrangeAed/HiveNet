package com.bignerdranch.andriod.hivenet.dataclasses

import com.bignerdranch.andriod.hivenet.pieces.HivePiece

data class Board(
    val whitePlayer: Player,
    val blackPlayer: Player
) {
    var centerHexSpace: HexSpace? = null

    fun initializeBoard(radius: Int) {
        val hexMap = mutableMapOf<Pair<Int, Int>, HexSpace>()

        // Initialize center hex space
        centerHexSpace = HexSpace(0, 0, this)
        hexMap[0 to 0] = centerHexSpace!!

        // Create hex spaces in a hexagonal grid
        for (q in -radius..radius) {
            for (r in maxOf(-radius, -q-radius)..minOf(radius, -q+radius)) {
                if (q == 0 && r == 0) continue // Skip the center hex space
                val hexSpace = HexSpace(q, r, this)
                hexMap[q to r] = hexSpace
            }
        }

        // Connect hex spaces
        for ((coords, hexSpace) in hexMap) {
            val (q, r) = coords
            hexSpace.top = hexMap[q to r-1]
            hexSpace.bottom = hexMap[q to r+1]
            hexSpace.topLeft = hexMap[q-1 to r]
            hexSpace.topRight = hexMap[q+1 to r-1]
            hexSpace.bottomLeft = hexMap[q-1 to r+1]
            hexSpace.bottomRight = hexMap[q+1 to r]
        }
    }

    fun isHiveIntactAfterMove(piece: HivePiece, destination: HexSpace): Boolean {
        val originalHexSpace = piece.currentHexSpace
        originalHexSpace?.hivePiece = null
        destination.hivePiece = piece

        val allPieces = getAllPieces()
        val visited = mutableSetOf<HivePiece>()
        val startPiece = allPieces.firstOrNull() ?: return true

        dfs(startPiece, visited)

        originalHexSpace?.hivePiece = piece
        destination.hivePiece = null

        return visited.size == allPieces.size
    }

    private fun dfs(piece: HivePiece, visited: MutableSet<HivePiece>) {
        visited.add(piece)
        for (neighbor in piece.currentHexSpace?.getAdjacentSpaces() ?: emptyList()) {
            if (neighbor?.hivePiece != null && neighbor.hivePiece !in visited) {
                dfs(neighbor.hivePiece!!, visited)
            }
        }
    }

    private fun getAllPieces(): List<HivePiece> {
        val pieces = mutableListOf<HivePiece>()
        val visited = mutableSetOf<HexSpace>()
        centerHexSpace?.let { collectPieces(it, pieces, visited) }
        return pieces
    }

    private fun collectPieces(hexSpace: HexSpace, pieces: MutableList<HivePiece>, visited: MutableSet<HexSpace>) {
        if (hexSpace in visited) return
        visited.add(hexSpace)
        hexSpace.hivePiece?.let { pieces.add(it) }
        for (neighbor in hexSpace.getTouchingPieces()) {
            if (neighbor != null) {
                neighbor.currentHexSpace?.let { collectPieces(it, pieces, visited) }
            }
        }
    }
}