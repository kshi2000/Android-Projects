package com.example.sevenminuteworkout

object Constants {
    fun getExerciseList(): ArrayList<ExerciseModel> {
        val ExerciseList:ArrayList<ExerciseModel> = ArrayList()

        val sitUps = ExerciseModel(1,"Sit Ups",R.drawable.ic_sit_ups,false,false)
        val stretching = ExerciseModel(2,"Stretches",R.drawable.ic_pulling,false,false)
        val sideBend = ExerciseModel(3,"Side Bends",R.drawable.ic_side_bending,false,false)
        val toe = ExerciseModel(4,"Toe to Toe",R.drawable.ic_toe_to_toe,false,false)
        val weights = ExerciseModel(5,"Weight Lifts",R.drawable.iv_weight_lifting,false,false)

        ExerciseList.add(sitUps)
        ExerciseList.add(stretching)
        ExerciseList.add(sideBend)
        ExerciseList.add(toe)
        ExerciseList.add(weights)

        return ExerciseList
    }
}