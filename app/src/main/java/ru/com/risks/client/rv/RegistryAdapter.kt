package ru.com.risks.client.rv

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.com.risks.R
import ru.com.risks.client.risks.RisksActivity
import ru.com.risks.common.DBHelper
import ru.com.risks.common.data.Registry
import ru.com.risks.databinding.RvRegistryBinding
import java.text.SimpleDateFormat

class RegistryAdapter: RecyclerView.Adapter<RegistryAdapter.RegistryHolder>() {

    class RegistryHolder(view: View):RecyclerView.ViewHolder(view), View.OnClickListener {
        private val binding = RvRegistryBinding.bind(view)

        init {
            view.setOnClickListener(this)
        }

        @SuppressLint("SimpleDateFormat")
        fun bind(registry: Registry){
            val name: String = registry.factory.name
            binding.textViewCardFactoryName.text = name
            binding.textViewCardDate.text =SimpleDateFormat("dd.MM.yyyy").format(registry.dateOfCreation)
        }

        override fun onClick(v: View) {
            val intent = Intent(v.context, RisksActivity::class.java)
            intent.putExtra("registryId", DBHelper.instance.registries[adapterPosition].id)
            v.context.startActivity(intent)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RegistryHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.rv_registry, parent, false)
        return RegistryHolder(view)
    }

    override fun onBindViewHolder(holder: RegistryHolder, position: Int) {
        holder.bind(DBHelper.instance.registries[position])
    }

    override fun getItemCount(): Int {
        return DBHelper.instance.registries.size
    }


}


