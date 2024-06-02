package com.example.testapp

class User(val login: String, val password: String) {
    companion object {
        val listOfUsers = HashMap<String, String>()
    }

    init {
        listOfUsers[login] = password
    }
}