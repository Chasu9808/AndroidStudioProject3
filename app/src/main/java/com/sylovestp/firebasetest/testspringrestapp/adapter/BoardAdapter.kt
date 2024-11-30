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

    fun updateBoards(newBoards: List<BoardDto>) {
        boards = newBoards
        notifyDataSetChanged() // 데이터 변경 시 갱신
    }

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

        fun bind(board: BoardDto) {
            titleTextView.text = board.title
        }
    }
}
