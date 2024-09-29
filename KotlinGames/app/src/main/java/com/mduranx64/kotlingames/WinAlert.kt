package com.mduranx64.kotlingames

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mduranx64.kotlingames.ui.theme.KotlinGamesTheme

@Composable
fun WinAlert(capturedColor: PieceColor, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
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
                val imageResId = if (capturedColor == PieceColor.WHITE) R.drawable.b_king else R.drawable.w_king
                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = null,
                    modifier = Modifier.size(80.dp),  // Size of the image
                    contentScale = ContentScale.Fit
                )
                Spacer(modifier = Modifier.height(16.dp)) // Add some spacing between image and text
                // Show the winning message
                Text(
                    text = if (capturedColor == PieceColor.WHITE) "Black wins!" else "White wins!",
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

@Preview(showBackground = true)
@Composable
fun PreviewWinAlertLight() {
    KotlinGamesTheme {
        WinAlert(capturedColor = PieceColor.WHITE) { }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewWinAlertDark() {
    KotlinGamesTheme(darkTheme = true) {
        WinAlert(capturedColor = PieceColor.BLACK) { }
    }
}