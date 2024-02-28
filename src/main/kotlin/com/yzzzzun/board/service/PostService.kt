package com.yzzzzun.board.service

import com.yzzzzun.board.exception.PostNotDeletableException
import com.yzzzzun.board.exception.PostNotFoundException
import com.yzzzzun.board.repository.PostRepository
import com.yzzzzun.board.service.dto.PostCreateRequestDto
import com.yzzzzun.board.service.dto.PostUpdateRequestDto
import com.yzzzzun.board.service.dto.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PostService(
    private val postRepository: PostRepository,
) {
    @Transactional
    fun createPost(postCreateRequestDto: PostCreateRequestDto): Long {
        return postRepository.save(postCreateRequestDto.toEntity()).id
    }

    @Transactional
    fun updatePost(
        id: Long,
        postUpdateRequestDto: PostUpdateRequestDto,
    ): Long {
        val post = postRepository.findByIdOrNull(id) ?: throw PostNotFoundException()
        post.update(postUpdateRequestDto)
        return id
    }

    @Transactional
    fun deletePost(
        id: Long,
        deletedBy: String,
    ): Long {
        val post = postRepository.findByIdOrNull(id) ?: throw PostNotFoundException()
        if (post.createdBy != deletedBy) {
            throw PostNotDeletableException()
        }
        postRepository.deleteById(id)
        return id
    }
}
