package com.sylovestp.firebasetest.testspringrestapp.paging.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sylovestp.firebasetest.testspringrestapp.R
import com.sylovestp.firebasetest.testspringrestapp.databinding.ItemViewBinding
import com.sylovestp.firebasetest.testspringrestapp.dto.UserItem

class MyViewHolderRetrofit(val binding: ItemViewBinding)
    : RecyclerView.ViewHolder(binding.root)

class MyAdapterRetrofit(val context: Context, val datas: List<UserItem>?)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : RecyclerView.ViewHolder {
        return MyViewHolderRetrofit(
            ItemViewBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val binding = (holder as MyViewHolderRetrofit).binding
        val item = datas?.get(position)
        binding.itemEmail.text = item?.email
        binding.itemUsername.text = item?.username
        binding.itemName.text = item?.name
        binding.itemPhone.text = item?.phone
        binding.itemAddress.text = item?.address

        val imageUrl = "http://10.100.201.87:8080/api/users/${item?.id}/profileImage"
//        val imageUrl = "http://192.168.219.200:8080/api/users/${item?.id}/profileImage"
        Glide.with(context)

            .load(imageUrl)  // profileImageId는 URL 또는 리소스 ID일 수 있습니다
            .override(300,300)
            .placeholder(R.drawable.test) // 이미지 로드 중에 표시할 플레이스홀더
            .error(R.drawable.user_basic) // 로드 실패 시 표시할 이미지
            .into(binding.itemProfileImage)
    }

}








