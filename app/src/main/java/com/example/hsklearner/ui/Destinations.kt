package com.example.hsklearner.ui

object Destinations {
    const val HOME = "home"
    const val UNIT_LIST = "units/{levelId}"
    const val LESSON_LIST = "lessons/{unitId}"
    const val VOCAB_LIST = "vocab/{lessonId}"
    const val FLASHCARDS = "flashcards/{lessonId}"
    const val QUIZ = "quiz/{lessonId}"

    fun unitList(levelId: Int) = "units/$levelId"
    fun lessonList(unitId: Int) = "lessons/$unitId"
    fun vocabList(lessonId: Int) = "vocab/$lessonId"
    fun flashcards(lessonId: Int) = "flashcards/$lessonId"
    fun quiz(lessonId: Int) = "quiz/$lessonId"
}
