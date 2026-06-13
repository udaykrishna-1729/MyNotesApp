package com.example.puzzlegame.logic

/**
 * Handles the core logic of the puzzle game.
 * The board is a 2D array, typically 10x10.
 * A value of 0 means empty. Any value > 0 represents a filled block (color index).
 */
class GameEngine(
    val rows: Int = 10,
    val cols: Int = 10
) {
    // Current state of the grid
    var grid: Array<IntArray> = Array(rows) { IntArray(cols) { 0 } }
        private set

    /**
     * Restore grid state from a saved flat list.
     */
    fun restoreGrid(flatGrid: List<Int>) {
        if (flatGrid.size == rows * cols) {
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    grid[r][c] = flatGrid[r * cols + c]
                }
            }
        }
    }

    /**
     * Get grid as flat list for saving to DB.
     */
    fun getFlatGrid(): List<Int> {
        return grid.flatMap { it.toList() }
    }

    /**
     * Checks if a piece can be placed at the given row/col.
     */
    fun canPlacePiece(piece: Piece, startRow: Int, startCol: Int): Boolean {
        for (point in piece.shape) {
            val r = startRow + point.y
            val c = startCol + point.x

            // Check boundaries
            if (r < 0 || r >= rows || c < 0 || c >= cols) return false

            // Check if cell is already occupied
            if (grid[r][c] != 0) return false
        }
        return true
    }

    /**
     * Places the piece on the grid.
     * Assumes canPlacePiece has already been called and returned true.
     */
    fun placePiece(piece: Piece, startRow: Int, startCol: Int) {
        val colorVal = piece.colorIndex + 1 // Offset by 1 so 0 is empty
        for (point in piece.shape) {
            val r = startRow + point.y
            val c = startCol + point.x
            grid[r][c] = colorVal
        }
    }

    /**
     * Checks for completed rows and columns, clears them, and returns the score earned.
     */
    fun clearCompletedLines(): Int {
        val rowsToClear = mutableListOf<Int>()
        val colsToClear = mutableListOf<Int>()

        // Check rows
        for (r in 0 until rows) {
            if (grid[r].all { it != 0 }) {
                rowsToClear.add(r)
            }
        }

        // Check columns
        for (c in 0 until cols) {
            var colFull = true
            for (r in 0 until rows) {
                if (grid[r][c] == 0) {
                    colFull = false
                    break
                }
            }
            if (colFull) colsToClear.add(c)
        }

        // Calculate score
        val linesCleared = rowsToClear.size + colsToClear.size
        // Simple scoring: 10 points per line. Combo for multiple lines.
        val score = linesCleared * 10 * linesCleared 

        // Clear the lines
        for (r in rowsToClear) {
            for (c in 0 until cols) {
                grid[r][c] = 0
            }
        }
        for (c in colsToClear) {
            for (r in 0 until rows) {
                grid[r][c] = 0
            }
        }

        return score
    }

    /**
     * Checks if any of the available pieces can be placed anywhere on the board.
     * Returns true if there's no valid move left (Game Over).
     */
    fun isGameOver(availablePieces: List<Piece>): Boolean {
        if (availablePieces.isEmpty()) return false // If empty, we need to generate more pieces, not game over

        for (piece in availablePieces) {
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (canPlacePiece(piece, r, c)) {
                        return false // Found at least one valid move
                    }
                }
            }
        }
        return true // No piece can be placed anywhere
    }
    
    /**
     * Resets the grid for a new game.
     */
    fun resetGrid() {
        grid = Array(rows) { IntArray(cols) { 0 } }
    }
}
