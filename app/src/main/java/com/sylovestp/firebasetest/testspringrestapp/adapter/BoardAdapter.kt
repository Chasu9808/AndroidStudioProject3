package com.sylovestp.firebasetest.testspringrestapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sylovestp.firebasetest.testspringrestapp.dto.BoardDto

class BoardAdapter(
    private var boards: List<BoardDto>,
    private val onBoardClick: (Long) -> Unit // 게시글 클릭 이벤트 처리
) : RecyclerView.Adapter<BoardAdapter.BoardViewHolder>() {

    class BoardViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.boardTitleTextView)
        val writer: TextView = view.findViewById(R.id.boardWriterTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BoardViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_board, parent, false)
        return BoardViewHolder(view)
    }

    override fun onBindViewHolder(holder: BoardViewHolder, position: Int) {
        val board = boards[position]
        holder.title.text = board.title
        holder.writer.text = board.writer
        holder.itemView.setOnClickListener {
            board.id?.let { onBoardClick(it) }
        }
    }

    override fun getItemCount(): Int = boards.size

    // 새로운 데이터를 설정하는 메서드
    fun updateBoards(newBoards: List<BoardDto>) {
        this.boards = newBoards
        notifyDataSetChanged()
    }
}
