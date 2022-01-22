package ru.com.risks.client.risks

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.fragment.app.Fragment
import ru.com.risks.R
import ru.com.risks.client.IFragmented
import ru.com.risks.common.DBHelper
import ru.com.risks.common.data.Registry
import ru.com.risks.common.data.Risk
import java.text.SimpleDateFormat

class RisksActivity : AppCompatActivity(), IFragmented {


    var risks: ArrayList<Risk> = ArrayList()
    lateinit var currentRegistry: Registry;


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val registryId = intent.getIntExtra("registryId", -1)

        for (risk in DBHelper.instance.risks){
            if (risk.registry.id==registryId){
                risks.add(risk)
            }
        }


        setContentView(R.layout.activity_risks)
        @SuppressLint("SimpleDateFormat") val format1 = SimpleDateFormat("dd.MM.yyyy")
        val dbHelper = DBHelper.instance
        currentRegistry = dbHelper.registries.find{ registry -> registry.id==registryId }!!
        title = getString(R.string.registry)+ " «" + currentRegistry.factory.name + "» от " + format1.format(currentRegistry.dateOfCreation)

        replaceFragment(RisksFragment())
    }

    override fun replaceFragment(newFragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragmentContainerRisks, newFragment)
        ft.commit()
    }
}