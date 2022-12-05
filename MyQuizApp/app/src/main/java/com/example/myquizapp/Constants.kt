package com.example.myquizapp

object Constants {
    private const val questionStatement:String="Which country flag is this?"

    const val USER_NAME:String = "user_name"
    const val TOTAL_QUESTIONS = "total_questions"
    const val CORRECT_ANSWERS = "correct_answers"
    fun getQuestions():ArrayList<Question>{
        val questions = ArrayList<Question>()
        val q1 = Question(1,
            questionStatement,R.drawable.ic_afghanistan,"Afghanistan","Pakistan","India","Sri Lanka",0)

        val q2 = Question(2,
            questionStatement,R.drawable.ic_argentina,"Norway","Argentina","Uruguay","Paraguay",1)

        val q3 = Question(3,
            questionStatement,R.drawable.ic_bangladesh,"South Africa","Macau","Saudi Arabia","Bangladesh",3)

        questions.add(q1)
        questions.add(q2)
        questions.add(q3)

        return questions
    }
}