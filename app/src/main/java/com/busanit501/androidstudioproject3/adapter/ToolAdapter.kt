package com.busanit501.androidstudioproject3.adapter

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
import com.bumptech.glide.Glide
import com.busanit501.androidstudioproject3.R
import com.busanit501.androidstudioproject3.dto.Tool
import com.busanit501.androidstudioproject3.ToolDetailActivity

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
        holder.shortDescriptionTextView.text = tool.description
        holder.imgTextView.text = tool.imgText
        holder.regDateTextView.text = tool.regDate
        holder.modDateTextView.text = tool.modDate

        val imageUrl = "http://10.100.201.26:8080/images/"+ tool.imageFileName

        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .placeholder(R.drawable.img) // 로딩 중 표시할 이미지
            .error(R.drawable.error_img)
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, ToolDetailActivity::class.java).apply {
                putExtra("imageFileName", tool.imageFileName)
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