package com.yzzzzun.board.repository

import com.yzzzzun.board.domain.Like
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<Like, Long> {
    fun countByPostId(postId: Long): Long
}
