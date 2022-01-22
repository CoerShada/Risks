package ru.com.risks.client.risks

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.com.risks.R
import ru.com.risks.client.IFragmented
import ru.com.risks.common.DBHelper
import ru.com.risks.common.data.Interval
import ru.com.risks.common.data.MinimizationMeasure
import java.text.SimpleDateFormat
import java.util.*

class SettingUpMinimizationMeasureFragment : Fragment(), View.OnClickListener {

    lateinit var currentMinimizationMeasure: MinimizationMeasure
    var create = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: ViewGroup = inflater.inflate(R.layout.fragment_setting_up_minimization_measure, container, false) as ViewGroup


        val checkBoxMinimizationMeasureRegular = view.findViewById<CheckBox>(R.id.checkBoxMinimizationMeasureRegular)
        checkBoxMinimizationMeasureRegular.setOnClickListener(this)
        view.findViewById<FloatingActionButton>(R.id.buttonMinimizationMeasureSave).setOnClickListener(this)
        val spinnerInterval: Spinner = view.findViewById(R.id.spinnerMinimizationMeasureInterval)
        val adapterInterval: ArrayAdapter<String> = ArrayAdapter(activity!!.baseContext, android.R.layout.simple_spinner_item, Interval.getDescriptions(this.activity!!.applicationContext))
        adapterInterval.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerInterval.adapter = adapterInterval

