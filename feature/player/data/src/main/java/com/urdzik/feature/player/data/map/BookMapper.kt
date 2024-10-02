package com.urdzik.feature.player.data.map

import com.urdzik.core.api.contract.model.BookResponse
import com.urdzik.core.api.contract.model.ChapterResponse
import com.urdzik.feature.player.data.model.Book
import com.urdzik.feature.player.data.model.Chapter

fun BookResponse.toBook(): Book {
    return Book(
        id = id,
        cover = cover,
        name = name,
        chapter = chapter.toChapter()
    )
}

fun List<ChapterResponse>.toChapter(): List<Chapter> {
    return map { it.toChapter() }
}


fun ChapterResponse.toChapter(): Chapter {
    return Chapter(
        id = id,
        title = title,
        audioUrl = audioUrl,
        duration = duration
    )
}