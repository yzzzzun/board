package com.yzzzzun.board.repository

import com.yzzzzun.board.domain.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long>
