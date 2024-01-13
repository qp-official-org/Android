package com.example.qp

interface Items_Answer
    data class ItemAnswer(
        var answer: Answer
    ):Items_Answer
    data class ItemWriteAnswer(
        val is_answerd:Boolean
    ):Items_Answer
    data class ItemNotice(
        val is_answered:Boolean
    ):Items_Answer
    data class ItemComment(
        var isProfessor:Boolean
    ):Items_Answer
