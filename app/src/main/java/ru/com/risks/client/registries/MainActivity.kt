package ru.com.risks.client.registries

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import ru.com.risks.R
import ru.com.risks.client.IFragmented
import ru.com.risks.common.DBHelper

class MainActivity : AppCompatActivity(), IFragmented {

    companion object {
        private var dbHelper: DBHelper? = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (dbHelper ==null)
            dbHelper = DBHelper(this)
        setContentView(R.layout.activity_main)
        replaceFragment(RegistriesFragment())
        title = getString(R.string.registries)
    }

    override fun replaceFragment(newFragment: Fragment) {
        val ft = supportFragmentManager.beginTransaction()
        ft.replace(R.id.fragmentContainerRegistries, newFragment)
        ft.commit()
    }


}