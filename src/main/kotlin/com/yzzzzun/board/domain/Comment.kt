package com.yzzzzun.board.domain

import com.yzzzzun.board.exception.CommentNotUpdatableException
import com.yzzzzun.board.service.dto.CommentUpdateRequestDto
import jakarta.persistence.ConstraintMode
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ForeignKey
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
class Comment(
    content: String,
    post: Post,
    createdBy: String,
) : BaseEntity(createdBy = createdBy) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var content: String = content
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = ForeignKey(ConstraintMode.NO_CONSTRAINT))
    var post: Post = post
        protected set

    fun update(commentUpdateRequestDto: CommentUpdateRequestDto) {
        if (commentUpdateRequestDto.updatedBy != createdBy) {
            throw CommentNotUpdatableException()
        }
        content = commentUpdateRequestDto.content
        super.updatedBy(commentUpdateRequestDto.updatedBy)
    }
}
