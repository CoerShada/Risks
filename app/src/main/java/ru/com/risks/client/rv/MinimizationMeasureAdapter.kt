package ru.com.risks.client.rv

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.com.risks.R
import ru.com.risks.client.risks.RisksActivity
import ru.com.risks.client.risks.SettingUpMinimizationMeasureFragment
import ru.com.risks.client.risks.SettingUpRiskFragment
import ru.com.risks.common.DBHelper
import ru.com.risks.common.data.MinimizationMeasure
import ru.com.risks.common.data.Risk
import ru.com.risks.databinding.RvMinimizationMeasureBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MinimizationMeasureAdapter(riskId: Int, findClosed: Boolean): RecyclerView.Adapter<MinimizationMeasureAdapter.RegistryHolder>() {
    var minimizationMeasures: ArrayList<MinimizationMeasure> = ArrayList()
    init{
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, 1)
        cal.add(Calendar.MONTH, -1)
        var date1 = cal.time
        for (minimizationMeasure: MinimizationMeasure in DBHelper.instance.minimizationMeasures){
            if (minimizationMeasure.risk.id == riskId)
                if (findClosed && minimizationMeasure.closed && date1>=minimizationMeasure.date) {
                    minimizationMeasures.add(minimizationMeasure)
                }
                else if (!findClosed && (!minimizationMeasure.closed || (minimizationMeasure.closed && date1<minimizationMeasure.date))){
                    minimizationMeasures.add(minimizationMeasure)
                }

        }
    }

    class RegistryHolder(view: View, minimizationMeasures: ArrayList<MinimizationMeasure>): RecyclerView.ViewHolder(view), View.OnClickListener {
        private val binding = RvMinimizationMeasureBinding.bind(view)
        private val minimizationMeasures: ArrayList<MinimizationMeasure>
        init {
            view.setOnClickListener(this)
            this.minimizationMeasures = minimizationMeasures
        }

        @SuppressLint("SimpleDateFormat")
        fun bind(minimizationMeasure: MinimizationMeasure){
            val name: String = minimizationMeasure.name
            val responsible: String = minimizationMeasure.responsible
            val date: String = SimpleDateFormat("dd.MM.yyyy").format(minimizationMeasure.date)
            binding.textViewCardMinimizationMeasureName.text = name
            binding.textViewCardMinimizationMeasureResponsible.text = responsible
            binding.textViewCardMinimizationMeasureDate.text = date
            if (minimizationMeasure.closed)
                binding.imageViewRvMinimizationMeasureClosed.visibility = View.VISIBLE
        }

        override fun onClick(v: View) {

            val bundle = Bundle()
            bundle.putInt("index", minimizationMeasures[adapterPosition].id)
            val fragment = SettingUpMinimizationMeasureFragment()
            fragment.arguments = bundle
            (v.context as RisksActivity).replaceFragment(fragment)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_minimization_measure, parent, false)
        return RegistryHolder(view, minimizationMeasures)
    }

    override fun onBindViewHolder(holder: RegistryHolder, position: Int) {
        holder.bind(minimizationMeasures[position])
    }

    override fun getItemCount(): Int {
        return minimizationMeasures.size
    }


}