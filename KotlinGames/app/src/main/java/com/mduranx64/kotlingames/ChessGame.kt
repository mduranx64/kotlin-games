package com.mduranx64.kotlingames

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.mduranx64.kotlingames.ui.theme.KotlinGamesTheme

enum class BoardTheme {
    Black,
    Brown
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessGame(navController: NavHostController) {
    var showMenuAlert by remember { mutableStateOf(false) }
    var currentTheme by remember { mutableStateOf(BoardTheme.Black) }
    var showCustomAlert: Boolean by remember { mutableStateOf(false) }
    val board by remember { mutableStateOf(Board()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Chess",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                navigationIcon = {
                    // Left button (Menu or Back button)
                    IconButton(onClick = {
                        // handle
                        showCustomAlert = true
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Back", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                actions = {
                    // Right button (Settings or any action)
                    IconButton(onClick = {
                        showMenuAlert = true
                    }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        content = { paddingValues ->
            ChessBoardView(
                board = remember { board }, // Pass a Board object here
                boardTheme = currentTheme,
                modifier = Modifier
                    .padding(paddingValues) // Respect the Scaffold padding for the content
                    .fillMaxSize()
                    .background(Color.Gray)
            )
        }
    )

    if (showMenuAlert) {
        MenuAlert(
            onDismiss = { showMenuAlert = false },
            theme = currentTheme,
            onThemeChange = { newTheme -> currentTheme = newTheme }
        )
    }

    if (showCustomAlert) {
        CustomExitAlert(
            onDismiss = { showCustomAlert = false },
            onAccept = { navController.popBackStack() }
        )
    }
}

@Composable
fun ChessBoardView(board: Board, boardTheme: BoardTheme, modifier: Modifier) {
    val squares = 8
    val gridSize = LocalConfiguration.current.screenWidthDp.coerceAtMost(LocalConfiguration.current.screenHeightDp).dp
    val squareSize = gridSize / squares

    var showWinAlert: Boolean by remember { mutableStateOf(false) }
    var showPawnAlert: Boolean by remember { mutableStateOf(false) }

    val letters = listOf("a", "b", "c", "d", "e", "f", "g", "h")
    val numbers = listOf("8", "7", "6", "5", "4", "3", "2", "1")

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        val captureSize = if (board.blackCapture.count() > 8 || board.whiteCapture.count() > 8)  squareSize * 2 else squareSize
        // Top Captured pieces
        LazyVerticalGrid(
            columns = GridCells.Fixed(squares),
            modifier = Modifier.size(width = gridSize, height = captureSize)
        ) {
            itemsIndexed(board.whiteCapture) { _, piece ->
                Image(
                    painter = painterResource(piece.pieceImage),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(squareSize)
                )
            }
        }

        Column(
            modifier = Modifier.alpha(if (board.currentTurn == PieceColor.BLACK) 1f else 0f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Game Controller",
                tint = Color.Black
            )
            Text(
                text = "Black move",
                fontSize = 14.sp,
                color = Color.Black
            )
        }

        // Board and Pieces
        Box (modifier = Modifier.size(width = gridSize, height = gridSize)) {
            // Board
            LazyHorizontalGrid(rows = GridCells.Fixed(squares)) {
                items(64) { index ->
                    val row = index % squares
                    val column = index / squares
                    val isLight = (row + column) % 2 == 0

                    val finalTheme = when (boardTheme) {
                        BoardTheme.Black -> if (isLight) R.drawable.square_gray_light else R.drawable.square_gray_dark
                        BoardTheme.Brown -> if (isLight) R.drawable.square_brown_light else R.drawable.square_brown_dark
                    }
                    val color = if (isLight) Color.Black else Color.White
                    val numberGuide = if (index < numbers.size) numbers[index] else ""
                    val letterGuide = if (row == 7) letters[column] else ""

                    Box(
                        modifier = Modifier
                            .size(squareSize)
                            .padding(0.dp),
                        contentAlignment = Alignment.BottomEnd
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopStart
                        ) {
                            Image(
                                painter = painterResource(id = finalTheme),
                                contentDescription = null,
                                modifier = Modifier.size(squareSize), // Adjust the size of each square
                                contentScale = ContentScale.Fit // Ensures the image covers the entire area
                            )
                            // Top-left number guide
                            Text(
                                text = numberGuide,
                                color = color,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(start = 0.dp, top = 0.dp)
                            )
                        }
                        // Bottom-right letter guide
                        Text(
                            text = letterGuide,
                            color = color,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(end = 0.dp, bottom = 0.dp)
                        )
                    }
                }
            }
            // Pieces
            LazyHorizontalGrid(rows = GridCells.Fixed(squares)) {
                items(board.pieces.size) { x ->
                    val row = board.pieces[x]
                    Row {
                        row.forEachIndexed { y, piece ->
                            val pieceImage = row[y]?.pieceImage ?: R.drawable.empty
                            val position = Position(x, y)
                            Box(
                                modifier = Modifier
                                    .size(squareSize)
                                    .background(Color.Transparent)
                                    .border(
                                        2.dp,
                                        if (board.isSelected(position)) Color.Yellow else Color.Transparent
                                    )
                                    .clickable {
                                        board.selectPiece(position)
                                        if (board.isBlackKingCaptured || board.isWhiteKingCaptured) {
                                            showWinAlert = true
                                        }
                                        if (board.isPiecePromoted) {
                                            showPawnAlert = true
                                        }
                                    }
                            ) {
                                piece?.let {
                                    Image(
                                        painter = painterResource(pieceImage),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier.alpha(if (board.currentTurn == PieceColor.WHITE) 1f else 0f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Game Controller",
                tint = Color.White
            )
            Text(
                text = "White move",
                fontSize = 14.sp,
                color = Color.White
            )
        }

        // Bottom captured pieces
        LazyVerticalGrid(
            columns = GridCells.Fixed(squares),
            modifier = Modifier.size(width = gridSize, height = captureSize)
        ) {
            itemsIndexed(board.blackCapture) { _, piece ->
                Image(
                    painter = painterResource(piece.pieceImage),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .size(squareSize)
                )
            }
        }

        // Alerts (for example, when the game is won or a pawn promotion)
        if (showWinAlert) {
            WinAlert(onDismiss = { showWinAlert = false }, board = board)
        }

        if (showPawnAlert) {
            PawnPromotionDialog(
                onDismiss = { showPawnAlert = false },
                board = board
            )
        }
    }
}

@Composable
fun WinAlert(onDismiss: () -> Unit, board: Board) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Add image of the winning king (either black or white)
                val imageResId = if (board.isWhiteKingCaptured) R.drawable.b_king else R.drawable.w_king

                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),  // Size of the image
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(16.dp)) // Add some spacing between image and text
                // Show the winning message
                Text(
                    text = if (board.isWhiteKingCaptured) "Black wins!" else "White wins!",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Accept")
            }
        }
    )
}

@Composable
fun CustomExitAlert(onDismiss: () -> Unit, onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Confirm Exit", style = MaterialTheme.typography.titleLarge)
        },
        text = {
            Text("Are you sure you want to exit the game?", style = MaterialTheme.typography.bodyLarge)
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("Yes")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("No")
            }
        }
    )
}

@Composable
fun MenuAlert(
    onDismiss: () -> Unit,
    theme: BoardTheme,
    onThemeChange: (BoardTheme) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = "Game settings",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Board theme selection using a Dropdown (like a picker)
                var expanded by remember { mutableStateOf(false) }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { expanded = !expanded }
                        .padding(12.dp)
                ) {
                    Text(
                        text = when (theme) {
                            BoardTheme.Black -> "Black"
                            BoardTheme.Brown -> "Brown"
                        },
                        style = MaterialTheme.typography.bodyLarge,
                    )

                    DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text("Black") },
                            onClick = {
                                onThemeChange(BoardTheme.Black)
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Brown") },
                            onClick = {
                                onThemeChange(BoardTheme.Brown)
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onDismiss() }) {
                Text("Accept")
            }
        }
    )
}

