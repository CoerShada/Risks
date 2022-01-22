package ru.com.risks.common.data

import java.text.SimpleDateFormat
import java.util.*

data class Registry(var id: Int, var factory: Factory, var model: Boolean, var scaleFrom: Float, var scaleTo:Float) {

    var dateOfCreation: Date

    init{
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        this.dateOfCreation = cal.time
    }


    constructor(id: Int, factory: Factory, model: Boolean, scaleFrom: Float, scaleTo:Float, dateOfCreation: Date): this(id, factory, model, scaleFrom, scaleTo){
        this.dateOfCreation = dateOfCreation
    }

    fun getTransformedResults(number: Int): Float {
        return (this.scaleTo - this.scaleFrom) / 100 * number + this.scaleFrom
    }
}
