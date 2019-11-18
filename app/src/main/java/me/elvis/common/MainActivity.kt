package me.elvis.common

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

class MainActivity : AppCompatActivity() {

    private val datas = mutableListOf("1", "2", "3")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    inner class MyAdapter : CommonAdapter<String>(this@MainActivity, R.layout.item, datas) {
        override fun convert(holder: ViewHolder, t: String, position: Int) {
            holder.setText(R.id.tv, t)
        }

    }
}
