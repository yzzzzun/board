package com.yzzzzun.board.service

import com.yzzzzun.board.domain.Comment
import com.yzzzzun.board.domain.Post
import com.yzzzzun.board.exception.CommentNotDeletableException
import com.yzzzzun.board.exception.CommentNotUpdatableException
import com.yzzzzun.board.exception.PostNotFoundException
import com.yzzzzun.board.repository.CommentRepository
import com.yzzzzun.board.repository.PostRepository
import com.yzzzzun.board.service.dto.CommentCreateRequestDto
import com.yzzzzun.board.service.dto.CommentUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.longs.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class CommentServiceTest(
    private val commentService: CommentService,
    private val commentRepository: CommentRepository,
    private val postRepository: PostRepository,
) : BehaviorSpec({
        given("댓글 생성시") {
            val post =
                postRepository.save(
                    Post(
                        title = "title",
                        content = "content",
                        createdBy = "hk",
                    ),
                )
            When("정상 input") {
                val commentId =
                    commentService.createComment(
                        post.id,
                        CommentCreateRequestDto(
                            postId = 1L,
                            content = "댓글 내용",
                            createdBy = "댓글 생성자",
                        ),
                    )
                then("정상 생성") {
                    commentId shouldBeGreaterThan 0L
                    val comment = commentRepository.findByIdOrNull(commentId)
                    comment shouldNotBe null
                    comment?.content shouldBe "댓글 내용"
                    comment?.createdBy shouldBe "댓글 생성자"
                }
            }
            When("게시글이 없을때") {
                then("게시글 존재하지 않음 예외 발생") {
                    shouldThrow<PostNotFoundException> {
                        commentService.createComment(
                            999L,
                            CommentCreateRequestDto(
                                postId = 1L,
                                content = "댓글 내용",
                                createdBy = "댓글 생성자",
                            ),
                        )
                    }
                }
            }
        }

        given("댓글 수정시") {
            val post =
                postRepository.save(
                    Post(
                        title = "title",
                        content = "content",
                        createdBy = "hk",
                    ),
                )
            val saved = commentRepository.save(Comment("댓글내용", post, "댓글 생성자"))
            When("정상 input") {
                val updatedId =
                    commentService.updateComment(
                        saved.id,
                        CommentUpdateRequestDto(
                            content = "수정 댓글 내용",
                            updatedBy = "댓글 생성자",
                        ),
                    )
                then("정상 생성") {
                    updatedId shouldBe saved.id
                    val updated = commentRepository.findByIdOrNull(updatedId)
                    updated shouldNotBe null
                    updated?.content shouldBe "수정 댓글 내용"
                    updated?.createdBy shouldBe "댓글 생성자"
                }
            }
            When("댓글 작성자와 수정자가 다르면") {
                then("수정할 수 없는 댓글 예외 발생") {
                    shouldThrow<CommentNotUpdatableException> {
                        commentService.updateComment(
                            saved.id,
                            CommentUpdateRequestDto(
                                content = "수정 댓글 내용",
                                updatedBy = "hk",
                            ),
                        )
                    }
                }
            }
        }

        given("댓글 삭제시") {
            val post =
                postRepository.save(
                    Post(
                        title = "title",
                        content = "content",
                        createdBy = "hk",
                    ),
                )
            val saved = commentRepository.save(Comment("댓글내용", post, "댓글 생성자"))

            When("댓글 작성자와 수정자가 다르면") {
                then("삭제할 수 없는 댓글 예외 발생") {
                    shouldThrow<CommentNotDeletableException> {
                        commentService.deleteComment(
                            saved.id,
                            deletedBy = "hk",
                        )
                    }
                }
            }
            When("정상 input") {
                val commentId =
                    commentService.deleteComment(
                        saved.id,
                        deletedBy = "댓글 생성자",
                    )
                then("정상 삭제") {
                    commentId shouldBe saved.id
                    commentRepository.findByIdOrNull(commentId) shouldBe null
                }
            }
        }
    })
