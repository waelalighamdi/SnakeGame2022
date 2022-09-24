import Constants.BOARD_SIZE
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf

class SnakeGameEngine {
    private val _boardGame: MutableState<BoardGame>
    private val vacancyList: MutableList<Cell>

    init {
        // set the board grid
        val grid = List(BOARD_SIZE) { y ->
            List(BOARD_SIZE) { x ->
                Cell(x = x, y = y)
            }
        }

        // set the default value of the vacancyList - use to monitor the grid availability
        vacancyList = grid.flatten().toMutableList()

        // set the snake on the board
        val snake = Snake(
            head = Cell(x = BOARD_SIZE / 2, y = BOARD_SIZE / 2),
            tail = listOf(
                Cell(x = BOARD_SIZE / 2 - 1, y = BOARD_SIZE / 2),
                Cell(x = BOARD_SIZE / 2 - 2, y = BOARD_SIZE / 2),
                Cell(x = BOARD_SIZE / 2 - 3, y = BOARD_SIZE / 2),
                Cell(x = BOARD_SIZE / 2 - 4, y = BOARD_SIZE / 2)
            ),
            direction = Directions.RIGHT
        )
        // remove snake position from the vacancyList
        setCellAsOccupied(snake.head)
        snake.tail.forEach(::setCellAsOccupied)

        // set the apple on the board randomly
        val apple = vacancyList.random()
        setCellAsOccupied(apple)

        // set the board game
        _boardGame = mutableStateOf(
            BoardGame(grid = grid, snake = snake, apple = apple)
        )
    }

    val boardGame: State<BoardGame>
        get() = _boardGame

    private fun setCellAsOccupied(cell: Cell) {
        vacancyList.remove(cell)
    }

    private fun setCellAsVacant(cell: Cell) {
        vacancyList.add(cell)
    }

    fun moveSnakeEngine(direction: Directions) {
        // obtain the current location of the snake's head before performing the snake move
        val x = _boardGame.value.snake.head.x
        val y = _boardGame.value.snake.head.y

        // snake pre-move actions
        // snake on-move actions
        snakeOnMove(direction, x, y)
        // snake post-move actions
    }

    private fun isHeadCrossedTail(newHead: Cell): Boolean {
        return _boardGame.value.snake.tail.contains(newHead)
    }

    private fun isHeadCrossedBoardBorders(newHead: Cell): Boolean {
        return (newHead.x !in 0 until BOARD_SIZE || newHead.y !in 0 until BOARD_SIZE)
    }

    private fun produceNewSnakeHead(newDirection: Directions, lastDirection: Directions, x: Int, y: Int): Cell? {
        return when {
            (newDirection == Directions.UP && lastDirection != Directions.DOWN) -> Cell(x = x, y = y - 1)
            (newDirection == Directions.DOWN && lastDirection != Directions.UP) -> Cell(x = x, y = y + 1)
            (newDirection == Directions.LEFT && lastDirection != Directions.RIGHT) -> Cell(x = x - 1, y = y)
            (newDirection == Directions.RIGHT && lastDirection != Directions.LEFT) -> Cell(x = x + 1, y = y)
            else -> null
        }
    }

    private fun produceNewSnakeTail(snakeHead: Cell, snakeTail: List<Cell>, newSnakeHead: Cell): List<Cell> {
        val tail = snakeTail.toMutableList()
        if (newSnakeHead != _boardGame.value.apple) {
            setCellAsVacant(snakeTail.last())
            tail.remove(snakeTail.last())
        }
        tail.add(0, snakeHead)
        return tail
    }


    private fun produceNewScore(newHead: Cell, apple: Cell): Int {
        var score = _boardGame.value.score
        if (newHead == apple) score += 1
        return score
    }

    private fun produceNewApple(newHead: Cell, apple: Cell): Cell {
        return if (newHead == apple) vacancyList.random() else apple
    }

    private fun snakeOnMove(direction: Directions, x: Int, y: Int) {
        val newHead = produceNewSnakeHead(
            newDirection = direction,
            lastDirection = _boardGame.value.snake.direction,
            x = x,
            y = y
        ) ?: return

        if (isHeadCrossedTail(newHead = newHead) || isHeadCrossedBoardBorders(newHead = newHead)) {
            _boardGame.value = _boardGame.value.copy(gameOver = true)
            return
        }

        setCellAsOccupied(newHead)

        val newTail = produceNewSnakeTail(
            snakeHead = _boardGame.value.snake.head,
            snakeTail = _boardGame.value.snake.tail,
            newSnakeHead = newHead
        )

        val newScore = produceNewScore(newHead = newHead, apple = _boardGame.value.apple)

        val newApple = produceNewApple(newHead = newHead, apple = _boardGame.value.apple)

        _boardGame.value = _boardGame.value.copy(
            snake = Snake(head = newHead, tail = newTail, direction = direction),
            score = newScore,
            apple = newApple
        )
    }
}