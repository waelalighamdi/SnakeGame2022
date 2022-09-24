data class BoardGame(
    val grid: List<List<Cell>>,
    val snake: Snake,
    val apple: Cell,
    val score: Int = 0,
    val gameOver: Boolean = false
)
