package com.timmymike.githubapi_codetask.mvvm

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.timmymike.githubapi_codetask.api.UserSearchModel
import com.timmymike.githubapi_codetask.databinding.AdapterUserListBinding
import com.timmymike.githubapi_codetask.tools.bindImage

class UserAdapter(val viewModel: MainViewModel) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    var list: ArrayList<UserSearchModel.Item>? = viewModel.listLiveData.value
    var slidePosition = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list != null && list!!.isNotEmpty()) {
            val item = list!![position]
            holder.bind(viewModel, item)
        }
        slidePosition = position
    }

    override fun getItemCount(): Int {
        return list?.count() ?: 0
    }

    fun getNowSlidePostion() = slidePosition

    class ViewHolder private constructor(private val binding: AdapterUserListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: MainViewModel, item: UserSearchModel.Item) {
            binding.viewModel = viewModel
            binding.userModel = item

            bindImage(binding.ivAvatar, item.avatarUrl)

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AdapterUserListBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }

}