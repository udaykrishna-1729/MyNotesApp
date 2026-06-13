package com.example.puzzlegame.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill

// Basic Level Palettes. As level increases, we use modulo to loop through palettes.
val levelPalettes = listOf(
    // Level 1: Bright & vibrant
    listOf(Color(0xFFE57373), Color(0xFF64B5F6), Color(0xFF81C784), Color(0xFFFFD54F), Color(0xFFBA68C8)),
    // Level 2: Neon dark
    listOf(Color(0xFFFF007F), Color(0xFF00E5FF), Color(0xFF39FF14), Color(0xFFFDFE00), Color(0xFFB026FF)),
    // Level 3: Pastel
    listOf(Color(0xFFFFB3BA), Color(0xFFBAE1FF), Color(0xFFB3E2CD), Color(0xFFFFFFBA), Color(0xFFF4C2C2)),
    // Level 4: Monotone Blues
    listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9), Color(0xFF42A5F5), Color(0xFF1E88E5), Color(0xFF0D47A1))
)

fun getColorForBlock(level: Int, colorIndex: Int): Color {
    val paletteIndex = ((level - 1).coerceAtLeast(0)) % levelPalettes.size
    val palette = levelPalettes[paletteIndex]
    return palette[colorIndex % palette.size]
}

/**
 * Draws a 3D looking block by drawing borders with lighter/darker shades.
 */
fun DrawScope.draw3DBlock(
    x: Float,
    y: Float,
    size: Float,
    baseColor: Color,
    padding: Float = 2f
) {
    val actualSize = size - padding * 2
    val startX = x + padding
    val startY = y + padding

    // Base color
    drawRect(
        color = baseColor,
        topLeft = Offset(startX, startY),
        size = Size(actualSize, actualSize),
        style = Fill
    )

    // Highlight (Top and Left edges)
    val highlightColor = baseColor.copy(alpha = 0.5f) // or a mix with white
    drawRect(
        color = Color.White.copy(alpha = 0.3f),
        topLeft = Offset(startX, startY),
        size = Size(actualSize, actualSize * 0.15f) // Top highlight
    )
    drawRect(
        color = Color.White.copy(alpha = 0.3f),
        topLeft = Offset(startX, startY),
        size = Size(actualSize * 0.15f, actualSize) // Left highlight
    )

    // Shadow (Bottom and Right edges)
    drawRect(
        color = Color.Black.copy(alpha = 0.3f),
        topLeft = Offset(startX, startY + actualSize * 0.85f),
        size = Size(actualSize, actualSize * 0.15f) // Bottom shadow
    )
    drawRect(
        color = Color.Black.copy(alpha = 0.3f),
        topLeft = Offset(startX + actualSize * 0.85f, startY),
        size = Size(actualSize * 0.15f, actualSize) // Right shadow
    )
}
