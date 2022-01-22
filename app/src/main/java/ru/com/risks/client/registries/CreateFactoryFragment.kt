package ru.com.risks.client.registries

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import ru.com.risks.R
import ru.com.risks.client.IFragmented
import ru.com.risks.common.DBHelper
import ru.com.risks.common.data.Factory


class CreateFactoryFragment(parent: CreateRegistryFragment) : Fragment(), View.OnClickListener {

    private val parent: Fragment = parent

    override fun onCreateView(inflater: LayoutInflater,container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_create_factory, container, false)
        (view.findViewById(R.id.buttonFactorySave) as? View)?.setOnClickListener(this)
        return view
    }

    override fun onClick(v: View?) {
        val view = this.view ?: return
        val nameET: EditText = view.findViewById(R.id.editTextFactoryName)
        val name: String = nameET.text.toString().trim()
        if (name.isEmpty()) return
        DBHelper.instance.saveFactory(Factory(name))
        if(activity is IFragmented)
        {
            val activityIF: IFragmented = activity as IFragmented
            activityIF.replaceFragment(parent)
        }

    }


}