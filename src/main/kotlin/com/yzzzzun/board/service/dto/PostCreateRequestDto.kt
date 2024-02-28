package com.yzzzzun.board.service.dto

import com.yzzzzun.board.domain.Post

data class PostCreateRequestDto(
    val title: String,
    val content: String,
    val createdBy: String,
)

fun PostCreateRequestDto.toEntity() =
    Post(
        title = title,
        content = content,
        createdBy = createdBy,
    )
