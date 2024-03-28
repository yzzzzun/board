package com.yzzzzun.board.event

import com.yzzzzun.board.domain.Like
import com.yzzzzun.board.event.dto.LikeEvent
import com.yzzzzun.board.exception.PostNotFoundException
import com.yzzzzun.board.repository.LikeRepository
import com.yzzzzun.board.repository.PostRepository
import com.yzzzzun.board.util.RedisUtil
import org.springframework.data.repository.findByIdOrNull
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.event.TransactionalEventListener

@Service
class LikeEventHandler(
    private val postRepository: PostRepository,
    private val likeRepository: LikeRepository,
    private val redisUtil: RedisUtil,
) {
    @Async
    @TransactionalEventListener(LikeEvent::class)
    fun handle(event: LikeEvent) {
        val post = postRepository.findByIdOrNull(event.postId) ?: throw PostNotFoundException()
        redisUtil.increment(redisUtil.getLikeCountKey(event.postId))
        likeRepository.save(Like(post, event.createdBy)).id
    }
}
