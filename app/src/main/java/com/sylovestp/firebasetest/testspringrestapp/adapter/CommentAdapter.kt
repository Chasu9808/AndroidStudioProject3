package com.sylovestp.firebasetest.testspringrestapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sylovestp.firebasetest.testspringrestapp.dto.CommentDto

class CommentAdapter(
    private val comments: List<CommentDto>,
    private val onItemClick: (Long, String, String) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val writer: TextView = view.findViewById(R.id.commentWriter)
        val content: TextView = view.findViewById(R.id.commentContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_comment, parent, false)
        return CommentViewHolder(view)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        holder.writer.text = comment.writer ?: "Unknown"
        holder.content.text = comment.content2
        holder.itemView.setOnClickListener {
            onItemClick(comment.id ?: -1L, comment.writer ?: "Unknown", comment.content2)
        }
    }

    override fun getItemCount(): Int = comments.size
}
