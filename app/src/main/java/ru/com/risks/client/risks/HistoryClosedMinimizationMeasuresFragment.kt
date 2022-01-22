package ru.com.risks.client.risks

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import ru.com.risks.R
import ru.com.risks.client.IFragmented
import ru.com.risks.client.rv.MinimizationMeasureAdapter
import ru.com.risks.client.rv.RiskAdapter
import ru.com.risks.common.DBHelper
import ru.com.risks.common.data.Risk
import kotlin.math.abs

class HistoryClosedMinimizationMeasuresFragment: Fragment(), View.OnTouchListener {
    private lateinit var adapter: MinimizationMeasureAdapter
    private lateinit var currentRisk: Risk

    var x = 0F
    var y = 0F

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadData()
    }

    @SuppressLint("ResourceAsColor", "ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        view.setOnTouchListener(this)
        view.findViewById<FloatingActionButton>(R.id.buttonAdd).visibility = View.GONE
        view.findViewById<RecyclerView>(R.id.rvMain).setOnTouchListener(this)
        view.setBackgroundColor(resources.getColor(R.color.light_gray))

        init(view)
        return view
    }


    override fun onTouch(v: View, event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                x = event.x
                y = event.y

            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (abs(x-event.x) > abs(y-event.y) && x+100<=event.x){
                    if (this.activity is IFragmented){
                        val bundle = Bundle()
                        bundle.putInt("index", this.currentRisk.id)
                        val fragment = SettingUpRiskFragment()
                        fragment.arguments = bundle
                        (v.context as RisksActivity).replaceFragment(fragment)
                    }
                }
            }
        }

        return true
    }

    private fun loadData(){
        val id = this.arguments?.getInt("index")
        this.currentRisk = DBHelper.instance.risks.find { risk -> risk.id == id }!!
        this.adapter = MinimizationMeasureAdapter(currentRisk.id, true)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init(view: View){
        val rv: RecyclerView = view.findViewById(R.id.rvMain)
        val manager = LinearLayoutManager(this.requireContext())
        adapter.notifyDataSetChanged()
        rv.adapter = adapter
        rv.layoutManager = manager

    }
}