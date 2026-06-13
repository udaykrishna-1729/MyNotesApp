package com.example.puzzlegame.logic

/**
 * Represents a coordinate on the grid
 */
data class Point(val x: Int, val y: Int)

/**
 * Represents a playable piece.
 * The shape is defined by a list of relative points.
 * For example, a 2x2 square might be:
 * (0,0), (1,0), (0,1), (1,1)
 */
data class Piece(
    val id: String,
    val shape: List<Point>,
    val colorIndex: Int = 0 // Used to determine its theme/color
) {
    val width: Int = (shape.maxOfOrNull { it.x } ?: 0) + 1
    val height: Int = (shape.maxOfOrNull { it.y } ?: 0) + 1
}

object PieceFactory {
    // Basic shapes
    val singleBlock = Piece("single", listOf(Point(0, 0)))
    val horizontal2 = Piece("h2", listOf(Point(0, 0), Point(1, 0)))
    val vertical2 = Piece("v2", listOf(Point(0, 0), Point(0, 1)))
    val horizontal3 = Piece("h3", listOf(Point(0, 0), Point(1, 0), Point(2, 0)))
    val vertical3 = Piece("v3", listOf(Point(0, 0), Point(0, 1), Point(0, 2)))
    val square2x2 = Piece("sq2", listOf(Point(0, 0), Point(1, 0), Point(0, 1), Point(1, 1)))
    val lShape = Piece("lshape", listOf(Point(0, 0), Point(0, 1), Point(0, 2), Point(1, 2)))
    val lShapeReverse = Piece("lshaper", listOf(Point(1, 0), Point(1, 1), Point(1, 2), Point(0, 2)))
    
    private val allPieces = listOf(
        singleBlock, horizontal2, vertical2, horizontal3, vertical3,
        square2x2, lShape, lShapeReverse
    )

    /**
     * Generates a random set of pieces (usually 3) for the user to play.
     */
    fun generateRandomPieces(count: Int = 3): List<Piece> {
        return List(count) { 
            allPieces.random().copy(
                // Assign a random color index or link it to the level logic later
                colorIndex = (0..4).random() 
            ) 
        }
    }
}
