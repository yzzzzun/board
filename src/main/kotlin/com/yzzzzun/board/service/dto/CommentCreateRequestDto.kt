package com.yzzzzun.board.service.dto

import com.yzzzzun.board.domain.Comment
import com.yzzzzun.board.domain.Post

data class CommentCreateRequestDto(
    val content: String,
    val createdBy: String,
)

fun CommentCreateRequestDto.toEntity(post: Post) =
    Comment(
        content = content,
        post = post,
        createdBy = createdBy,
    )
