package com.sylovestp.firebasetest.testspringrestapp.paging.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.ItemViewBinding
import com.sylovestp.firebasetest.testspringrestapp.dto.UserItem

class UserAdapter : PagingDataAdapter<UserItem, UserAdapter.UserViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = ItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        if (user != null) {
            holder.bind(user)
        }
    }

    class UserViewHolder(private val binding: ItemViewBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(user: UserItem) {
            binding.itemUsername.text = user.username
            binding.itemName.text = user.name
            binding.itemEmail.text = user.email
            binding.itemPhone.text = user.phone
            binding.itemAddress.text = user.address

            val imageUrl = "http://10.100.201.87:8080/api/users/${user.id}/profileImage"
//            val imageUrl = "http://192.168.219.200:8080/api/users/${user?.id}/profileImage"
            Glide.with(binding.root.context)
                .load(imageUrl)
                .placeholder(R.drawable.user_basic)
                .into(binding.itemProfileImage)
        }
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<UserItem>() {
            override fun areItemsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: UserItem, newItem: UserItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}