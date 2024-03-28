package com.yzzzzun.board.service

import com.yzzzzun.board.domain.Post
import com.yzzzzun.board.exception.PostNotFoundException
import com.yzzzzun.board.repository.LikeRepository
import com.yzzzzun.board.repository.PostRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.extensions.testcontainers.perSpec
import org.springframework.boot.test.context.SpringBootTest
import org.testcontainers.containers.GenericContainer

@SpringBootTest
class LikeServiceTest(
    private val likeService: LikeService,
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository,
) : BehaviorSpec({
        val redisContainer = GenericContainer<Nothing>("redis:5.0.3-alpine")
        beforeSpec {
            redisContainer.portBindings.add("16379:6379")
            redisContainer.start()
            listener(redisContainer.perSpec())
        }
        afterSpec {
            redisContainer.stop()
        }
        given("좋아요 생성시") {
            val saved = postRepository.save(Post("hk", "title", "content"))
//            When("인풋이 정상") {
//                val likeId = likeService.createLike(1L, "hk")
//                then("좋아요 정상 생성") {
//                    val like = likeRepository.findByIdOrNull(likeId)
//                    like shouldNotBe null
//                    like?.createdBy shouldBe "hk"
//                }
//            }

            When("게시글 존재하지 않으면") {
                then("존재하지 않는 게시글 에러") {
                    shouldThrow<PostNotFoundException> {
                        likeService.createLike(9999L, "hk")
                    }
                }
            }
        }
    })
