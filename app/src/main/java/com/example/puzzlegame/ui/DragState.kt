package com.example.puzzlegame.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import com.example.puzzlegame.logic.Piece

class DragState {
    var isDragging by mutableStateOf(false)
    var dragPosition by mutableStateOf(Offset.Zero)
    var draggedPiece by mutableStateOf<Piece?>(null)
    
    // The bounds of the board to determine if drop is valid
    var boardBounds: Rect = Rect.Zero
    var cellSize: Float = 0f
    
    fun startDrag(piece: Piece, startPosition: Offset) {
        isDragging = true
        draggedPiece = piece
        dragPosition = startPosition
    }
    
    fun onDrag(dragAmount: Offset) {
        dragPosition += dragAmount
    }
    
    fun stopDrag(): Pair<Int, Int>? {
        isDragging = false
        val piece = draggedPiece ?: return null
        draggedPiece = null
        
        // Calculate which cell we dropped on based on dragPosition
        // dragPosition is roughly the center of the first block of the piece
        if (boardBounds.contains(dragPosition)) {
            val relativeX = dragPosition.x - boardBounds.left
            val relativeY = dragPosition.y - boardBounds.top
            
            val col = (relativeX / cellSize).toInt()
            val row = (relativeY / cellSize).toInt()
            
            return Pair(row, col)
        }
        return null
    }
}
