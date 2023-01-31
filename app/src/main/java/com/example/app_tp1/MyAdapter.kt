package com.example.app_tp1

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class MyAdapter(private val list:ArrayList<String>, private val context:Context) : BaseAdapter() {
    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): Any {
        return list.get(p0)
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutItem: ConstraintLayout?
        val mInflater:LayoutInflater = LayoutInflater.from(context)

        layoutItem = if(convertView == null) {
            mInflater.inflate(R.layout.item_layout, parent, false) as ConstraintLayout
        }else{
            convertView as ConstraintLayout
        }

        var viewHolder: ViewHolder? = layoutItem.tag as? ViewHolder
        if(viewHolder == null){
            viewHolder = ViewHolder()
            viewHolder.tv = layoutItem.findViewById(R.id.textView3)
            layoutItem.tag = viewHolder
        }

        val trainCourant:String = list[position]
        viewHolder.tv?.text = trainCourant

        return layoutItem
    }

    private class ViewHolder(var tv:TextView?){
        constructor():this(
            tv=null
        )
    }
}