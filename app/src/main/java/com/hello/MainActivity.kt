package com.hello

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.util.containsKey
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hello.databinding.ActivityMainBinding
import com.hello.databinding.ViewHorizontalBinding
import com.hello.databinding.ViewVerticalBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var viewModel: MainViewModel

    private val adapter = MyAdapter()

    private val totalData: List<List<Int>> = (0..100).map { index -> (0..100).map { it + index * 100 }.toList() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        binding.recyclerView.adapter = adapter
        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.setRecycledViewPool(RecyclerView.RecycledViewPool())
        adapter.submitList(totalData)
    }
    
    private class MyViewHolder(val binding: ViewVerticalBinding, val adapter: MyHorizontalAdapter, var index: Int) : RecyclerView.ViewHolder(binding.root)

    private class MyHorizontalViewHolder(val binding: ViewHorizontalBinding) : RecyclerView.ViewHolder(binding.root)

    private class MyHorizontalAdapter : ListAdapter<Int, MyHorizontalViewHolder>(object : DiffUtil.ItemCallback<Int>() {
        override fun areItemsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem

        override fun areContentsTheSame(oldItem: Int, newItem: Int): Boolean = oldItem == newItem
    }) {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHorizontalViewHolder {
            println("MyHorizontalAdapter, onCreateViewHolder")
            val binding = ViewHorizontalBinding.inflate(LayoutInflater.from(parent.context))
            return MyHorizontalViewHolder(binding)
        }

        override fun onBindViewHolder(holder: MyHorizontalViewHolder, position: Int) {
            holder.binding.textView.text = "${getItem(position)}"
        }
    }

    private class MyAdapter : ListAdapter<List<Int>, MyViewHolder>(object : DiffUtil.ItemCallback<List<Int>>() {
        override fun areItemsTheSame(oldItem: List<Int>, newItem: List<Int>): Boolean = false

        override fun areContentsTheSame(oldItem: List<Int>, newItem: List<Int>): Boolean = false
    }) {
        private val pool = RecyclerView.RecycledViewPool().apply {
            setMaxRecycledViews(0, 20)
        }

        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            println("MyAdapter, onCreateViewHolder")
            val binding = ViewVerticalBinding.inflate(LayoutInflater.from(parent.context))
            binding.horizontalView.layoutManager = LinearLayoutManager(parent.context, LinearLayoutManager.HORIZONTAL, false).apply {
                recycleChildrenOnDetach = true
            }
            val adapter = MyHorizontalAdapter()
            binding.horizontalView.setRecycledViewPool(pool)
            binding.horizontalView.setHasFixedSize(true)
            return MyViewHolder(binding, adapter, -1)
        }

        private val jobs = SparseArray<Job>()

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.binding.textView.text = "$position"
            holder.binding.horizontalView.adapter = holder.adapter
            jobs.takeIf { jobs.containsKey(position) }?.also {
                it[position].cancel()
                it.remove(position)
            }

            holder.index = position
            val job = scope.launch(Dispatchers.Default) {
                delay(100)
                if (holder.index == position)
                    holder.adapter.submitList(getItem(position))
            }
            jobs[position] = job


        }
    }
}