package ru.com.risks.common

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import ru.com.risks.common.data.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    internal var factories: ArrayList<Factory> = ArrayList()
    internal var registries: ArrayList<Registry> = ArrayList()
    internal var risks: ArrayList<Risk> = ArrayList()
    internal var riskTypes: ArrayList<RiskType> = ArrayList()
    internal var minimizationMeasures: ArrayList<MinimizationMeasure> = ArrayList()

    init {
        loadRecords()
        instance = this
    }

    companion object {  //Статики
        private var DATABASE_VERSION = 2
        private var DATABASE_NAME = "db_main"
        lateinit var instance: DBHelper;
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("create table factories (_id integer primary key, name text)")
        db.execSQL("create table registries (_id integer primary key, factory_id integer, model integer, scale_from integer, scale_to integer, date_of_creation text)")
        db.execSQL("create table risks (_id integer primary key, registry_id integer, name text, risk_type_id integer, probability_of_occurrence real, detection_probability_estimate real, severity_assessment real, date_of_creation text)")
        db.execSQL("create table risk_types (_id integer primary key, name text, value integer)")
        db.execSQL("create table minimization_measure (_id integer primary key, risk_id integer, name text, more text, responsible text, date text, date_of_creation text, interval integer, closed integer)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists factories")
        db.execSQL("drop table if exists registries")
        db.execSQL("drop table if exists risks")
        db.execSQL("drop table if exists risk_types")
        db.execSQL("drop table if exists minimization_measure")
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("drop table if exists factories")
        db.execSQL("drop table if exists registries")
        db.execSQL("drop table if exists risks")
        db.execSQL("drop table if exists risk_types")
        db.execSQL("drop table if exists minimization_measure")
        onCreate(db)
    }

    fun saveFactory(factory: Factory){

        val db = this.readableDatabase
        val cv = ContentValues()
        if (factories.find{factoryl-> factoryl.name == factory.name }!=null) return;
        cv.put("name", factory.name)

        if (factory.id ==-1) {
            db.insert("factories", null, cv)

            val cursor: Cursor = db.query("factories", null, null, null, null, null, null)

            cursor.moveToLast()
            val cursorId: Int = cursor.getColumnIndex("_id")
            val cursorName: Int = cursor.getColumnIndex("name")
            factories.add(Factory(cursor.getInt(cursorId), cursor.getString(cursorName)))
            cursor.close()

        }
        else db.update(
            "factories", cv, "_id = ?", arrayOf(factory.id.toString()
            )
        )
    }

    @SuppressLint("SimpleDateFormat")
    fun saveRegistry(registry: Registry){

        val db = this.readableDatabase
        val cv = ContentValues()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd.MM.yyyy")
        cv.put("factory_id", registry.factory.id)
        cv.put("model", registry.model.compareTo(false))
        cv.put("scale_from", registry.scaleFrom)
        cv.put("scale_to", registry.scaleTo)
        cv.put("date_of_creation", format.format(registry.dateOfCreation))
        if (registry.id ==-1) {
            db.insert("registries", null, cv)

            val cursor: Cursor = db.query("registries", null, null, null, null, null, null)

            cursor.moveToLast()
            val cursorId: Int = cursor.getColumnIndex("_id")
            val cursorFactory = cursor.getColumnIndex("factory_id")
            val cursorModel = cursor.getColumnIndex("model")
            val cursorScaleFrom = cursor.getColumnIndex("scale_from")
            val cursorScaleTo= cursor.getColumnIndex("scale_to")
            val cursorDateOfCreation = cursor.getColumnIndex("date_of_creation")


            val date: Date = format.parse(cursor.getString(cursorDateOfCreation))!!
            val factory: Factory = factories.find { factory->factory.id == cursor.getInt(cursorFactory)}!!
            registries.add(Registry(cursor.getInt(cursorId), factory, cursor.getInt(cursorModel) == 0, cursor.getFloat(cursorScaleFrom), cursor.getFloat(cursorScaleTo), date))


            cursor.close()
        }
        else db.update(
            "registries", cv, "_id = ?", arrayOf( registry.id.toString()
            )
        )
    }


    @SuppressLint("SimpleDateFormat")
    fun saveRisk(risk: Risk){
        val db = this.readableDatabase
        val cv = ContentValues()
        @SuppressLint("SimpleDateFormat") val format1 = SimpleDateFormat("dd.MM.yyyy")
        cv.put("registry_id", risk.registry.id)
        cv.put("name", risk.name)
        cv.put("risk_type_id", risk.riskType.id)
        cv.put("probability_of_occurrence", risk.probabilityOfOccurrence)
        cv.put("detection_probability_estimate", risk.detectionProbabilityEstimate)
        cv.put("severity_assessment", risk.severityAssessment)
        cv.put("date_of_creation", format1.format(risk.dateOfCreation))
        if (risk.id ==-1) {
            db.insert("risks", null, cv)
            val cursor: Cursor = db.query("risks", null, null, null, null, null, null)

            cursor.moveToLast()
            val cursorId: Int = cursor.getColumnIndex("_id")
            val cursorRegistryId: Int = cursor.getColumnIndex("registry_id")
            val cursorName: Int = cursor.getColumnIndex("name")
            val cursorRiskTypeId: Int = cursor.getColumnIndex("risk_type_id")
            val cursorProbabilityOfOccurrence: Int = cursor.getColumnIndex("probability_of_occurrence")
            val cursorDetectionProbabilityEstimate: Int = cursor.getColumnIndex("detection_probability_estimate")
            val cursorSeverityAssessment: Int = cursor.getColumnIndex("severity_assessment")
            val cursorDateOfCreation: Int = cursor.getColumnIndex("date_of_creation")

            val dateOfCreation: Date = SimpleDateFormat("dd.MM.yyyy").parse(cursor.getString(cursorDateOfCreation))!!
            val registry: Registry = registries.find { registry->registry.id == cursor.getInt(cursorRegistryId)}!!
            val riskType: RiskType = riskTypes.find { riskType->riskType.id == cursor.getInt(cursorRiskTypeId)}!!

            risks.add(Risk(cursor.getInt(cursorId), registry, cursor.getString(cursorName), riskType, cursor.getFloat(cursorProbabilityOfOccurrence), cursor.getFloat(cursorDetectionProbabilityEstimate), cursor.getFloat(cursorSeverityAssessment), dateOfCreation))
            cursor.close()

        }
        else {
            db.update("risks", cv, "_id = ?", arrayOf(risk.id.toString()))
        }
    }

    fun saveRiskType(riskType: RiskType){
        val db = this.readableDatabase
        val cv = ContentValues()
        cv.put("name", riskType.name)
        cv.put("value", riskType.value)
        if (riskType.id ==-1) {
            db.insert("risk_types", null, cv)
            val cursor: Cursor = db.query("risk_types", null, null, null, null, null, null)

            cursor.moveToLast()
            val cursorId: Int = cursor.getColumnIndex("_id")
            val cursorName: Int = cursor.getColumnIndex("name")
            val cursorValue: Int = cursor.getColumnIndex("value")
            riskTypes.add(RiskType(cursor.getInt(cursorId), cursor.getString(cursorName), cursor.getInt(cursorValue)))
            cursor.close()

        }
        else db.update(
            "risk_types", cv, "_id = ?", arrayOf( riskType.id.toString()
            )
        )
    }



    fun saveMinimizationMeasure(minimizationMeasure: MinimizationMeasure){
        val db = this.readableDatabase
        val cv = ContentValues()
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd.MM.yyyy")
        cv.put("risk_id", minimizationMeasure.risk.id)
        cv.put("name", minimizationMeasure.name)
        cv.put("more", minimizationMeasure.more)
        cv.put("responsible", minimizationMeasure.responsible)
        cv.put("date", format.format(minimizationMeasure.date))
        cv.put("date_of_creation", format.format(minimizationMeasure.dateOfCreation))
        cv.put("interval", minimizationMeasure.interval.ordinal)
        cv.put("closed", if (minimizationMeasure.closed) 1 else 0)
        if (minimizationMeasure.id ==-1) {

            db.insert("minimization_measure", null, cv)
            val cursor: Cursor = db.query("minimization_measure", null, null, null, null, null, null)

            cursor.moveToLast()
            val cursorId: Int = cursor.getColumnIndex("_id")
            val cursorRisk: Int = cursor.getColumnIndex("risk_id")
            val cursorName: Int = cursor.getColumnIndex("name")
            val cursorMore: Int = cursor.getColumnIndex("more")
            val cursorResponsible: Int = cursor.getColumnIndex("responsible")
            val cursorDate: Int = cursor.getColumnIndex("date")
            val cursorDateOfCreation: Int = cursor.getColumnIndex("date_of_creation")
            val cursorInterval: Int = cursor.getColumnIndex("interval")
            val cursorClosed: Int = cursor.getColumnIndex("closed")

            val date: Date = format.parse(cursor.getString(cursorDate))!!
            val dateOfCreation: Date = format.parse(cursor.getString(cursorDateOfCreation))!!
            val risk: Risk = risks.find { factory->factory.id == cursor.getInt(cursorRisk)}!!
            val interval: Interval = Interval.values()[cursor.getInt(cursorInterval)]

            minimizationMeasures.add(MinimizationMeasure(cursor.getInt(cursorId), risk, cursor.getString(cursorName), cursor.getString(cursorMore), cursor.getString(cursorResponsible), date, dateOfCreation, interval, cursor.getInt(cursorClosed)==1))


            cursor.close()
        }
        else db.update(
            "minimization_measure", cv, "_id = ?", arrayOf( minimizationMeasure.id.toString()
            )
        )
    }

    private fun loadFactories(){
        val db: SQLiteDatabase = readableDatabase
        val cursor: Cursor = db.query("factories", null, null, null, null, null, null)

        cursor.moveToFirst()
        val cursorId: Int = cursor.getColumnIndex("_id")
        val cursorName: Int = cursor.getColumnIndex("name")
        if (cursor.count > 0) {
            do {
                factories.add(Factory(cursor.getInt(cursorId), cursor.getString(cursorName)))
            } while (cursor.moveToNext())
        }
        cursor.close()

    }

    @SuppressLint("SimpleDateFormat")
    private fun loadMinimizationMeasures(){
        val db: SQLiteDatabase = readableDatabase
        val cursor: Cursor = db.query("minimization_measure", null, null, null, null, null, null)

        cursor.moveToFirst()
        val cursorId: Int = cursor.getColumnIndex("_id")
        val cursorRisk: Int = cursor.getColumnIndex("risk_id")
        val cursorName: Int = cursor.getColumnIndex("name")
        val cursorMore: Int = cursor.getColumnIndex("more")
        val cursorResponsible: Int = cursor.getColumnIndex("responsible")
        val cursorDate: Int = cursor.getColumnIndex("date")
        val cursorDateOfCreation: Int = cursor.getColumnIndex("date_of_creation")
        val cursorInterval: Int = cursor.getColumnIndex("interval")
        val cursorClosed: Int = cursor.getColumnIndex("closed")

        if (cursor.count > 0) {
            do {
                val date: Date = SimpleDateFormat("dd.MM.yyyy").parse(cursor.getString(cursorDate))!!
                val dateOfCreation: Date = SimpleDateFormat("dd.MM.yyyy").parse(cursor.getString(cursorDateOfCreation))!!
                val risk: Risk = risks.find { factory->factory.id == cursor.getInt(cursorRisk)}!!
                val interval: Interval = Interval.values().get(cursor.getInt(cursorInterval))
                minimizationMeasures.add(MinimizationMeasure(cursor.getInt(cursorId), risk, cursor.getString(cursorName), cursor.getString(cursorMore), cursor.getString(cursorResponsible), date, dateOfCreation, interval, cursor.getInt(cursorClosed)==1))
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private fun loadRiskTypes(){
        val db: SQLiteDatabase = readableDatabase
        val cursor: Cursor = db.query("risk_types", null, null, null, null, null, null)

        cursor.moveToFirst()
        val cursorId: Int = cursor.getColumnIndex("_id")
        val cursorName: Int = cursor.getColumnIndex("name")
        val cursorValue: Int = cursor.getColumnIndex("value")

        if (cursor.count > 0) {
            do {
                riskTypes.add(RiskType(cursor.getInt(cursorId), cursor.getString(cursorName), cursor.getInt(cursorValue)))
            } while (cursor.moveToNext())
        }
        cursor.close()


    }

    @SuppressLint("SimpleDateFormat")
    private fun loadRisks(){
        val db: SQLiteDatabase = readableDatabase
        val cursor: Cursor = db.query("risks", null, null, null, null, null, null)
        cursor.moveToFirst()

        val cursorId: Int = cursor.getColumnIndex("_id")
        val cursorRegistryId: Int = cursor.getColumnIndex("registry_id")
        val cursorName: Int = cursor.getColumnIndex("name")
        val cursorRiskTypeId: Int = cursor.getColumnIndex("risk_type_id")
        val cursorProbabilityOfOccurrence: Int = cursor.getColumnIndex("probability_of_occurrence")
        val cursorDetectionProbabilityEstimate: Int = cursor.getColumnIndex("detection_probability_estimate")
        val cursorSeverityAssessment: Int = cursor.getColumnIndex("severity_assessment")
        val cursorDateOfCreation: Int = cursor.getColumnIndex("date_of_creation")

        if (cursor.count > 0) {
            do {
                val dateOfCreation: Date = SimpleDateFormat("dd.MM.yyyy").parse(cursor.getString(cursorDateOfCreation))!!
                val registry: Registry = registries.find { registry->registry.id == cursor.getInt(cursorRegistryId)}!!
                val riskType: RiskType = riskTypes.find { riskType->riskType.id == cursor.getInt(cursorRiskTypeId)}!!

                risks.add(Risk(cursor.getInt(cursorId), registry, cursor.getString(cursorName), riskType, cursor.getFloat(cursorProbabilityOfOccurrence), cursor.getFloat(cursorDetectionProbabilityEstimate), cursor.getFloat(cursorSeverityAssessment), dateOfCreation))
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    @SuppressLint("SimpleDateFormat")
    fun loadRegistries(){
        val db: SQLiteDatabase = readableDatabase
        val cursor: Cursor = db.query("registries", null, null, null, null, null, null)

        cursor.moveToFirst()
        val cursorId: Int = cursor.getColumnIndex("_id")
        val cursorFactory = cursor.getColumnIndex("factory_id")
        val cursorModel = cursor.getColumnIndex("model")
        val cursorScaleFrom = cursor.getColumnIndex("scale_from")
        val cursorScaleTo= cursor.getColumnIndex("scale_to")
        val cursorDateOfCreation = cursor.getColumnIndex("date_of_creation")

        if (cursor.count > 0) {
            do {
                val date: Date = SimpleDateFormat("dd.MM.yyyy").parse(cursor.getString(cursorDateOfCreation))!!
                val factory: Factory = factories.find { factory->factory.id == cursor.getInt(cursorFactory)}?: continue
                registries.add(Registry(cursor.getInt(cursorId), factory, cursor.getInt(cursorModel) == 0, cursor.getFloat(cursorScaleFrom), cursor.getFloat(cursorScaleTo), date))
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private fun loadRecords(){
        loadFactories()
        loadRegistries()
        loadRiskTypes()
        loadRisks()
        loadMinimizationMeasures()
    }
}