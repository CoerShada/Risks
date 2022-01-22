package ru.com.risks.common.data

import android.content.Context
import android.content.res.Resources
import ru.com.risks.R
import java.util.*
import kotlin.collections.ArrayList

data class MinimizationMeasure(val id: Int, val risk: Risk, var name: String, var more: String, var responsible: String, var date: Date, var interval: Interval, var closed: Boolean){
    var dateOfCreation: Date

    init{
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        this.dateOfCreation = cal.time
    }


    constructor(id: Int, risk: Risk, name: String, more: String, responsible: String, date: Date, dateOfCreation: Date, interval: Interval, closed:Boolean): this(id, risk, name, more, responsible, date, interval, closed){
        this.dateOfCreation = dateOfCreation
    }

}

enum class Interval(val descId: Int){
    NO(-1),
    DAY(R.string.day),
    WEEK(R.string.week),
    DECADE(R.string.decade),
    MONTH(R.string.month),
    QUARTER(R.string.quarter),
    TRIMESTER(R.string.trimester),
    HALF_YEAR(R.string.halfYear),
    YEAR(R.string.year);

    companion object {
        fun getDescriptions(context: Context): ArrayList<String> {
            val names: ArrayList<String> = ArrayList()
            for (interval: Interval in values()) {
                if (interval.descId<0) continue
                names.add(context.getString(interval.descId))
            }
            return names
        }
    }
}
