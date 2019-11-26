package com.tilert.message.models

data class User(val uid: String, val username: String, val profileImageUrl: String) {
    constructor() : this("", "", "")
}