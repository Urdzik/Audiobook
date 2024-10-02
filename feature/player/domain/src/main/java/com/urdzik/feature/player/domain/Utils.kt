package com.urdzik.feature.player.domain

import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import com.urdzik.feature.player.data.model.Chapter

@OptIn(UnstableApi::class)
fun MediaItem.toChapter() =
    Chapter(
        id = mediaId,
        title = mediaMetadata.title.toString(),
        duration = mediaMetadata.durationMs?.toInt() ?: 0
    )
