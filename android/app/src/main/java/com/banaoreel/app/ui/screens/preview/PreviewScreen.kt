package com.banaoreel.app.ui.screens.preview

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem

@Composable
fun PreviewScreen(
    onGenerateAnother: () -> Unit,
    viewModel: PreviewViewModel = hiltViewModel()
) {
    val job by viewModel.job.collectAsState()
    val context = LocalContext.current

    Scaffold(topBar = { TopAppBar(title = { Text("Your video") }) }) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val videoUrl = job?.videoUrl

            if (videoUrl == null) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else {
                AndroidView(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    factory = {
                        val player = ExoPlayer.Builder(context).build().apply {
                            setMediaItem(MediaItem.fromUri(videoUrl))
                            prepare()
                            playWhenReady = true
                            repeatMode = androidx.media3.common.Player.REPEAT_MODE_ONE
                        }
                        PlayerView(context).apply { this.player = player }
                    }
                )

                Spacer(Modifier.height(16.dp))
                Row {
                    OutlinedButton(onClick = {
                        val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(videoUrl))
                        context.startActivity(intent)
                        // TODO: implement real download-to-device via DownloadManager instead of opening in browser
                    }) { Text("Download") }

                    Spacer(Modifier.width(8.dp))

                    OutlinedButton(onClick = {
                        val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(android.content.Intent.EXTRA_TEXT, videoUrl)
                        }
                        context.startActivity(android.content.Intent.createChooser(shareIntent, "Share video"))
                    }) { Text("Share") }
                }
            }

            Spacer(Modifier.height(24.dp))
            Button(onClick = onGenerateAnother, modifier = Modifier.fillMaxWidth()) {
                Text("Generate another")
            }
        }
    }
}
