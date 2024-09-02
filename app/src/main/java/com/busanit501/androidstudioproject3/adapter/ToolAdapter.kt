package com.busanit501.androidstudioproject3.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.busanit501.androidstudioproject3.R
import com.busanit501.androidstudioproject3.data.Tool
import com.busanit501.androidstudioproject3.ToolDetailActivity

class ToolAdapter(
    private val toolList: List<Tool>,
    private val context: Context
) : RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tool, parent, false)
        return ToolViewHolder(view)
    }

    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val tool = toolList[position]
        holder.nameTextView.text = tool.name
        holder.shortDescriptionTextView.text = tool.shortDescription

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ToolDetailActivity::class.java).apply {
                putExtra("tool_id", tool.id)
                putExtra("tool_name", tool.name)
                putExtra("tool_description", tool.detailedDescription)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = toolList.size

    class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val shortDescriptionTextView: TextView = itemView.findViewById(R.id.shortDescriptionTextView)
    }
}