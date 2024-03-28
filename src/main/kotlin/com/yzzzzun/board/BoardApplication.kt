package com.yzzzzun.board

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class BoardApplication

fun main(args: Array<String>) {
    runApplication<BoardApplication>(*args)
}
