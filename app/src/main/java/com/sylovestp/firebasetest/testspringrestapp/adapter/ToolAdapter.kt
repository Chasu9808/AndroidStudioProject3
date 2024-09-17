package com.sylovestp.firebasetest.testspringrestapp.adapter

import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.sylovestp.firebasetest.testspringrestapp.dto.Tool
import com.sylovestp.firebasetest.testspringrestapp.ToolDetailActivity
import com.sylovestp.firebasetest.testspringrestapp.R

class ToolAdapter(
    private val toolList: List<Tool>,
    private val context: Context
) : RecyclerView.Adapter<ToolAdapter.ToolViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToolViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_tool, parent, false)
        return ToolViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ToolViewHolder, position: Int) {
        val tool = toolList[position]
        holder.nameTextView.text = tool.toolName
        val maxLength = 20
        val description = if (tool.description.length > maxLength) {
            tool.description.substring(0, maxLength) + "..."
        } else {
            tool.description
        }
        holder.shortDescriptionTextView.text = description
        holder.imgTextView.text = tool.imgText
        holder.regDateTextView.text = tool.regDate
        holder.modDateTextView.text = tool.modDate

        val imageName = when (tool.toolName) {
            "망치" -> R.drawable.hammer
            "니퍼" -> R.drawable.nipper
            "줄자" -> R.drawable.tape_measure
            "그라인더" -> R.drawable.grinder
            "드라이버" -> R.drawable.screwdriver
            "전동드릴" -> R.drawable.drill
            "스패너" -> R.drawable.spanner
            "공업가위" -> R.drawable.scissors
            "톱" -> R.drawable.saw
            "캘리퍼스" -> R.drawable.vernier_calipers
            else -> R.drawable.ic_launcher_foreground
        }

        holder.imageView.setImageResource(imageName)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ToolDetailActivity::class.java).apply {

                putExtra("tool_id", tool.id)
                putExtra("tool_name", tool.toolName)
                putExtra("tool_description", tool.description)
                putExtra("regDate", tool.regDate)
                putExtra("modDate", tool.modDate)
                putExtra("img_text", tool.imgText)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = toolList.size

    class ToolViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.toolImageView)
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val shortDescriptionTextView: TextView = itemView.findViewById(R.id.shortDescriptionTextView)
        val imgTextView: TextView = itemView.findViewById(R.id.imgTextView)
        val regDateTextView: TextView = itemView.findViewById(R.id.regDateTextView)
        val modDateTextView: TextView = itemView.findViewById(R.id.modDateTextView)
    }
}