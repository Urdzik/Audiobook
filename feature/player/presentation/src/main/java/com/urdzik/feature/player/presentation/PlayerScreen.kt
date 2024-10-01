package com.urdzik.feature.player.presentation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import audiobook.R
import coil.compose.AsyncImage
import com.urdzik.core.api.contract.model.Chapter
import com.urdzik.core.common.composed
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun PlayerRoute() {
    val viewModel: PlayerViewModel = koinViewModel()

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val obtainEvent = viewModel::obtainEvent

    val stateEvent by viewModel.stateEvent.collectAsStateWithLifecycle()
    val obtainEventConsumed = viewModel::obtainEventConsumed

    PlayerScreen(
        uiState = uiState,
        obtainEvent = obtainEvent
    )
}


@Composable
private fun PlayerScreen(
    uiState: PlayerScreenUIState,
    obtainEvent: (PlayerScreenEvent) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.SpaceAround,
    ) {
        AnimatedContent(uiState.playerScreenType) {
            when (it) {
                PlayerScreenType.Player -> PlayerScreenContent(uiState, obtainEvent)
                PlayerScreenType.Chapters -> ChaptersScreenContent(uiState, obtainEvent)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = { obtainEvent(PlayerScreenEvent.ChangeScreenType(PlayerScreenType.Player)) }) {
                Text(text = "Player")
            }
            Button(onClick = { obtainEvent(PlayerScreenEvent.ChangeScreenType(PlayerScreenType.Chapters)) }) {
                Text(text = "Chapters")
            }
        }
    }

}

@Composable
private fun ChaptersScreenContent(
    uiState: PlayerScreenUIState,
    obtainEvent: (PlayerScreenEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Chapters",
            style = MaterialTheme.typography.labelSmall
        )

        Spacer(modifier = Modifier.height(16.dp))

       LazyColumn {
            items(uiState.book.chapter) { chapter ->
                ChapterItem(chapter, obtainEvent)
            }
        }
       }
    }

@Composable
fun ChapterItem(chapter: Chapter, obtainEvent: (PlayerScreenEvent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(4.dp, RoundedCornerShape(8.dp), clip = true)
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = chapter.title,
            style = MaterialTheme.typography.labelMedium
        )
        IconButton(onClick = { obtainEvent(PlayerScreenEvent.ChangeChapter(chapter)) }) {
            Icon(
                painter = painterResource(R.drawable.ic_play_arrow),
                contentDescription = "Play",
                modifier = Modifier.size(24.dp)
            )
        }
    }

}

@Composable
private fun PlayerScreenContent(
    uiState: PlayerScreenUIState,
    obtainEvent: (PlayerScreenEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.height(16.dp))

        val scale by animateFloatAsState(
            targetValue = if (uiState.isPlaying) 1f else .9f,
            animationSpec = tween()
        )
        AsyncImage(
            model = uiState.imageUrl,
            contentDescription = "Book Cover",
            modifier = Modifier
                .height(300.dp)
                .scale(scale)
                .clip(RoundedCornerShape(8.dp))

        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = uiState.title.composed, style = MaterialTheme.typography.labelSmall)
        Text(
            text = uiState.description.composed,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Progress bar and time
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                style = MaterialTheme.typography.labelMedium,
                text = uiState.currentAudioTime
            )
            Slider(
                modifier = Modifier.weight(1f),
                value = uiState.progress,
                onValueChange = {
                    obtainEvent(PlayerScreenEvent.SeekTo(it))
                },
            )
            Text(
                style = MaterialTheme.typography.labelMedium,
                text = uiState.totalAudioTime
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { obtainEvent(PlayerScreenEvent.ChangeSpeed(uiState.speed)) }) {
            AnimatedContent(uiState.speed, label = "") { speed ->
                Text(text = "Speed ${uiState.speed.text.composed}")
            }
        }

        Spacer(modifier = Modifier.height(54.dp))

        // Control buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { obtainEvent(PlayerScreenEvent.PreviousArticle) }) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(R.drawable.ic_skip_previous),
                    contentDescription = "Previous"
                )
            }
            IconButton(onClick = { obtainEvent(PlayerScreenEvent.MoveBackward) }) {
                Icon(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(R.drawable.ic_replay_5),
                    contentDescription = "Rewind 5 seconds"
                )
            }
            IconButton(onClick = { obtainEvent(PlayerScreenEvent.PlayOrPause) }) {
                val icon =
                    remember(uiState.isPlaying) { if (uiState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow }
                Icon(
                    modifier = Modifier.size(48.dp),
                    painter = painterResource(icon),
                    contentDescription = "Pause"
                )
            }
            IconButton(onClick = { obtainEvent(PlayerScreenEvent.MoveForward) }) {
                Icon(
                    painterResource(R.drawable.ic_forward_10),
                    contentDescription = "Forward 10 seconds",
                    modifier = Modifier.size(48.dp),
                )
            }
            IconButton(onClick = { obtainEvent(PlayerScreenEvent.NextArticle) }) {
                Icon(
                    painterResource(R.drawable.ic_skip_next),
                    contentDescription = "Next",
                    modifier = Modifier.size(48.dp),
                )
            }
        }
    }
}
