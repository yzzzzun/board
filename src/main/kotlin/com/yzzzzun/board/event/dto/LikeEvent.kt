package com.yzzzzun.board.event.dto

data class LikeEvent(
    val postId: Long,
    val createdBy: String,
)
