package com.timmymike.githubapi_codetask

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.timmymike.githubapi_codetask.api.UserSearchModel
import com.timmymike.githubapi_codetask.databinding.ActivityMainBinding
import com.timmymike.githubapi_codetask.mvvm.MainViewModel
import com.timmymike.githubapi_codetask.mvvm.UserAdapter
import com.timmymike.githubapi_codetask.mvvm.ViewModelFactory
import com.timmymike.githubapi_codetask.tools.logi

class MainActivity : AppCompatActivity() {

    val TAG = javaClass.simpleName

    private val context: Context = this
    private val activity = this

    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var adapter: UserAdapter
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = DataBindingUtil.setContentView(activity, R.layout.activity_main)
//        setContentView(R.layout.activity_main)
        initMvvm()

        initView()

        initEvent()

        initObeserver()
    }

    private fun initMvvm() {
        viewModel = ViewModelProvider(
            this,
            ViewModelFactory(context)
        ).get(MainViewModel::class.java)

        mainBinding.viewModel = viewModel
        mainBinding.lifecycleOwner = activity
    }


    private fun initView() {


        mainBinding.rvUserList.layoutManager = LinearLayoutManager(context).apply {
            orientation = RecyclerView.VERTICAL
        }

        adapter = UserAdapter(viewModel)
        mainBinding.rvUserList.adapter = adapter

    }

    private fun initEvent() {
        //Listen Scroll Event
        mainBinding.rvUserList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 && recyclerView.computeVerticalScrollOffset() > 0 && !recyclerView.canScrollVertically(1)) { //
                    val position = (recyclerView.adapter as UserAdapter).getNowSlidePostion()
                    viewModel.reGetData( position / 30 + 2)
                }
            }
        })
    }

    private fun initObeserver() {
        viewModel.listLiveData.observe(this,
            Observer<ArrayList<UserSearchModel.Item>> {
                logi(TAG, "now Data size is===>${it.size}")
                adapter.list = viewModel.listLiveData.value
                adapter.notifyDataSetChanged()
                activity.title =
                    "${context.getString(R.string.app_name)} Search Result Size：${it.size}"
            })
    }
}