@Composable
fun PawnPromotionDialog(
    onDismiss: () -> Unit,
    board: Board
) {
    var pieceTypeSelected: PieceType by remember { mutableStateOf( PieceType.QUEEN) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = if (board.isWhiteKingCaptured) "Black pawn promotion!" else "White pawn promotion!",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Promotion instruction
                Text(text = "Select a piece type", style = MaterialTheme.typography.bodyLarge)

                // Dropdown (Picker) for selecting piece type
                var expanded by remember { mutableStateOf(false) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { expanded = !expanded }
                        .padding(12.dp)
                ) {
                    Text(
                        text = when (pieceTypeSelected) {
                            PieceType.QUEEN -> "Queen"
                            PieceType.KNIGHT -> "Knight"
                            PieceType.BISHOP -> "Bishop"
                            PieceType.ROOK -> "Rook"
                            PieceType.KING -> ""
                            PieceType.PAWN -> ""
                        },
                        fontSize = 18.sp
                    )
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Queen") },
                            onClick = {
                                pieceTypeSelected = PieceType.QUEEN
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Knight") },
                            onClick = {
                                pieceTypeSelected = PieceType.KNIGHT
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Bishop") },
                            onClick = {
                                pieceTypeSelected = PieceType.BISHOP
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Rook") },
                            onClick = {
                                pieceTypeSelected = PieceType.ROOK
                                expanded = false
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    board.promotePiece(pieceTypeSelected) // Promote the pawn
                    onDismiss() // Close the dialog
                }
            ) {
                Text("Accept")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewLight() {
    KotlinGamesTheme {
        val navController = rememberNavController()
        ChessGame(navController)
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreviewDark() {
    KotlinGamesTheme(darkTheme = true) {
        val navController = rememberNavController()
        ChessGame(navController)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWinAlertLight() {
    KotlinGamesTheme {
        WinAlert(onDismiss = { }, board = Board())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWinAlertDark() {
    KotlinGamesTheme(darkTheme = true) {
        WinAlert(onDismiss = { }, board = Board())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExitAlertLight() {
    KotlinGamesTheme {
        CustomExitAlert(onDismiss = {}, onAccept = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewExitAlertDark() {
    KotlinGamesTheme(darkTheme = true) {
        CustomExitAlert(onDismiss = {}, onAccept = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMenuLight() {
    KotlinGamesTheme(darkTheme = false) {
        MenuAlert(
            onDismiss = {  },
            theme = BoardTheme.Black,
            onThemeChange = {  }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMenuDark() {
    KotlinGamesTheme(darkTheme = true) {
        MenuAlert(
            onDismiss = { },
            theme = BoardTheme.Black,
            onThemeChange = { }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPromotionLight() {
    KotlinGamesTheme(darkTheme = false) {
        PawnPromotionDialog(onDismiss = { }, board = Board())
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPromotionDark() {
    KotlinGamesTheme(darkTheme = true) {
        PawnPromotionDialog(onDismiss = { }, board = Board())
    }
}