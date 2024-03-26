package com.yzzzzun.board.service.dto

import com.yzzzzun.board.domain.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl

fun Page<Tag>.toSummaryResponseDto(countLike: (Long) -> Long) =
    PageImpl(
        content.map { it.toSummaryResponseDto(countLike) },
        pageable,
        totalElements,
    )

fun Tag.toSummaryResponseDto(countLike: (Long) -> Long) =
    PostSummaryResponseDto(
        id = post.id,
        title = post.title,
        createdBy = post.createdBy,
        createdAt = post.createdAt.toString(),
        firstTag = name,
        likeCount = countLike(post.id),
    )
