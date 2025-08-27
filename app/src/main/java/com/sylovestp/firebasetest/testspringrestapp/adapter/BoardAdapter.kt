package com.sylovestp.firebasetest.testspringrestapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.dto.BoardDto

class BoardAdapter(
    private var boards: List<BoardDto>,
    private val onItemClick: (Long) -> Unit
) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_board, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val board = boards[position]
        holder.bind(board)
        holder.itemView.setOnClickListener { onItemClick(board.id ?: -1L) }
    }

    override fun getItemCount(): Int = boards.size

    class BoardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.boardTitleTextView)
        private val writerTextView: TextView = itemView.findViewById(R.id.boardWriterTextView)
        private val contentTextView: TextView = itemView.findViewById(R.id.boardContentTextView)

        fun bind(board: BoardDto) {
            titleTextView.text = board.title
            writerTextView.text = "작성자: ${board.writer}"
            contentTextView.text = board.boardContent
        }
    }
}
