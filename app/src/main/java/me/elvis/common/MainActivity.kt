package me.elvis.common

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val datas = mutableListOf("1", "2", "3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btn.setOnClickListener {
            tv.setText("${tv.text}123asd")
        }
        list.layoutManager = LinearLayoutManager(this)
        val datas = mutableListOf<String>()
        for (i in 1..50) {
            datas.add(i.toString())
        }
//        list.adapter=MyAdapter()
    }


    inner class MyAdapter : CommonAdapter<String>(this@MainActivity, R.layout.item, datas) {
        override fun convert(holder: ViewHolder, t: String, position: Int) {
        }

    }
}
