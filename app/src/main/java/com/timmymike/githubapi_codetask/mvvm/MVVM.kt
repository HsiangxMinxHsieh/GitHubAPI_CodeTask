package com.timmymike.githubapi_codetask.mvvm

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
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
import org.json.JSONObject

/**======== MVVM ========*/

class MainViewModel(val context: Context) : ViewModel() {
    val TAG = javaClass.simpleName
    val listLiveData: MutableLiveData<ArrayList<UserSearchModel.Item>> by lazy { MutableLiveData<ArrayList<UserSearchModel.Item>>() }
    val liveLoadingOver: MutableLiveData<Boolean> by lazy { MutableLiveData<Boolean>() } // According this value To Show now Status
    var searchString = ""

    init {
        liveLoadingOver.postValue(true) // default close the progressBar
    }

    fun searchData(search: String?) {
        logi(TAG, "the search string get is===>$search")

        //close the input keyboad
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as (InputMethodManager))
            .hideSoftInputFromWindow((context as Activity).window.decorView.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)

        if (search != null && search != "") {
            searchString = search

            val list = ArrayList<UserSearchModel.Item>()
            liveLoadingOver.postValue(false)
            GlobalScope.launch {
                try {
                    //when search,mean new String to search ,so need clean before data
                    list.clear()

                    list.getFromApiSearchData( search)

                    listLiveData.postValue(list)

                } catch (e: Exception) {
                    Toast.makeText(context, "getDataFail，because:${e.message}", Toast.LENGTH_SHORT).show()
                    e.printStackTrace()
                } finally {
                    liveLoadingOver.postValue(true)
                }
            }
        } else {
            Toast.makeText(context, "Please enter a string at first,then press search button.", Toast.LENGTH_SHORT).show()
        }
    }

    @Throws(Exception::class)
    fun ArrayList<UserSearchModel.Item>.getFromApiSearchData( search: String, nowGetPage: Int = 1, TAG: String = "getFromApiSearchData") {
        val cell = ApiConnect.getService(context).getSearchData(search, nowGetPage)

        logi(TAG, "Start Call API,To Get getFromApiSearchData Method")

        val response = cell.execute()
        logi(TAG, "getFromApiSearchData Send Data is===>${response ?: "null"}")
        if (response.isSuccessful) {
            // debug check log
            logi(TAG, "getFromApiSearchData Get Data is Below,total ${response?.body()?.items?.size ?: 0} count")
//            response?.body()?.items?.logiAllData()
            this.addAll(response?.body()?.items ?: mutableListOf())

        } else {
            Handler(Looper.getMainLooper()).post {
                //normal is 403 (rate limit error)
                val errorJson = JSONObject(response.errorBody()?.string().toString())
                Toast.makeText(context, "getDataFail，because:${errorJson.optString("message","Other error can't get Message")}", Toast.LENGTH_SHORT).show()
            }
        }
        response?.body().toString()

        return
    }

    fun clickItem(itemName: String) {
        Toast.makeText(context, "You clicked $itemName", Toast.LENGTH_SHORT).show()
    }

    fun reGetData(recyclerView: RecyclerView, nowGetPage: Int) {
        liveLoadingOver.postValue(false)
        GlobalScope.launch {
            val list = ArrayList<UserSearchModel.Item>()
            list.addAll(listLiveData.value ?: mutableListOf())
            list.getFromApiSearchData(searchString, nowGetPage)
            listLiveData.postValue(list)
            liveLoadingOver.postValue(true)
            Handler(Looper.getMainLooper()).post {
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
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