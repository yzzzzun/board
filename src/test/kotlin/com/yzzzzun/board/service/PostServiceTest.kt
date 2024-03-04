package com.yzzzzun.board.service

import com.yzzzzun.board.domain.Comment
import com.yzzzzun.board.domain.Post
import com.yzzzzun.board.exception.PostNotDeletableException
import com.yzzzzun.board.exception.PostNotFoundException
import com.yzzzzun.board.exception.PostNotUpdatableException
import com.yzzzzun.board.repository.CommentRepository
import com.yzzzzun.board.repository.PostRepository
import com.yzzzzun.board.repository.TagRepository
import com.yzzzzun.board.service.dto.PostCreateRequestDto
import com.yzzzzun.board.service.dto.PostSearchRequestDto
import com.yzzzzun.board.service.dto.PostUpdateRequestDto
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.comparables.shouldBeGreaterThan
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull

@SpringBootTest
class PostServiceTest(
    private val postService: PostService,
    private val postRepository: PostRepository,
    private val commentRepository: CommentRepository,
    private val tagRepository: TagRepository,
) : BehaviorSpec({
        beforeSpec {
            postRepository.saveAll(
                listOf(
                    Post(title = "title1", content = "content", createdBy = "hk"),
                    Post(title = "title12", content = "content", createdBy = "hk"),
                    Post(title = "title13", content = "content", createdBy = "hk"),
                    Post(title = "title14", content = "content", createdBy = "hk"),
                    Post(title = "title15", content = "content", createdBy = "hk"),
                    Post(title = "title6", content = "content", createdBy = "hk1"),
                    Post(title = "title7", content = "content", createdBy = "hk1"),
                    Post(title = "title8", content = "content", createdBy = "hk1"),
                    Post(title = "title9", content = "content", createdBy = "hk1"),
                    Post(title = "title10", content = "content", createdBy = "hk1"),
                ),
            )
        }
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

            When("태그 추가시") {
                val postId =
                    postService.createPost(
                        PostCreateRequestDto(
                            title = "제목",
                            content = "내용",
                            createdBy = "hk",
                            tags = listOf("tag1", "tag2"),
                        ),
                    )
                then("태그가 정상적으로 추가됨") {
                    val tags = tagRepository.findByPostId(postId)
                    tags.size shouldBe 2
                    tags[0].name shouldBe "tag1"
                    tags[1].name shouldBe "tag2"
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
            When("태그 수정시") {
                val updatedId =
                    postService.updatePost(
                        saved.id,
                        PostUpdateRequestDto(
                            title = "update title",
                            content = "update content",
                            updatedBy = "hk",
                            tags = listOf("tag1", "tag2", "tag3"),
                        ),
                    )
                then("정상 수정 확인") {
                    val tags = tagRepository.findByPostId(updatedId)
                    tags.size shouldBe 3
                    tags[2].name shouldBe "tag3"
                }
                then("순서 변경 확인") {
                    postService.updatePost(
                        saved.id,
                        PostUpdateRequestDto(
                            title = "update title",
                            content = "update content",
                            updatedBy = "hk",
                            tags = listOf("tag3", "tag2", "tag1"),
                        ),
                    )
                    val tags = tagRepository.findByPostId(updatedId)
                    tags.size shouldBe 3
                    tags[2].name shouldBe "tag1"
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

        given("게시글 조회시") {
            val saved = postRepository.save(Post(title = "title", content = "content", createdBy = "hk"))
            When("게시글 조회 정상케이스") {
                val post = postService.getPost(saved.id)
                then("게시글 내용 정상 반환") {
                    post.id shouldBe saved.id
                    post.title shouldBe saved.title
                    post.content shouldBe saved.content
                    post.createdBy shouldBe saved.createdBy
                }
            }
            When("게시글 없는 경우") {
                then("게시글이 존재하지 않는다는 예외 발생 ") {
                    shouldThrow<PostNotFoundException> {
                        postService.getPost(999L)
                    }
                }
            }

            When("댓글 추가시") {
                commentRepository.save(Comment(content = "댓글 내용1", post = saved, createdBy = "댓글 작성자"))
                commentRepository.save(Comment(content = "댓글 내용2", post = saved, createdBy = "댓글 작성자"))
                commentRepository.save(Comment(content = "댓글 내용3", post = saved, createdBy = "댓글 작성자"))
                val post = postService.getPost(saved.id)
                then("댓글이 함께 조회됨을 확인") {
                    post.comments.size shouldBe 3
                    post.comments[0].content shouldBe "댓글 내용1"
                    post.comments[1].content shouldBe "댓글 내용2"
                    post.comments[2].content shouldBe "댓글 내용3"
                    post.comments[0].createdBy shouldBe "댓글 작성자"
                    post.comments[1].createdBy shouldBe "댓글 작성자"
                    post.comments[2].createdBy shouldBe "댓글 작성자"
                }
            }
        }

        given("게시글 목록 조회시") {
            When("정상 조회시") {
                val posts = postService.getPosts(PageRequest.of(0, 5), PostSearchRequestDto())
                then("게시글 페이지 반환") {
                    posts.number shouldBe 0
                    posts.size shouldBe 5
                    posts.content.size shouldBe 5
                    posts.content[0].title shouldBe "title"
                    posts.content[0].createdBy shouldBe "hk"
                }
            }
            When("타이틀로 검색") {
                val postPage = postService.getPosts(PageRequest.of(0, 5), PostSearchRequestDto(title = "title1"))
                then("타이틀에 해당하는 게시글이 반환된다.") {
                    postPage.number shouldBe 0
                    postPage.size shouldBe 5
                    postPage.content.size shouldBe 5
                    postPage.content[0].title shouldContain "title1"
                    postPage.content[0].createdBy shouldContain "hk"
                }
            }

            When("작성자로 검색") {
                val postPage = postService.getPosts(PageRequest.of(0, 5), PostSearchRequestDto(createdBy = "hk1"))
                then("작성자에 해당하는 게시글이 반환된다.") {
                    postPage.number shouldBe 0
                    postPage.size shouldBe 5
                    postPage.content.size shouldBe 5
                    postPage.content[0].title shouldContain "title"
                    postPage.content[0].createdBy shouldBe "hk1"
                }
            }
        }
    })
