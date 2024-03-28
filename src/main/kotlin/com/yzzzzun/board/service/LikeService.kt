package com.yzzzzun.board.service

import com.yzzzzun.board.domain.Like
import com.yzzzzun.board.exception.PostNotFoundException
import com.yzzzzun.board.repository.LikeRepository
import com.yzzzzun.board.repository.PostRepository
import com.yzzzzun.board.util.RedisUtil
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class LikeService(
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository,
    private val redisUtil: RedisUtil,
) {
    @Transactional
    fun createLike(
        postId: Long,
        createdBy: String,
    ): Long {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        redisUtil.increment(redisUtil.getLikeCountKey(postId))
        return likeRepository.save(Like(post, createdBy)).id
    }

    fun countLike(postId: Long): Long {
        redisUtil.getCount(redisUtil.getLikeCountKey(postId))?.let {
            return it
        }
        with(likeRepository.countByPostId(postId)) {
            redisUtil.setData(redisUtil.getLikeCountKey(postId), 1L)
            return this
        }
    }
}
