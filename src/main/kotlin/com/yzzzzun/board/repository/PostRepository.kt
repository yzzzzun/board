package com.yzzzzun.board.repository

import com.yzzzzun.board.domain.Post
import org.springframework.data.jpa.repository.JpaRepository

interface PostRepository : JpaRepository<Post, Long>
