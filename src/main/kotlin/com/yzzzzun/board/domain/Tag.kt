package com.yzzzzun.board.domain

import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToMany

@Entity
class Tag(
    name: String,
    post: Post,
    createdBy: String,
) {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    var name: String = name
        protected set

    @ManyToMany(fetch = FetchType.LAZY)
    var post: Post = post
        protected set
}
