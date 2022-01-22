package ru.com.risks.client.registries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

import androidx.recyclerview.widget.LinearLayoutManager

import ru.com.risks.client.rv.RegistryAdapter


import androidx.recyclerview.widget.RecyclerView
import ru.com.risks.R
import ru.com.risks.client.IFragmented


class RegistriesFragment() : Fragment() , View.OnClickListener{


    private val adapter = RegistryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

    }


    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val view: ViewGroup = inflater.inflate(R.layout.fragment_list, container, false) as ViewGroup
        (view.findViewById(R.id.buttonAdd) as? View)?.setOnClickListener(this)


        init(view)
        return view
    }


    override fun onClick(v: View?) {
        if (v==null) return
        if (v.id == R.id.buttonAdd){
            if (activity is MainActivity){
                val mainActivity = (activity as IFragmented)
                mainActivity.replaceFragment(CreateRegistryFragment())

            }
        }
    }

    private fun init(view: View){
        val rv: RecyclerView = view.findViewById(R.id.rvMain)
        val manager = LinearLayoutManager(this.requireContext())
        rv.adapter = adapter
        rv.layoutManager = manager
    }
}
