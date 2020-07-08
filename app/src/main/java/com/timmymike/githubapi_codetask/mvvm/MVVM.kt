package com.timmymike.githubapi_codetask.mvvm

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.timmymike.githubapi_codetask.api.ApiConnect
import com.timmymike.githubapi_codetask.api.UserSearchModel
import com.timmymike.githubapi_codetask.databinding.AdapterUserListBinding
import com.timmymike.githubapi_codetask.tools.bindImage
import com.timmymike.githubapi_codetask.tools.logi
import com.timmymike.githubapi_codetask.tools.logiAllData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**======== MVVM ========*/

class MainViewModel(val context: Context) : ViewModel() {
    val TAG = javaClass.simpleName
    val listLiveData: MutableLiveData<ArrayList<UserSearchModel.Item>> by lazy { MutableLiveData<ArrayList<UserSearchModel.Item>>() }
    val liveLoadingOver: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() } // According this value To Show now Status

    init {
        liveLoadingOver.postValue(true) // default close the progressBar
    }

    fun searchData(search: String?) {
        logi(TAG, "收到的searchString是===>$search")
        if (search != null && search != "") {
            val list = ArrayList<UserSearchModel.Item>()
            liveLoadingOver.postValue(false)
            GlobalScope.launch {
                try {


                    list.clear()
                    list.getFromApiSearchData(search)

                    listLiveData.postValue(list)

                } catch (e: Exception) {
                    Toast.makeText(context, "getDataFail，because:${e.message}", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                }finally {
                    liveLoadingOver.postValue(true)
                }
            }
        } else {
            Toast.makeText(context, "Please enter a string at first,then press search button.", Toast.LENGTH_SHORT).show()
        }
    }

    fun openItem(itemName: String) {
        Toast.makeText(context, "You clicked $itemName", Toast.LENGTH_SHORT).show()
    }

    @Throws(Exception::class)
    private fun ArrayList<UserSearchModel.Item>.getFromApiSearchData(search: String) {
        val cell = ApiConnect.getService(context).getSearchData(search)

        logi(TAG, "Start Call API,To Get getFromApiSearchData Method")

        val response = cell.execute()
        logi(TAG, "getFromApiSearchData Send Data is===>${response ?: "null"}")
        if (response.isSuccessful) {
            logi(TAG, "getFromApiSearchData Get Data is Below,total ${response?.body()?.items?.size ?: 0} count")

            response?.body()?.items?.logiAllData()
            this.addAll(response?.body()?.items ?: mutableListOf())

        }
        response?.body().toString()

        return
    }

}

class ViewModelFactory(private val context: Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


class UserAdapter(val viewModel: MainViewModel) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {
    var list: ArrayList< UserSearchModel.Item>? = viewModel.listLiveData.value

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list != null && list!!.isNotEmpty()) {
            val item = list!![position]
            holder.bind(viewModel, item)
        }
    }

    override fun getItemCount(): Int {
        return list?.count() ?: 0
    }

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