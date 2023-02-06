package com.example.myapplication

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout

class MyCanvasView(context: Context?, attrs: AttributeSet? = null) : View(context, attrs)
{
    var paint: Paint? = null
    var datas = mutableMapOf<Int, FingerChoiceData>()

    var myID = -1
    var myData = FingerChoiceData(-1, -100F, -100F, 0, false)

    /*
    override fun onTouchEvent(event: MotionEvent): Boolean {
        xval = event.x
        yval = event.y

        /*if (event.pointerCount == 1)
            datas.clear()*/

/*
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {
                /*datas.apply {
                    for (item in datas) {
                        if (item.ID == event.getPointerId(event.actionIndex)) {
                            item.removed = true
                            break
                        }
                    }
                }*/
                //datas[event.getPointerId(event.actionIndex)].removed = true
                datas.remove(event.getPointerId(event.actionIndex))
            }
            MotionEvent.ACTION_MOVE -> {
                xval = event.x
                yval = event.y

                datas[event.getPointerId(event.actionIndex)]?.dX = xval
                datas[event.getPointerId(event.actionIndex)]?.dY = yval

                /*for (item in datas) {
                    if (item.ID == event.getPointerId(event.actionIndex)) {
                        item.dX = dX
                        item.dY = dY
                        break
                    }
                }*/
            }
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_POINTER_DOWN -> {
                //datas.add(FingerChoiceData(ID = event.getPointerId(event.actionIndex), dX = event.x, dY = event.y, color = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), removed = false))
                datas[event.getPointerId(event.actionIndex)] = FingerChoiceData(ID = event.getPointerId(event.actionIndex), dX = event.x, dY = event.y, color = Color.rgb(Random.nextInt(0, 255), Random.nextInt(0, 255), Random.nextInt(0, 255)), removed = false)
            }

        }
        invalidate()
 */
        return super.onTouchEvent(event)
    }
    */

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_UP, MotionEvent.ACTION_POINTER_UP -> {

                invalidate()
            }
            MotionEvent.ACTION_MOVE -> {

                //myData.dX = event.x
                //myData.dY = event.y
                this.animate()
                    .x(event.x)
                    .y(event.y)
                    .setDuration(0)
                    .start()
            }
        }
        return super.onTouchEvent(event)
    }

    fun addData(id: Int, data: FingerChoiceData, parentLayout: RelativeLayout) {
        //datas[id] = data

        val newView = MyCanvasView(this.context)
        newView.layoutParams = RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        newView.myID = id
        newView.myData = data
        parentLayout.addView(newView)
        newView.invalidate()
    }
    fun modData(id: Int, dX: Float, dY: Float) {
        datas[id]?.dX = dX
        datas[id]?.dY = dY
    }
    fun rmData(id: Int){
        //datas.remove(id)
        datas[id]?.removed = true
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        paint = Paint()
        paint?.style = Paint.Style.FILL
        paint?.strokeWidth = 5F
        paint?.color = myData.color
        canvas?.drawCircle(myData!!.dX, myData!!.dY, 100F, paint!!)
        /*paint?.color = Color.BLUE
        canvas?.drawCircle(dX, dY, 100F, paint!!)*/

        /*for (data in datas.values) {
            if (!data.removed) {
                paint?.color = data.color
                canvas?.drawCircle(data.dX, data.dY, 100F, paint!!)
            }
        }*/
        /*datas.forEach() { (_, data) ->
            /*if (!it.removed) {
                paint?.color = it.color
                canvas?.drawCircle(it.dX, it.dY, 100F, paint!!)
            }*/
            paint?.color = data.color
            canvas?.drawCircle(data.dX, data.dY, 100F, paint!!)
            //canvas?.drawCircle(xval, yval, 100F, paint!!)
        }*/
    }
}