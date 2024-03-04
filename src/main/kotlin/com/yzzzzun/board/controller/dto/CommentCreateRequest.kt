package com.yzzzzun.board.controller.dto

import com.yzzzzun.board.service.dto.CommentCreateRequestDto

data class CommentCreateRequest(
    val content: String,
    val createdBy: String,
)

fun CommentCreateRequest.toDto() =
    CommentCreateRequestDto(
        content = content,
        createdBy = createdBy,
    )