        setContent(view)
        hideRegularView(view, this.currentMinimizationMeasure.interval==Interval.NO)
        offViews(view)
        return view
    }


    @SuppressLint("CutPasteId")
    private fun setContent(view: ViewGroup){
        if (currentMinimizationMeasure.id==-1) return
        @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd.MM.yyyy")
        val checkBoxMinimizationMeasureRegular = view.findViewById<CheckBox>(R.id.checkBoxMinimizationMeasureRegular)
        if (this.currentMinimizationMeasure.interval!=Interval.NO){
            checkBoxMinimizationMeasureRegular.isChecked = true
            val spinner = view.findViewById<Spinner>(R.id.spinnerMinimizationMeasureInterval)
            //spinner.visibility = View.VISIBLE
            spinner.setSelection(this.currentMinimizationMeasure.interval.ordinal-1)
            //view.findViewById<TextView>(R.id.labelMinimizationMeasureInterval).visibility = View.VISIBLE
        }
        view.findViewById<TextView>(R.id.textViewMinimizationMeasureName).text = this.currentMinimizationMeasure.name
        view.findViewById<TextView>(R.id.textViewMinimizationMeasureMore).text = this.currentMinimizationMeasure.more
        view.findViewById<TextView>(R.id.textViewMinimizationMeasureResponsible).text = this.currentMinimizationMeasure.responsible
        view.findViewById<TextView>(R.id.dateViewMinimizationMeasureTheDateOfThe).text = format.format(this.currentMinimizationMeasure.date)
        val checkBox = view.findViewById<CheckBox>(R.id.checkBoxMinimizationMeasureClosed)
        checkBox.isChecked = this.currentMinimizationMeasure.closed
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        val currentDate = cal.time
        if (currentDate>=this.currentMinimizationMeasure.date){
            checkBox.visibility = View.VISIBLE
        }


    }



    private fun hideRegularView(fragment: ViewGroup, hide: Boolean){
        val spinner = fragment.findViewById<Spinner>(R.id.spinnerMinimizationMeasureInterval)
        val label = fragment.findViewById<TextView>(R.id.labelMinimizationMeasureInterval)
        if (hide){
            spinner.visibility = View.GONE
            label.visibility = View.GONE
        }
        else{
            spinner.visibility = View.VISIBLE
            label.visibility = View.VISIBLE
        }
    }

    private fun loadData(){
        var id = this.arguments?.getInt("index")
        var idRisk = this.arguments?.getInt("indexRisk")
        if (id==null){
            id = -1
        }
        if (idRisk == null || idRisk==-1) return
        if (id==-1){
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, 1)

            this.currentMinimizationMeasure = MinimizationMeasure(id, DBHelper.instance.risks.find { risk -> risk.id == idRisk}!!, "unnamed", "empty", "empty",  cal.time, Interval.NO, false)
            return
        }
        this.currentMinimizationMeasure = DBHelper.instance.minimizationMeasures.find { minimizationMeasure -> minimizationMeasure.id == id}!!
    }

    override fun onClick(v: View) {
        lateinit var activityFragmented: IFragmented
        if (this.activity is IFragmented) {
            activityFragmented = this.activity as IFragmented
        }
        else{
            return
        }

        when (v.id) {
            R.id.checkBoxMinimizationMeasureRegular->{
                val checkBox = this.view!!.findViewById<CheckBox>(R.id.checkBoxMinimizationMeasureRegular)
                hideRegularView(this.view!! as ViewGroup, !checkBox.isChecked)
                this.create = checkBox.isChecked
            }
            R.id.buttonMinimizationMeasureSave->{
                @SuppressLint("SimpleDateFormat") val format = SimpleDateFormat("dd.MM.yyyy")
                this.currentMinimizationMeasure.name = view!!.findViewById<TextView>(R.id.textViewMinimizationMeasureName).text.toString()
                this.currentMinimizationMeasure.more = view!!.findViewById<TextView>(R.id.textViewMinimizationMeasureMore).text.toString()
                this.currentMinimizationMeasure.responsible = view!!.findViewById<TextView>(R.id.textViewMinimizationMeasureResponsible).text.toString()
                this.currentMinimizationMeasure.closed = view!!.findViewById<CheckBox>(R.id.checkBoxMinimizationMeasureClosed).isChecked
                if (!view!!.findViewById<CheckBox>(R.id.checkBoxMinimizationMeasureRegular).isChecked){
                    this.currentMinimizationMeasure.interval = Interval.NO
                }
                else{
                    this.currentMinimizationMeasure.interval = Interval.values()[view!!.findViewById<Spinner>(R.id.spinnerMinimizationMeasureInterval).selectedItemPosition+1]
                }
                this.currentMinimizationMeasure.date = format.parse(view!!.findViewById<TextView>(R.id.dateViewMinimizationMeasureTheDateOfThe).text.toString())!!
                DBHelper.instance.saveMinimizationMeasure(currentMinimizationMeasure)
                if (this.create) {

                    val cal = Calendar.getInstance()
                    cal.time = currentMinimizationMeasure.date

                    when (this.currentMinimizationMeasure.interval) {
                        Interval.DAY -> {
                            cal.add(Calendar.DAY_OF_MONTH, 1)
                        }
                        Interval.WEEK -> {
                            cal.add(Calendar.WEEK_OF_MONTH, 1)
                        }
                        Interval.DECADE -> {
                            cal.add(Calendar.DAY_OF_MONTH, 10)
                        }
                        Interval.MONTH -> {
                            cal.add(Calendar.MONTH, 1)
                        }
                        Interval.QUARTER->{
                            cal.add(Calendar.MONTH, 3)
                        }
                        Interval.TRIMESTER->{
                            cal.add(Calendar.MONTH, 4)
                        }
                        Interval.HALF_YEAR->{
                            cal.add(Calendar.MONTH, 6)
                        }
                        Interval.YEAR->{
                            cal.add(Calendar.YEAR, 6)
                        }


                    }

                    val minimizationMeasure = MinimizationMeasure(
                        -1,
                        currentMinimizationMeasure.risk,
                        currentMinimizationMeasure.name,
                        currentMinimizationMeasure.more,
                        currentMinimizationMeasure.responsible,
                        cal.time,
                        currentMinimizationMeasure.interval,
                        false
                    )
                    DBHelper.instance.saveMinimizationMeasure(minimizationMeasure)
                }
                val bundle = Bundle()
                bundle.putInt("index", this.currentMinimizationMeasure.risk.id)
                val fragment = SettingUpRiskFragment()
                fragment.arguments = bundle
                activityFragmented.replaceFragment(fragment)
            }
        }
    }

    private fun offViews(view: ViewGroup){
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        var date1 = cal.time
        cal.add(Calendar.WEEK_OF_MONTH, -1)
        var date2 = cal.time


        if ((!this.currentMinimizationMeasure.closed && date1>=this.currentMinimizationMeasure.date && date2<=this.currentMinimizationMeasure.dateOfCreation) || this.currentMinimizationMeasure.id==-1) return;

        view.findViewById<TextView>(R.id.textViewMinimizationMeasureName).isEnabled = false
        view.findViewById<TextView>(R.id.dateViewMinimizationMeasureTheDateOfThe).isEnabled = false
        view.findViewById<TextView>(R.id.textViewMinimizationMeasureMore).isEnabled = false
        view.findViewById<TextView>(R.id.textViewMinimizationMeasureResponsible).isEnabled = false
        view.findViewById<CheckBox>(R.id.checkBoxMinimizationMeasureRegular).isEnabled = false
        view.findViewById<Spinner>(R.id.spinnerMinimizationMeasureInterval).isEnabled = false
        view.findViewById<TextView>(R.id.textViewMinimizationMeasureDegradedMode).visibility = View.VISIBLE
        if(this.currentMinimizationMeasure.closed)
            view.findViewById<CheckBox>(R.id.checkBoxMinimizationMeasureClosed).isEnabled = false

    }
}