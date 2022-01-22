package ru.com.risks.client.registries

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import ru.com.risks.R
import ru.com.risks.client.IFragmented
import ru.com.risks.client.risks.RisksActivity
import ru.com.risks.common.DBHelper
import ru.com.risks.common.data.Factory
import ru.com.risks.common.data.Registry
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList


class CreateRegistryFragment : Fragment(), View.OnClickListener {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View {
        val view: View = inflater.inflate(R.layout.fragment_create_registry, container, false)

        val spinnerFactories: Spinner = view.findViewById(R.id.spinner_factories)
        val factoriesNames: ArrayList<String> = ArrayList()
        for (factory in DBHelper.instance.factories) {
            factoriesNames.add(factory.name)
        }
        factoriesNames.add("Добавить")
        val adapterFactories: ArrayAdapter<String> =ArrayAdapter(activity!!.baseContext, android.R.layout.simple_spinner_item, factoriesNames)
        adapterFactories.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFactories.adapter = adapterFactories
        if (factoriesNames.size==1) {
            val mainActivity = (activity as IFragmented)
            mainActivity.replaceFragment(CreateFactoryFragment(this))
        }

        spinnerFactories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == DBHelper.instance.factories.size) {
                    val mainActivity = (activity as IFragmented)
                    mainActivity.replaceFragment(CreateFactoryFragment(this@CreateRegistryFragment))
                }
            }

        }

        (view.findViewById(R.id.buttonRegistrySave) as? View)?.setOnClickListener(this)
        (view.findViewById(R.id.buttonRegistryBack) as? View)?.setOnClickListener(this)
        (view.findViewById(R.id.switch_model) as? View)?.setOnClickListener(this)
        return view
    }

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onClick(v: View?) {
        val view = this.view ?: return
        if (v==null) return
        if (v.id == R.id.buttonRegistrySave) {
            try {

                val spinnerFactories = view.findViewById<View>(R.id.spinner_factories) as Spinner
                val models = view.findViewById<View>(R.id.switch_model) as Switch
                val editFrom: EditText = view.findViewById(R.id.editTextNumberDecimalFrom)
                val editTo: EditText = view.findViewById(R.id.editTextNumberDecimalTo)

                val factory: Factory =DBHelper.instance.factories[spinnerFactories.selectedItemPosition]
                val model: Boolean = models.isChecked
                val from: Float = editFrom.text.trim().toString().toFloat()
                val to: Float = editTo.text.trim().toString().toFloat()

                val registry = Registry(-1, factory, model, from, to)
                if (DBHelper.instance.registries.find{registryFind->registryFind.factory==registry.factory}!=null){
                    Toast.makeText(this.context, "Нельзя создать два реестра которые отражают риски на одном предприятии :(", Toast.LENGTH_LONG).show()
                    return
                }
                DBHelper.instance.saveRegistry(registry)
                val intent = Intent(this.context, RisksActivity::class.java)
                intent.putExtra("registryId", DBHelper.instance.registries.last().id)
                startActivity(intent)
            }
            catch (e:Exception){
                val toast = Toast.makeText(view.context,"Ошибка сохранения, пожалуйста проверьте правильность введенных данных.",Toast.LENGTH_LONG)

                toast.show()
            }
        }
        else if (v.id == R.id.buttonRegistryBack) {
            if(activity is IFragmented)
            {
                val activityIF: IFragmented = activity as IFragmented
                activityIF.replaceFragment(RegistriesFragment())
            }
        }
        else if(v.id == R.id.switch_model){
            val switch: Switch = v as Switch;
            if(switch.isChecked){
                switch.text = getString(R.string.three_factors_model)
            }
            else{
                switch.text = getString(R.string.two_factors_model)

            }
        }
    }

}