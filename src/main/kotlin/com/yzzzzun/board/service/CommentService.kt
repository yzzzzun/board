package com.yzzzzun.board.service

import com.yzzzzun.board.exception.CommentNotDeletableException
import com.yzzzzun.board.exception.CommentNotFoundException
import com.yzzzzun.board.exception.PostNotFoundException
import com.yzzzzun.board.repository.CommentRepository
import com.yzzzzun.board.repository.PostRepository
import com.yzzzzun.board.service.dto.CommentCreateRequestDto
import com.yzzzzun.board.service.dto.CommentUpdateRequestDto
import com.yzzzzun.board.service.dto.toEntity
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CommentService(
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
) {
    @Transactional
    fun createComment(
        postId: Long,
        commentCreateRequestDto: CommentCreateRequestDto,
    ): Long {
        val post = postRepository.findByIdOrNull(postId) ?: throw PostNotFoundException()
        return commentRepository.save(commentCreateRequestDto.toEntity(post)).id
    }

    @Transactional
    fun updateComment(
        id: Long,
        commentUpdateRequestDto: CommentUpdateRequestDto,
    ): Long {
        val comment = commentRepository.findByIdOrNull(id) ?: throw CommentNotFoundException()
        comment.update(commentUpdateRequestDto)
        return comment.id
    }

    @Transactional
    fun deleteComment(
        id: Long,
        deletedBy: String,
    ): Long {
        val comment = commentRepository.findByIdOrNull(id) ?: throw CommentNotFoundException()
        if (comment.createdBy != deletedBy) {
            throw CommentNotDeletableException()
        }
        commentRepository.delete(comment)
        return id
    }
}
