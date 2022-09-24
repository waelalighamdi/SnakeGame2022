import Constants.APPLE_COLOR
import Constants.CELL_SIZE
import Constants.SNAKE_HEAD_COLOR
import Constants.SNAKE_TAIL_COLOR
import Constants.VACANT_COLOR
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalComposeUiApi::class)
@Composable
@Preview
fun SnakeGameScreen() {
    val board = SnakeGameEngine()
    val boardState by board.boardGame

    val focusOnBoard = remember { FocusRequester() }

    MaterialTheme {
        LaunchedEffect(Unit) {
            focusOnBoard.requestFocus()
        }

        // capture Key Events
        Box(
            modifier = Modifier
                .fillMaxSize()
                .focusRequester(focusOnBoard)
                .focusable()
                .onKeyEvent { keyEvent ->
                    when {
                        (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.DirectionRight) -> {
                            board.moveSnakeEngine(Directions.RIGHT)
                            true
                        }

                        (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.DirectionLeft) -> {
                            board.moveSnakeEngine(Directions.LEFT)
                            true
                        }

                        (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.DirectionUp) -> {
                            board.moveSnakeEngine(Directions.UP)
                            true
                        }

                        (keyEvent.type == KeyEventType.KeyUp && keyEvent.key == Key.DirectionDown) -> {
                            board.moveSnakeEngine(Directions.DOWN)
                            true
                        }

                        else -> false
                    }
                }
        )

        // compose box to display the game score
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text("SCORE: ${boardState.score}")
        }

        // compose box to draw grid, snake and apple
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column {
                if (!boardState.gameOver) {
                    boardState.grid.forEach { line ->
                        Row {
                            line.forEach { cell ->
                                when (cell) {
                                    boardState.apple -> DrawCellSurface(APPLE_COLOR)
                                    boardState.snake.head -> DrawCellSurface(SNAKE_HEAD_COLOR)
                                    in boardState.snake.tail -> DrawCellSurface(SNAKE_TAIL_COLOR)
                                    else -> DrawCellSurface(VACANT_COLOR)
                                }
                            }
                        }
                    }
                } else {
                    Text("GAME OVER")
                    Text("SCORE ${boardState.score}")
                }
            }
        }
    }
}

@Composable
fun DrawCellSurface(color: Color) {
    Surface(
        modifier = Modifier.size(CELL_SIZE.dp),
        color = color,
        border = BorderStroke(width = 1.dp, color = Color.White),
        content = {}
    )
}
