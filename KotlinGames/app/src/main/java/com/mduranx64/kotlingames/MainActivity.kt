package com.mduranx64.kotlingames

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mduranx64.kotlingames.ui.theme.KotlinGamesTheme

enum class GameType {
    Chess,
    ComingSoon
}

// Data class representing a game item (Equivalent to Swift's struct GameItem)
data class GameItem(
    val title: String,
    val imageResId: Int,
    val game: GameType
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            KotlinGamesTheme {
                val navController = rememberNavController()
                Navigation(navController)
            }
        }
    }
}

@Composable
fun Navigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "Main") {
        composable("Main") { MainView(navController) }
        composable("Chess") { ChessGame(navController) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainView(navController: NavHostController) {
    val gameList = listOf(
        GameItem(title = "Chess", imageResId = R.drawable.chess_cover, game = GameType.Chess)
    )
    var showInfoAlert by rememberSaveable { mutableStateOf(false) }
    val columns = if (LocalConfiguration.current.screenWidthDp < LocalConfiguration.current.screenHeightDp) 2 else 4

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Kotlin Games",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.titleLarge
                    )
                        },
                actions = {
                    IconButton(onClick = {
                        showInfoAlert = true
                    }) {
                        Icon(Icons.Default.Info, contentDescription = "Info", tint = MaterialTheme.colorScheme.primary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp), // Space between rows
                    horizontalArrangement = Arrangement.spacedBy(16.dp) // Space between columns
                ) {
                    items(gameList.size) { index ->
                        val item = gameList[index]

                        GameView(title = item.title, imageResId = item.imageResId) {
                            if (item.game != GameType.ComingSoon) {
                                navController.navigate(item.title)
                            }
                        }
                    }
                }
            }

            if (showInfoAlert) {
                InfoAlert(onDismiss = { showInfoAlert = false })
            }
        }
    )
}

@Composable
fun InfoAlert(onDismiss: () -> Unit) {
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo?.versionName
    val versionCode = packageInfo?.longVersionCode

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kotlin Games Info", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Add the image (replace R.drawable.kotlin_logo with your actual image resource)
                Image(
                    painter = painterResource(id = R.drawable.dev), // Use an actual image resource
                    contentDescription = "face",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(8.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("An open-source collection of games made in Kotlin!",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text("Version: $versionName ($versionCode)", style = MaterialTheme.typography.bodyLarge)

                Spacer(modifier = Modifier.height(16.dp))

                // Clickable text using Modifier.clickable
                Text(
                    text = "Visit the repository on GitHub",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.clickable {
                        // Open the link in a browser
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/mduranx64/kotlin-games"))
                        context.startActivity(intent)
                    }
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
fun GameView(
    title: String,
    imageResId: Int, // Use a resource ID for the image
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background, shape = RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .border(
                1.dp,
                MaterialTheme.colorScheme.onBackground,
                RoundedCornerShape(10.dp)
            ) // Border
            .padding(16.dp)
            .clickable(onClick = onClick)
    ) {
        // Game image
        Image(
            painter = painterResource(id = imageResId),
            contentDescription = title,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(619f / 493f) // Aspect ratio
                .clip(RoundedCornerShape(10.dp))
        )

        // Game title
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(top = 16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainLight() {
    KotlinGamesTheme(darkTheme = false) {
        val navController = rememberNavController()
        MainView(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMainDark() {
    KotlinGamesTheme(darkTheme = true) {
        val navController = rememberNavController()
        MainView(navController = navController)
    }
}

@Preview(showBackground = true)
@Composable
fun GameViewPreviewLight() {
    KotlinGamesTheme(darkTheme = false) {
        GameView(
            title = "Chess",
            imageResId = R.drawable.chess_cover,
            onClick = {}// Use a placeholder or actual resource
        )
    }
}

@Preview(showBackground = true)
@Composable
fun GameViewPreviewDark() {
    KotlinGamesTheme(darkTheme = true) {
        GameView(
            title = "Chess",
            imageResId = R.drawable.chess_cover,
            onClick = {}// Use a placeholder or actual resource
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInfoLight() {
    KotlinGamesTheme(darkTheme = false) {
        InfoAlert(onDismiss = {})
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewInfoDark() {
    KotlinGamesTheme(darkTheme = true) {
        InfoAlert(onDismiss = {})
    }
}