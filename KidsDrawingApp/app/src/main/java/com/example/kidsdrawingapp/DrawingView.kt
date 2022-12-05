package com.example.kidsdrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View


class DrawingView(context:Context,attrs:AttributeSet):View(context,attrs) {
    private var mDrawPath:CustomPath? = null
    private var mCanvasBitmap:Bitmap? = null
    private var mDrawPaint:Paint? = null
    private var mCanvasPaint:Paint? = null
    private var mBrushSize= 20.toFloat()
    private var color: Int = Color.BLACK
    private var canvas:Canvas?= null

    private var mPaths = ArrayList<CustomPath>()
    private var mUndoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    private fun  setUpDrawing(){
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color, mBrushSize)
        mDrawPaint?.color = color
        mDrawPaint?.style = Paint.Style.STROKE
        mDrawPaint?.strokeJoin = Paint.Join.ROUND
        mDrawPaint?.strokeCap = Paint.Cap.ROUND
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888)

        //commented out this, but didn't make any difference!!
      canvas = Canvas(mCanvasBitmap!!)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
       canvas.drawBitmap(mCanvasBitmap!!,0f,0f,mCanvasPaint)

        for(path in mPaths){
            mDrawPaint!!.strokeWidth = path.brushThickness
            mDrawPaint!!.color = path.color
            canvas.drawPath(path,mDrawPaint!!)
        }
        if(!mDrawPath!!.isEmpty){
            mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
            mDrawPaint!!.color = mDrawPath!!.color
            canvas.drawPath(mDrawPath!!,mDrawPaint!!)
        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when(event?.action){
            MotionEvent.ACTION_DOWN->{
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize

                mDrawPath!!.reset()
                mDrawPath!!.moveTo(touchX!!,touchY!!)
            }

            MotionEvent.ACTION_MOVE->{
                mDrawPath!!.lineTo(touchX!!,touchY!!)
            }

            MotionEvent.ACTION_UP->{
                mPaths!!.add(mDrawPath!!)
                mDrawPath = CustomPath(color,mBrushSize)
            }

            else-> return false
        }
        invalidate()
        return  true
    }

    fun setBrushSize(newSize:Float){
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)
    }

    fun setPaintColor(newColor:String){
        color = Color.parseColor(newColor)
    }

    fun onUndo(){
        if(mPaths.isNotEmpty()){
            mUndoPaths.add(mPaths.removeLast())
            invalidate()
        }
    }

    fun onRedo(){
        if(mUndoPaths.isNotEmpty()){
            mPaths.add(mUndoPaths.removeLast())
            invalidate()
        }
    }
    internal inner class CustomPath(var color:Int,var brushThickness:Float): Path(){


    }
}