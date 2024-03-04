package com.yzzzzun.board.controller.dto

import com.yzzzzun.board.service.dto.PostCreateRequestDto

data class PostCreateRequest(
    val title: String,
    val content: String,
    val createdBy: String,
    val tags: List<String> = emptyList(),
)

fun PostCreateRequest.toDto() =
    PostCreateRequestDto(
        title = title,
        content = content,
        createdBy = createdBy,
    )
