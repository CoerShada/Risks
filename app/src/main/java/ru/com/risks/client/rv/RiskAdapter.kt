package ru.com.risks.client.rv

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.com.risks.R
import ru.com.risks.client.risks.RisksActivity
import ru.com.risks.client.risks.SettingUpRiskFragment
import ru.com.risks.common.DBHelper
import ru.com.risks.common.data.Risk
import ru.com.risks.databinding.RvRiskBinding
import java.text.SimpleDateFormat
import android.os.Bundle
import java.util.*
import kotlin.collections.ArrayList


class RiskAdapter(var risks: ArrayList<Risk>) : RecyclerView.Adapter<RiskAdapter.RiskHolder>() {


    class RiskHolder(view: View): RecyclerView.ViewHolder(view), View.OnClickListener {
        private val binding = RvRiskBinding.bind(view)

        init {
            view.setOnClickListener(this)
        }

        @SuppressLint("SimpleDateFormat")
        fun bind(risk: Risk){
            val cal = Calendar.getInstance()
            cal.add(Calendar.DATE, 1)
            cal.add(Calendar.MONTH, -1)
            val currentDate = cal.time
            binding.textViewCardRiskName.text = risk.name
            binding.textViewCardRiskDate.text = SimpleDateFormat("dd.MM.yyyy").format(risk.dateOfCreation)
            when {
                risk.magnitudeOfRisk<30 -> {
                    binding.textViewCardRiskPriority.text = "L"
                }
                risk.magnitudeOfRisk<70 -> {
                    binding.textViewCardRiskPriority.text = "M"
                }
                else -> {
                    binding.textViewCardRiskPriority.text = "H"
                }
            }


            //output converted results for users
            val red: Int
            val green: Int

            if (risk.magnitudeOfRisk > 50) {
                red = 255
                green = (255 - risk.magnitudeOfRisk * 5.1).toInt()
            } else {
                red = (risk.magnitudeOfRisk * 5.1).toInt()
                green = 255
            }

            binding.cardRiskCardColor.setCardBackgroundColor(Color.rgb(red, green, 0))
            if (risk.dateOfCreation>=currentDate){
                binding.imageViewRvRiskClosed.visibility = View.GONE
            }
        }

        override fun onClick(v: View) {
            val activity: RisksActivity = v.context as RisksActivity
            val bundle = Bundle()
            bundle.putInt("index", activity.risks[adapterPosition].id)
            val fragment = SettingUpRiskFragment()
            fragment.arguments = bundle
            activity.replaceFragment(fragment)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RiskHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_risk, parent, false)
        return RiskHolder(view)
    }

    override fun onBindViewHolder(holder: RiskHolder, position: Int) {
        holder.bind(risks[position])
    }

    override fun getItemCount(): Int {
        return risks.size
    }
}