package ru.com.risks.common.data

import ru.com.risks.common.DBHelper
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.pow

data class Risk(val id: Int, val registry: Registry, var name: String, var riskType: RiskType, var probabilityOfOccurrence: Float, var detectionProbabilityEstimate: Float, var severityAssessment: Float){
    var dateOfCreation: Date
    var magnitudeOfRisk: Float = 0.0f

    init{
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        this.dateOfCreation = cal.time
        calculateMagnitudeOfRisk()
    }


    constructor(id: Int, registry: Registry, name: String, riskType: RiskType, probabilityOfOccurrence: Float, detectionProbabilityEstimate: Float, severityAssessment: Float ,dateOfCreation: Date): this(id, registry, name, riskType, probabilityOfOccurrence, detectionProbabilityEstimate, severityAssessment){
        this.dateOfCreation = dateOfCreation
        calculateMagnitudeOfRisk()
    }

    fun calculateMagnitudeOfRisk(){
        magnitudeOfRisk = if (registry.model){(this.probabilityOfOccurrence * severityAssessment).toDouble().pow(0.5).toFloat()}
        else{(this.probabilityOfOccurrence * severityAssessment * detectionProbabilityEstimate).toDouble().pow(0.333333333).toFloat()}
    }
}



