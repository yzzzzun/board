package com.yzzzzun.board.service

import com.yzzzzun.board.domain.Post
import com.yzzzzun.board.exception.PostNotDeletableException
import com.yzzzzun.board.exception.PostNotFoundException
import com.yzzzzun.board.exception.PostNotUpdatableException
import com.yzzzzun.board.repository.PostRepository
import com.yzzzzun.board.service.dto.PostCreateRequestDto
import com.yzzzzun.board.service.dto.PostUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
) : BehaviorSpec({
        given("게시글 생성시") {
            When("게시글 생성") {
                val postId =
                    postService.createPost(
                        PostCreateRequestDto(
                            title = "제목",
                            content = "내용",
                            createdBy = "hk",
                        ),
                    )
                then("게시글 정상 생성 확인") {
                    postId shouldBeGreaterThan 0L
                    val post = postRepository.findByIdOrNull(postId)
                    post shouldNotBe null
                    post?.title shouldBe "제목"
                    post?.content shouldBe "내용"
                    post?.createdBy shouldBe "hk"
                }
            }
        }

        given("게시글 수정시") {
            val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "hk"))
            When("정상 수정시") {
                val updatedId =
                    postService.updatePost(
                        saved.id,
                        PostUpdateRequestDto(
                            title = "update title",
                            content = "update content",
                            updatedBy = "hk",
                        ),
                    )
                then("게시글 정상 수정 확인") {
                    saved.id shouldBe updatedId
                    val updated = postRepository.findByIdOrNull(updatedId)
                    updated shouldNotBe null
                    updated?.title shouldBe "update title"
                    updated?.content shouldBe "update content"
                    updated?.createdBy shouldBe "hk"
                }
            }
            When("게시글 없을 때") {
                then("게시글을 찾을 수 없음") {
                    shouldThrow<PostNotFoundException> {
                        postService.updatePost(
                            999L,
                            PostUpdateRequestDto(
                                title = "update title",
                                content = "update content",
                                updatedBy = "update hk",
                            ),
                        )
                    }
                }
            }
            When("작성자 동일하지 않을 때") {
                then("수정할 수 없는 게시글 에러") {
                    shouldThrow<PostNotUpdatableException> {
                        postService.updatePost(
                            1L,
                            PostUpdateRequestDto(
                                title = "update title",
                                content = "update content",
                                updatedBy = "update hk",
                            ),
                        )
                    }
                }
            }
        }

        given("게시글 삭제시") {
            val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "hk"))

            When("작성자와 동일하지 않으면") {
                then("삭제 할 수 없는 게시글입니다 에러 발생") {
                    shouldThrow<PostNotDeletableException> {
                        postService.deletePost(saved.id, "hk test")
                    }
                }
            }

            When("정상 삭제") {
                val deletedId = postService.deletePost(saved.id, "hk")
                then("정상 삭제처리") {
                    deletedId shouldBe saved.id
                    postRepository.findByIdOrNull(saved.id) shouldBe null
                }
            }
        }
    })
