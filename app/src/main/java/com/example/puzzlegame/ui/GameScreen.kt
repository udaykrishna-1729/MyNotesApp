package com.example.puzzlegame.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.puzzlegame.GameUiState
import com.example.puzzlegame.logic.Piece
import com.example.puzzlegame.ui.theme.draw3DBlock
import com.example.puzzlegame.ui.theme.getColorForBlock

@Composable
fun GameScreen(
    uiState: GameUiState,
    onPiecePlaced: (Piece, Int, Int) -> Unit,
    onRestart: () -> Unit
) {
    val dragState = remember { DragState() }
    
    // Determine background color based on level
    val bgColor = if (uiState.level % 2 == 0) Color(0xFF1E1E1E) else Color(0xFFFAFAFA)
    val textColor = if (uiState.level % 2 == 0) Color.White else Color.Black

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Score: ${uiState.score}", color = textColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("High: ${uiState.highScore}", color = textColor, fontSize = 16.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Level ${uiState.level}", color = textColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Button(onClick = onRestart) {
                        Text("Restart")
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Game Board
            GameBoard(
                grid = uiState.grid,
                level = uiState.level,
                dragState = dragState
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Pieces Tray
            if (!uiState.isGameOver) {
                PiecesTray(
                    pieces = uiState.availablePieces,
                    level = uiState.level,
                    dragState = dragState,
                    onPieceDropped = { piece, dropPosition ->
                        val result = dragState.stopDrag()
                        if (result != null) {
                            onPiecePlaced(piece, result.first, result.second)
                        } else {
                            dragState.isDragging = false
                        }
                    }
                )
            } else {
                Text(
                    text = "GAME OVER",
                    color = Color.Red,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Draw dragged piece overlay
        if (dragState.isDragging && dragState.draggedPiece != null) {
            val piece = dragState.draggedPiece!!
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cellSize = dragState.cellSize
                val startX = dragState.dragPosition.x - (cellSize / 2)
                val startY = dragState.dragPosition.y - (cellSize / 2)
                
                for (p in piece.shape) {
                    val color = getColorForBlock(uiState.level, piece.colorIndex)
                    draw3DBlock(
                        x = startX + p.x * cellSize,
                        y = startY + p.y * cellSize,
                        size = cellSize,
                        baseColor = color
                    )
                }
            }
        }
    }
}

@Composable
fun GameBoard(
    grid: List<List<Int>>,
    level: Int,
    dragState: DragState
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                val boundsInRoot = androidx.compose.ui.geometry.Rect(
                    coordinates.positionInRoot(),
                    androidx.compose.ui.geometry.Size(
                        coordinates.size.width.toFloat(),
                        coordinates.size.height.toFloat()
                    )
                )
                dragState.boardBounds = boundsInRoot
                dragState.cellSize = boundsInRoot.width / grid[0].size
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cols = grid[0].size
            val rows = grid.size
            val cellWidth = size.width / cols
            val cellHeight = size.height / rows

            // Draw grid background
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val colorVal = grid[r][c]
                    val x = c * cellWidth
                    val y = r * cellHeight
                    
                    if (colorVal == 0) {
                        // Empty cell
                        drawRect(
                            color = Color.Gray.copy(alpha = 0.2f),
                            topLeft = Offset(x + 2f, y + 2f),
                            size = androidx.compose.ui.geometry.Size(cellWidth - 4f, cellHeight - 4f)
                        )
                    } else {
                        // Filled block
                        val blockColor = getColorForBlock(level, colorVal - 1)
                        draw3DBlock(x, y, cellWidth, blockColor)
                    }
                }
            }
        }
    }
}

@Composable
fun PiecesTray(
    pieces: List<Piece>,
    level: Int,
    dragState: DragState,
    onPieceDropped: (Piece, Offset) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (piece in pieces) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .pointerInput(piece) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                // Calculate global offset roughly
                                // Compose detectDragGestures gives local offset. We'll use a hacky
                                // but effective method by tracking pointer position relative to root later,
                                // or just using the global position of this box.
                            },
                            onDragEnd = {
                                if (dragState.isDragging && dragState.draggedPiece == piece) {
                                    onPieceDropped(piece, dragState.dragPosition)
                                }
                            },
                            onDragCancel = {
                                dragState.isDragging = false
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                if (!dragState.isDragging) {
                                    dragState.startDrag(piece, change.position) // need absolute position
                                }
                                dragState.onDrag(dragAmount)
                            }
                        )
                    }
            ) {
                // We need to capture global drag correctly. The above pointerInput uses local coordinates.
                // A better approach for Drag and Drop in compose:
                DraggablePieceItem(piece = piece, level = level, dragState = dragState, onPieceDropped = onPieceDropped)
            }
        }
    }
}

@Composable
fun DraggablePieceItem(
    piece: Piece,
    level: Int,
    dragState: DragState,
    onPieceDropped: (Piece, Offset) -> Unit
) {
    var globalPosition by remember { mutableStateOf(Offset.Zero) }
    
    // Hide original piece if it's currently being dragged
    val isBeingDragged = dragState.isDragging && dragState.draggedPiece == piece

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { coordinates ->
                globalPosition = coordinates.positionInRoot()
            }
            .pointerInput(piece) {
                detectDragGestures(
                    onDragStart = { localOffset ->
                        val initialGlobal = globalPosition + localOffset
                        dragState.startDrag(piece, initialGlobal)
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragState.onDrag(dragAmount)
                    },
                    onDragEnd = {
                        onPieceDropped(piece, dragState.dragPosition)
                    },
                    onDragCancel = {
                        dragState.isDragging = false
                    }
                )
            }
    ) {
        if (!isBeingDragged) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Calculate ideal cell size to fit the piece in 100.dp tray
                val maxDim = maxOf(piece.width, piece.height)
                val cellSize = size.width / 4f // arbitrary scaling
                
                // Center it
                val offsetX = (size.width - piece.width * cellSize) / 2
                val offsetY = (size.height - piece.height * cellSize) / 2

                for (p in piece.shape) {
                    val color = getColorForBlock(level, piece.colorIndex)
                    draw3DBlock(
                        x = offsetX + p.x * cellSize,
                        y = offsetY + p.y * cellSize,
                        size = cellSize,
                        baseColor = color
                    )
                }
            }
        }
    }
}
