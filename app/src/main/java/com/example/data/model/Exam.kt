package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val totalQuestions: Int = 100,
    val passPercentage: Double = 80.0,
    val negativeMark: Double = 0.25,
    // Comma-separated or serialized answer string e.g. "A,B,C,D,E..."
    val answerKeys: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    fun getAnswerKeyList(): List<String> {
        if (answerKeys.isBlank()) return List(totalQuestions) { "A" }
        val split = answerKeys.split(",")
        return (0 until totalQuestions).map { index ->
            if (index < split.size) split[index] else "A"
        }
    }
}
