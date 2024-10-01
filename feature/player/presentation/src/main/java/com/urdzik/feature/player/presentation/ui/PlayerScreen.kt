package com.urdzik.feature.player.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AssistChip
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import audiobook.R
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.urdzik.core.common.composed
import com.urdzik.core.ui.design_system.CommonButton
import com.urdzik.core.ui.design_system.CommonText
import com.urdzik.core.ui.shimmerBrush
import com.urdzik.feature.player.data.model.Chapter
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
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        AnimatedContent(uiState.playerScreenType) {
            when (it) {
                PlayerScreenType.Player -> PlayerScreenContent(uiState, obtainEvent)
                PlayerScreenType.Chapters -> ChaptersScreenContent(uiState, obtainEvent)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        ScreenSwitchButtons(obtainEvent = obtainEvent)
    }
}

@Composable
private fun ScreenSwitchButtons(obtainEvent: (PlayerScreenEvent) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 64.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssistChip(
            modifier = Modifier.width(96.dp),
            label = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Player",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = { obtainEvent(PlayerScreenEvent.ChangeScreenType(PlayerScreenType.Player)) }
        )
        AssistChip(
            modifier = Modifier.width(96.dp),
            label = {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "Chapters",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            onClick = { obtainEvent(PlayerScreenEvent.ChangeScreenType(PlayerScreenType.Chapters)) }
        )
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
        Text(
            text = stringResource(R.string.chapters),
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            itemsIndexed(uiState.chapters) { index, chapter ->
                ChapterItem(chapter, index, obtainEvent)
            }
        }
    }
}

@Composable
private fun ChapterItem(
    chapter: Chapter,
    index: Int,
    obtainEvent: (PlayerScreenEvent) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
            .clickable { obtainEvent(PlayerScreenEvent.ChaptersEvent.ChangeChapter(index)) }
            .background(Color.White.copy(alpha = 0.1f))
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = chapter.title,
            style = MaterialTheme.typography.labelMedium
        )
        IconButton(onClick = { obtainEvent(PlayerScreenEvent.ChaptersEvent.ChangeChapter(index)) }) {
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
        AudioBookCover(uiState = uiState)

        CommonText(
            modifier = Modifier.padding(top = 24.dp),
            text = uiState.title.composed,
            style = MaterialTheme.typography.labelSmall
        )

        CommonText(
            text = uiState.description.composed, style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold, fontSize = 16.sp
            )
        )

        Row(
            modifier = Modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(style = MaterialTheme.typography.labelMedium, text = uiState.currentAudioTime)
            Slider(
                modifier = Modifier.weight(1f),
                value = uiState.progress,
                onValueChange = {
                    obtainEvent(PlayerScreenEvent.PlayerEvent.SeekTo(it))
                }
            )
            Text(style = MaterialTheme.typography.labelMedium, text = uiState.totalAudioTime)
        }


        CommonButton(
            modifier = Modifier.padding(top = 16.dp),
            text = "Speed ${uiState.speed.text.composed}",
        ) {
            obtainEvent(PlayerScreenEvent.PlayerEvent.ChangeSpeed(uiState.speed))
        }
        PlayerControlButtons(
            modifier = Modifier.padding(top = 54.dp),
            uiState, obtainEvent
        )
    }
}

@Composable
private fun PlayerControlButtons(
    modifier: Modifier = Modifier,
    uiState: PlayerScreenUIState,
    obtainEvent: (PlayerScreenEvent) -> Unit
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        PlayerControlIcon(
            onClick = { obtainEvent(PlayerScreenEvent.PlayerEvent.PreviousArticle) },
            modifier = Modifier,
            painter = painterResource(R.drawable.ic_skip_previous),
            contentDescription = "Previous"
        )
        PlayerControlIcon(
            onClick = { obtainEvent(PlayerScreenEvent.PlayerEvent.MoveBackward) },
            modifier = Modifier,
            painter = painterResource(R.drawable.ic_replay_5),
            contentDescription = "Rewind 5 seconds"
        )

        val icon = remember(uiState.isPlaying) {
            if (uiState.isPlaying) R.drawable.ic_pause else R.drawable.ic_play_arrow
        }
        PlayerControlIcon(
            onClick = { obtainEvent(PlayerScreenEvent.PlayerEvent.PlayOrPause) },
            modifier = Modifier,
            painter = painterResource(icon),
            contentDescription = "Play/Pause"
        )
        PlayerControlIcon(
            onClick = { obtainEvent(PlayerScreenEvent.PlayerEvent.MoveForward) },
            modifier = Modifier,
            painter = painterResource(R.drawable.ic_forward_10),
            contentDescription = "Forward 10 seconds",
        )
        PlayerControlIcon(
            onClick = { obtainEvent(PlayerScreenEvent.PlayerEvent.NextArticle) },
            modifier = Modifier,
            painter = painterResource(R.drawable.ic_skip_next),
            contentDescription = "Next",
        )
    }
}

@Composable
private fun PlayerControlIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    painter: Painter,
    contentDescription: String
) {
    IconButton(onClick = onClick) {
        Icon(
            modifier = modifier.size(48.dp),
            painter = painter,
            contentDescription = contentDescription
        )
    }
}

@Composable
private fun AudioBookCover(
    modifier: Modifier = Modifier,
    uiState: PlayerScreenUIState
) {
    val scale by animateFloatAsState(
        targetValue = if (uiState.isPlaying) 1f else .9f,
        animationSpec = tween()
    )
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(uiState.imageUrl)
            .diskCachePolicy(CachePolicy.ENABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = "Book Cover",
        modifier = modifier
            .size(300.dp)
            .scale(scale)
            .clip(RoundedCornerShape(8.dp))
            .background(
                shimmerBrush(
                    targetValue = 1300f,
                    showShimmer = uiState.imageUrl.isNullOrEmpty()
                )
            ),
    )
}