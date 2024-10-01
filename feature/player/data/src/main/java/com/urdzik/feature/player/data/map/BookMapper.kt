package com.urdzik.feature.player.data.map

import com.urdzik.core.api.contract.model.BookResponse
import com.urdzik.core.api.contract.model.ChapterResponse
import com.urdzik.feature.player.data.model.Book
import com.urdzik.feature.player.data.model.Chapter

// book response to book
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


// chapter response to chapter
fun ChapterResponse.toChapter(): Chapter {
    return Chapter(
        mediaId = id,
        title = title,
        audioUrl = audioUrl,
        duration = duration
    )
}