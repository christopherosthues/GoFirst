package org.darchacheron.gofirst.settings

import org.darchacheron.gofirst.ui.PlayerColors

/**
 * Data class representing user settings.
 */
data class Settings(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val player1Color: Long = PlayerColors[0].value.toLong(),
    val player2Color: Long = PlayerColors[1].value.toLong(),
    val player3Color: Long = PlayerColors[2].value.toLong(),
    val player4Color: Long = PlayerColors[3].value.toLong(),
    val player5Color: Long = PlayerColors[4].value.toLong(),
    val player6Color: Long = PlayerColors[5].value.toLong(),
    val player7Color: Long = PlayerColors[6].value.toLong(),
    val player8Color: Long = PlayerColors[7].value.toLong(),
    val player9Color: Long = PlayerColors[8].value.toLong(),
    val player10Color: Long = PlayerColors[9].value.toLong(),
)