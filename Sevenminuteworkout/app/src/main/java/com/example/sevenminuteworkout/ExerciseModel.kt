package com.example.sevenminuteworkout

class ExerciseModel(
    private var id: Int,
    private var name: String,
    private var image: Int,
    private var isSelected: Boolean,
    private var isCompleted: Boolean
) {
    fun getId(): Int {
        return this.id
    }
    fun getName():String{
        return this.name
    }
    fun getImage():Int{
        return this.image
    }
    fun getIsSelected():Boolean{
        return this.isSelected
    }
    fun getIsCompleted():Boolean{
        return this.isCompleted
    }

    fun setSelected(value:Boolean){
      this.isSelected = value
    }

    fun setCompleted(value:Boolean){
        this.isCompleted = value
    }

}