package com.mduranx64.kotlingames

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
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
import androidx.compose.runtime.saveable.rememberSaveable
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.Serializable

enum class BoardTheme: Serializable {
    Black,
    Brown
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChessGame(navController: NavHostController) {
    var showMenuAlert by rememberSaveable { mutableStateOf(false) }
    var currentTheme by rememberSaveable { mutableStateOf(BoardTheme.Black) }
    var showCustomAlert by rememberSaveable { mutableStateOf(false) }
    val chessViewModel: ChessViewModel = viewModel()

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
                navController = navController,
                viewModel = chessViewModel,
                boardTheme = currentTheme,
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
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
            onAccept = {
                showCustomAlert = false
                navController.popBackStack()
            }
        )
    }
}

@Composable
fun ChessBoardView(navController: NavHostController, viewModel: ChessViewModel = viewModel(), boardTheme: BoardTheme, modifier: Modifier) {

    val configuration = LocalConfiguration.current

    val topPadding = WindowInsets.systemBars.getTop(LocalDensity.current)
    val bottomPadding = WindowInsets.systemBars.getBottom(LocalDensity.current)

    val squares = 8
    val fullScreenSize = configuration.screenWidthDp.coerceAtMost(configuration.screenHeightDp)
    val gridSize = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) fullScreenSize.dp else (fullScreenSize - (topPadding + bottomPadding)).dp
    val squareSize = gridSize / squares

    var showWinAlert: Boolean by rememberSaveable { mutableStateOf(false) }
    var showPawnAlert: Boolean by rememberSaveable { mutableStateOf(false) }

    val letters = listOf("a", "b", "c", "d", "e", "f", "g", "h")
    val numbers = listOf("8", "7", "6", "5", "4", "3", "2", "1")
    val captureSize = squareSize / 2

    if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
        Column(
            modifier = modifier,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Top Captured pieces
            LazyVerticalGrid(
                columns = GridCells.Fixed(squares * 2),
                modifier = Modifier.size(width = gridSize, height = captureSize)
            ) {
                itemsIndexed(viewModel.board.whiteCapture) { _, piece ->
                    Image(
                        painter = painterResource(piece.pieceImage),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(squareSize / 2)
                    )
                }
            }

            Column(
                modifier = Modifier.alpha(if (viewModel.board.currentTurn == PieceColor.BLACK) 1f else 0f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Game Controller",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Black moves",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Board and Pieces
            Box(
                modifier = Modifier
                    .size(gridSize)
                    .background(Color.DarkGray)
            ) {
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
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(squares)
                ) {
                    items(viewModel.board.pieces.size) { x ->
                        val row = viewModel.board.pieces[x]
                        Row {
                            row.forEachIndexed { y, piece ->
                                val pieceImage = row[y]?.pieceImage ?: R.drawable.empty
                                val position = Position(x, y)
                                val isLight = (x + y) % 2 == 0

                                val name = piece?.type?.name ?: "empty"
                                val color = piece?.color?.name
                                    ?: if (isLight) "white square" else "black square"
                                val numberGuide = numbers[x]
                                val letterGuide = letters[y]

                                val identifier = "$color $name $letterGuide$numberGuide"
                                Box(
                                    modifier = Modifier
                                        .size(squareSize)
                                        .background(Color.Transparent)
                                        .border(
                                            2.dp,
                                            if (viewModel.board.isSelected(position)) Color.Yellow else Color.Transparent
                                        )
                                        .clickable {
                                            viewModel.board.selectPiece(position)
                                            if (viewModel.board.isBlackKingCaptured || viewModel.board.isWhiteKingCaptured) {
                                                showWinAlert = true
                                            }
                                            if (viewModel.board.isPiecePromoted) {
                                                showPawnAlert = true
                                            }
                                        }
                                        .semantics {
                                            contentDescription =
                                                if (piece == null) identifier else ""
                                        }
                                ) {
                                    piece?.let {
                                        Image(
                                            painter = painterResource(pieceImage),
                                            contentDescription = identifier,
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
                modifier = Modifier.alpha(if (viewModel.board.currentTurn == PieceColor.WHITE) 1f else 0f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "White moves",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            // Bottom captured pieces
            LazyVerticalGrid(
                columns = GridCells.Fixed(squares * 2),
                modifier = Modifier.size(width = gridSize, height = captureSize)
            ) {
                itemsIndexed(viewModel.board.blackCapture) { _, piece ->
                    Image(
                        painter = painterResource(piece.pieceImage),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(squareSize / 2)
                    )
                }
            }
        }
    } else {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Top Captured pieces
            LazyHorizontalGrid(
                rows = GridCells.Fixed(squares * 2),
                modifier = Modifier.size(width = captureSize, height = gridSize)
            ) {
                itemsIndexed(viewModel.board.whiteCapture) { _, piece ->
                    Image(
                        painter = painterResource(piece.pieceImage),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(squareSize / 2)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.alpha(if (viewModel.board.currentTurn == PieceColor.BLACK) 1f else 0f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Game Controller",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "Black moves",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Board and Pieces
            Box(
                modifier = Modifier
                    .size(gridSize)
                    .background(Color.DarkGray)
            ) {
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
                LazyHorizontalGrid(
                    rows = GridCells.Fixed(squares)
                ) {
                    items(viewModel.board.pieces.size) { x ->
                        val row = viewModel.board.pieces[x]
                        Row {
                            row.forEachIndexed { y, piece ->
                                val pieceImage = row[y]?.pieceImage ?: R.drawable.empty
                                val position = Position(x, y)
                                val isLight = (x + y) % 2 == 0

                                val name = piece?.type?.name ?: "empty"
                                val color = piece?.color?.name
                                    ?: if (isLight) "white square" else "black square"
                                val numberGuide = numbers[x]
                                val letterGuide = letters[y]

                                val identifier = "$color $name $letterGuide$numberGuide"
                                Box(
                                    modifier = Modifier
                                        .size(squareSize)
                                        .background(Color.Transparent)
                                        .border(
                                            2.dp,
                                            if (viewModel.board.isSelected(position)) Color.Yellow else Color.Transparent
                                        )
                                        .clickable {
                                            viewModel.board.selectPiece(position)
                                            if (viewModel.board.isBlackKingCaptured || viewModel.board.isWhiteKingCaptured) {
                                                showWinAlert = true
                                            }
                                            if (viewModel.board.isPiecePromoted) {
                                                showPawnAlert = true
                                            }
                                        }
                                        .semantics {
                                            contentDescription =
                                                if (piece == null) identifier else ""
                                        }
                                ) {
                                    piece?.let {
                                        Image(
                                            painter = painterResource(pieceImage),
                                            contentDescription = identifier,
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

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.alpha(if (viewModel.board.currentTurn == PieceColor.WHITE) 1f else 0f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Play",
                    tint = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "White moves",
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Bottom captured pieces
            LazyHorizontalGrid(
                rows = GridCells.Fixed(squares * 2),
                modifier = Modifier.size(width = captureSize, height = gridSize)
            ) {
                itemsIndexed(viewModel.board.blackCapture) { _, piece ->
                    Image(
                        painter = painterResource(piece.pieceImage),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(squareSize / 2)
                    )
                }
            }
        }
    }

    // Alerts (for example, when the game is won or a pawn promotion)
    if (showWinAlert) {
        val color = if (viewModel.board.isWhiteKingCaptured) PieceColor.WHITE else PieceColor.BLACK
        WinAlert(
            capturedColor = color,
            onDismiss = {
                showWinAlert = false
                navController.popBackStack()
            }
        )
    }

    if (showPawnAlert) {
        PawnPromotionDialog(
            onDismiss = { showPawnAlert = false },
            board = viewModel.board
        )
    }
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
        onDismissRequest = { },
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