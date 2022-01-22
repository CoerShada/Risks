package ru.com.risks.client.risks

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.com.risks.R
import ru.com.risks.client.IFragmented
import ru.com.risks.client.rv.RegistryAdapter
import ru.com.risks.client.rv.RiskAdapter
import ru.com.risks.common.DBHelper


class RisksFragment : Fragment(), View.OnClickListener {
    private lateinit var activityRisks: RisksActivity
    private lateinit var adapter: RiskAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        this.activityRisks = this.activity as RisksActivity
        adapter = RiskAdapter(activityRisks.risks)
        (view.findViewById(R.id.buttonAdd) as? View)?.setOnClickListener(this)
        init(view)
        return view
    }

    override fun onClick(v: View?) {
        if (v==null) return
        if (v.id == R.id.buttonAdd){
            if (activity is IFragmented){
                val bundle = Bundle()
                bundle.putInt("index", -1)
                val fragment = SettingUpRiskFragment()
                fragment.arguments = bundle
                this.activityRisks.replaceFragment(fragment)

            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun init(view: View){
        val rv: RecyclerView = view.findViewById(R.id.rvMain)
        val manager = LinearLayoutManager(this.requireContext())
        this.activityRisks.risks = DBHelper.instance.risks
        adapter.risks = this.activityRisks.risks
        adapter.notifyDataSetChanged()
        rv.adapter = adapter
        rv.layoutManager = manager

    }

}