package com.yzzzzun.board.controller.dto

import com.yzzzzun.board.service.dto.PostUpdateRequestDto

data class PostUpdateRequest(
    val title: String,
    val content: String,
    val updatedBy: String,
)

fun PostUpdateRequest.toDto() =
    PostUpdateRequestDto(
        title = title,
        content = content,
        updatedBy = updatedBy,
    )
