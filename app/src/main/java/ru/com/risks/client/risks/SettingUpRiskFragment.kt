package ru.com.risks.client.risks

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.com.risks.R
import ru.com.risks.client.IFragmented
import ru.com.risks.client.rv.MinimizationMeasureAdapter
import ru.com.risks.common.DBHelper
import ru.com.risks.common.data.Risk
import java.lang.String
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.abs
import kotlin.math.roundToInt


class SettingUpRiskFragment : Fragment(), View.OnClickListener, View.OnTouchListener{

    private lateinit var currentRisk: Risk
    private lateinit var adapter: MinimizationMeasureAdapter
    var x = 0F
    var y = 0F
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (DBHelper.instance.riskTypes.size==0){
            if (this.activity is IFragmented){
                val activityFragmented: IFragmented = this.activity as IFragmented
                activityFragmented.replaceFragment(CreateRiskTypeFragment())
            }
        }
        else{
            loadData();
        }

    }

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?,savedInstanceState: Bundle?): View {

        val view: ViewGroup = inflater.inflate(R.layout.fragment_setting_up_risk, container, false) as ViewGroup
        view.setOnTouchListener(this)
        if (!this::currentRisk.isInitialized){
            return view
        }
        val spinnerRiskTypes: Spinner = view.findViewById(R.id.spinnerRiskType)
        val riskTypesNames: ArrayList<kotlin.String> = ArrayList()
        for (riskType in DBHelper.instance.riskTypes) {
            riskTypesNames.add(riskType.name)
        }
        riskTypesNames.add("Добавить")
        val adapterRiskTypes: ArrayAdapter<kotlin.String> = ArrayAdapter(activity!!.baseContext, android.R.layout.simple_spinner_item, riskTypesNames)
        adapterRiskTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerRiskTypes.adapter = adapterRiskTypes

        spinnerRiskTypes.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == DBHelper.instance.riskTypes.size) {
                    val mainActivity = (activity as IFragmented)
                    mainActivity.replaceFragment(CreateRiskTypeFragment())
                }
            }

        }

        setSeekBarChangeListener(R.id.seekBarProbabilityOfOccurrence, R.id.textViewProbabilityOfOccurrence, view)
        setSeekBarChangeListener(R.id.seekBarSeverityAssessment, R.id.textViewSeverityAssessment, view)
        setSeekBarChangeListener(R.id.seekBarProbabilityOfFinding, R.id.textViewProbabilityOfFinding, view)
        val activityRisks: RisksActivity = this.activity as RisksActivity
        if (activityRisks.currentRegistry.model){
            val cardView: CardView = view.findViewById(R.id.cardViewChangedVisible)
            cardView.visibility = View.GONE
        }
        (view.findViewById(R.id.buttonSaveRisk) as? View)?.setOnClickListener(this)
        (view.findViewById(R.id.buttonRiskCreateMinimizationMeasure) as? View)?.setOnClickListener(this)
        if (this.currentRisk.id!=-1){
            setContent(view)
        }
        init(view)
        offViews(view)

        return view
    }

    private fun setSeekBarChangeListener(idSeekBar: Int, idTextView: Int, view: ViewGroup){
        val seekBar = view.findViewById<SeekBar>(idSeekBar)
        val activityRisks: RisksActivity = this.activity as RisksActivity
        seekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {

                //output converted results for users
                var red: Int
                var green: Int

                if (seekBar.progress > 50) {
                    red = 255
                    green = (255 - seekBar.progress * 5.1).toInt()
                }
                else {
                    red = ((seekBar.progress * 5.1).toInt())
                    green = 255
                }

                val textView = view.findViewById<TextView>(idTextView)
                textView.text = String.valueOf(activityRisks.currentRegistry.getTransformedResults(progress).roundToInt())
                textView.setBackgroundColor(Color.rgb(red, green, 0))

                if (idTextView == R.id.textViewProbabilityOfOccurrence)
                    currentRisk.probabilityOfOccurrence = progress.toFloat()
                else if (idTextView == R.id.textViewProbabilityOfFinding)
                    currentRisk.detectionProbabilityEstimate = progress.toFloat()
                else
                    currentRisk.severityAssessment = progress.toFloat()

                currentRisk.calculateMagnitudeOfRisk()

                if (currentRisk.magnitudeOfRisk > 50) {
                    red = 255
                    green = (255 - currentRisk.magnitudeOfRisk * 5.1).toInt()
                }
                else {
                    red = ((currentRisk.magnitudeOfRisk * 5.1).toInt())
                    green = 255
                }
                val charMagnitudeOfRisk: TextView = view.findViewById(R.id.charMagnitudeOfRisk)
                charMagnitudeOfRisk.setBackgroundColor(Color.rgb(red, green, 0))


                if (currentRisk.magnitudeOfRisk<35){
                    charMagnitudeOfRisk.text = "L"
                }
                else if (currentRisk.magnitudeOfRisk<70){
                    charMagnitudeOfRisk.text = "M"
                }
                else{
                    charMagnitudeOfRisk.text = "H"
                }

            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }


    private fun loadData(){
        var id = this.arguments?.getInt("index")
        if (id==null){
            id = -1
        }
        var activityRisk: RisksActivity = this.activity as RisksActivity
        if (id==-1){
            this.currentRisk = Risk(id, activityRisk.currentRegistry, "unnamed", DBHelper.instance.riskTypes[0], 0F,0F,0F)
        }
        else {
            this.currentRisk = DBHelper.instance.risks.find { risk -> risk.id == id }!!
        }
        this.adapter = MinimizationMeasureAdapter(currentRisk.id, false)
    }

    private fun setContent(view: ViewGroup){
        val activityRisks: RisksActivity = this.activity as RisksActivity
        val name = view.findViewById<TextView>(R.id.textViewRiskName)
        val riskType = view.findViewById<Spinner>(R.id.spinnerRiskType)
        val seekBars: Array<SeekBar> = arrayOf(view.findViewById(R.id.seekBarSeverityAssessment), view.findViewById(R.id.seekBarProbabilityOfFinding), view.findViewById(R.id.seekBarProbabilityOfOccurrence))
        val textViews: Array<TextView> = arrayOf(view.findViewById(R.id.textViewSeverityAssessment), view.findViewById(R.id.textViewProbabilityOfFinding), view.findViewById(R.id.textViewProbabilityOfOccurrence))
        val values: Array<Float> = arrayOf(this.currentRisk.severityAssessment, this.currentRisk.detectionProbabilityEstimate, this.currentRisk.probabilityOfOccurrence)

        name.text = this.currentRisk.name
        riskType.setSelection(DBHelper.instance.riskTypes.indexOf(DBHelper.instance.riskTypes.find{riskType -> riskType==this.currentRisk.riskType}))
        for (i in seekBars.indices){
            seekBars[i].progress =  values[i].toInt()
            textViews[i].text = String.valueOf(activityRisks.currentRegistry.getTransformedResults(values[i].toInt()).roundToInt())

            var red: Int
            var green: Int

            if (seekBars[i].progress > 50) {
                red = 255
                green = (255 - seekBars[i].progress * 5.1).toInt()
            }
            else {
                red = ((seekBars[i].progress * 5.1).toInt())
                green = 255
            }
            textViews[i].setBackgroundColor(Color.rgb(red, green, 0))
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onClick(v: View) {
        lateinit var activityFragmented: IFragmented
        if (this.activity is IFragmented) {
            activityFragmented = this.activity as IFragmented
        }
        else{
            return
        }
        when (v.id) {
             R.id.buttonSaveRisk-> {
                currentRisk.name =view?.findViewById<TextView>(R.id.textViewRiskName)?.text.toString().trim()
                val spinnerRiskTypes: Spinner = view!!.findViewById(R.id.spinnerRiskType)
                currentRisk.riskType = DBHelper.instance.riskTypes[spinnerRiskTypes.selectedItemPosition]
                DBHelper.instance.saveRisk(currentRisk)
                val fragment = RisksFragment()
                activityFragmented.replaceFragment(fragment)
            }
            R.id.buttonRiskCreateMinimizationMeasure->{
                if (this.currentRisk.id==-1){
                    Toast.makeText(this.context, "Чтобы добавить предприятия по минимизации - сначала надо сохранить риск :(", Toast.LENGTH_LONG).show()
                    return
                }
                val bundle = Bundle()
                bundle.putInt("index", -1)
                bundle.putInt("indexRisk", this.currentRisk.id)
                val fragment = SettingUpMinimizationMeasureFragment()
                fragment.arguments = bundle
                activityFragmented.replaceFragment(fragment)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init(view: View){
            val rv: RecyclerView = view.findViewById(R.id.rvRiskMinimizationMeasure)
            val manager = LinearLayoutManager(this.requireContext())
            adapter.notifyDataSetChanged()
            rv.adapter = adapter
            rv.layoutManager = manager

    }

    private fun offViews(view: ViewGroup){
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        cal.add(Calendar.MONTH, -1)
        val date = cal.time

        if (date<=this.currentRisk.dateOfCreation) return;

        view.findViewById<TextView>(R.id.textViewRiskName).isEnabled = false
        view.findViewById<Spinner>(R.id.spinnerRiskType).isEnabled = false
        view.findViewById<SeekBar>(R.id.seekBarProbabilityOfOccurrence).isEnabled = false
        view.findViewById<SeekBar>(R.id.seekBarProbabilityOfFinding).isEnabled = false
        view.findViewById<SeekBar>(R.id.seekBarSeverityAssessment).isEnabled = false
        view.findViewById<TextView>(R.id.textViewRiskDegradedMode).visibility = View.VISIBLE
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x = event.x
                y = event.y

            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (abs(x-event.x) > abs(y-event.y) && x-100>=event.x && this.currentRisk.id!=-1){
                    if (this.activity is IFragmented){
                        val bundle = Bundle()
                        bundle.putInt("index", this.currentRisk.id)
                        val fragment = HistoryClosedMinimizationMeasuresFragment()
                        fragment.arguments = bundle
                        (v.context as RisksActivity).replaceFragment(fragment)
                    }
                }
            }
        }

        return true
    }

}