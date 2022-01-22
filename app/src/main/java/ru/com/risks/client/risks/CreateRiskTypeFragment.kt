package ru.com.risks.client.risks

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import ru.com.risks.R
import ru.com.risks.client.IFragmented
import ru.com.risks.common.DBHelper
import ru.com.risks.common.data.RiskType

class CreateRiskTypeFragment : Fragment(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?,): View {
        val view: ViewGroup = inflater.inflate(R.layout.fragment_create_risk_type, container, false) as ViewGroup

        (view.findViewById(R.id.buttonRiskTypeBack) as? View)?.setOnClickListener(this)
        (view.findViewById(R.id.buttonRiskTypeSave) as? View)?.setOnClickListener(this)
        return view
    }


    override fun onClick(v: View?) {
        if (v==null) return
        if (v.id== R.id.buttonRiskTypeBack){
            if (this.activity is IFragmented){
                val activityFragmented: IFragmented = this.activity as IFragmented
                activityFragmented.replaceFragment(SettingUpRiskFragment())
            }
        }
        else if(v.id == R.id.buttonRiskTypeSave){
            val name: String = view?.findViewById<TextView>(R.id.textViewRiskTypeName)?.text.toString().trim()
            var value: Int = -1
            if (view?.findViewById<TextView>(R.id.textViewRiskTypeName)?.text.toString().trim().isNotEmpty()) {
                value =view?.findViewById<TextView>(R.id.textViewRiskTypeValue)?.text.toString().trim()
                    .toInt()
            }
            if (name.isEmpty()){
                return
            }
            if (value==-1){
                return
            }
            DBHelper.instance.saveRiskType(RiskType(-1, name, value))
            if (this.activity is IFragmented){
                val activityFragmented: IFragmented = this.activity as IFragmented
                activityFragmented.replaceFragment(SettingUpRiskFragment())
            }
        }
    }


}