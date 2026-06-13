package com.example.puzzlegame

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.puzzlegame.data.GameDatabase
import com.example.puzzlegame.data.GameStateEntity
import com.example.puzzlegame.logic.GameEngine
import com.example.puzzlegame.logic.Piece
import com.example.puzzlegame.logic.PieceFactory
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class GameUiState(
    val score: Int = 0,
    val highScore: Int = 0,
    val level: Int = 1,
    val grid: List<List<Int>> = List(10) { List(10) { 0 } },
    val availablePieces: List<Piece> = emptyList(),
    val isGameOver: Boolean = false
)

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = GameDatabase.getDatabase(application).gameDao()
    private val engine = GameEngine(10, 10)
    private val gson = Gson()

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    init {
        loadGameState()
    }

    private fun loadGameState() {
        viewModelScope.launch {
            val savedState = dao.getGameState()
            if (savedState != null) {
                // Restore from DB
                val gridListType = object : TypeToken<List<Int>>() {}.type
                val flatGrid: List<Int> = gson.fromJson(savedState.gridJson, gridListType)
                engine.restoreGrid(flatGrid)

                val pieceListType = object : TypeToken<List<Piece>>() {}.type
                val pieces: List<Piece> = gson.fromJson(savedState.availablePiecesJson, pieceListType)
                
                // Safety check: if pieces were empty, generate new ones
                val finalPieces = if (pieces.isEmpty()) PieceFactory.generateRandomPieces() else pieces

                _uiState.value = GameUiState(
                    score = savedState.currentScore,
                    highScore = savedState.highScore,
                    level = savedState.currentLevel,
                    grid = engine.grid.map { it.toList() },
                    availablePieces = finalPieces,
                    isGameOver = engine.isGameOver(finalPieces)
                )
            } else {
                // New game state
                startNewGame()
            }
        }
    }

    private fun startNewGame(keepHighScoreAndLevel: Boolean = false) {
        engine.resetGrid()
        val newPieces = PieceFactory.generateRandomPieces()
        
        val currentState = _uiState.value
        
        _uiState.value = GameUiState(
            score = 0,
            highScore = if (keepHighScoreAndLevel) currentState.highScore else 0,
            level = if (keepHighScoreAndLevel) currentState.level else 1,
            grid = engine.grid.map { it.toList() },
            availablePieces = newPieces,
            isGameOver = false
        )
        saveGameState()
    }

    fun onPiecePlaced(piece: Piece, startRow: Int, startCol: Int) {
        if (_uiState.value.isGameOver) return

        if (engine.canPlacePiece(piece, startRow, startCol)) {
            // Place piece
            engine.placePiece(piece, startRow, startCol)
            
            // Clear lines and get score
            val points = engine.clearCompletedLines()
            // Add points for placing the piece itself (e.g. piece size)
            val placePoints = piece.shape.size
            val currentScore = _uiState.value.score + points + placePoints
            
            // Remove the played piece
            val remainingPieces = _uiState.value.availablePieces.filter { it.id != piece.id }
            
            // If all pieces played, generate new ones
            val finalPieces = if (remainingPieces.isEmpty()) {
                PieceFactory.generateRandomPieces()
            } else {
                remainingPieces
            }

            // Check game over
            val isOver = engine.isGameOver(finalPieces)
            
            // Level up logic: if Game Over and Score > High Score -> Next Level
            var newHighScore = _uiState.value.highScore
            var newLevel = _uiState.value.level
            
            if (currentScore > newHighScore) {
                newHighScore = currentScore
            }

            if (isOver && currentScore >= _uiState.value.highScore && currentScore > 0) {
                // Advance Level
                newLevel++
            }

            _uiState.value = _uiState.value.copy(
                score = currentScore,
                highScore = newHighScore,
                level = newLevel,
                grid = engine.grid.map { it.toList() },
                availablePieces = finalPieces,
                isGameOver = isOver
            )
            
            saveGameState()
        }
    }

    fun restartGame() {
        startNewGame(keepHighScoreAndLevel = true)
    }

    private fun saveGameState() {
        val state = _uiState.value
        viewModelScope.launch {
            val entity = GameStateEntity(
                currentScore = state.score,
                highScore = state.highScore,
                currentLevel = state.level,
                gridJson = gson.toJson(engine.getFlatGrid()),
                availablePiecesJson = gson.toJson(state.availablePieces)
            )
            dao.saveGameState(entity)
        }
    }
}
