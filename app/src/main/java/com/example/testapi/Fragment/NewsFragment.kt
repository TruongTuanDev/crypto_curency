package com.example.testapi.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.testapi.API.ApiNews
import com.example.testapi.Adapter.NewsAdapter
import com.example.testapi.databinding.FragmentNewsBinding
import com.example.testapi.model.DiscoverModel
import com.example.testapi.model.NewsModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class NewsFragment : Fragment() {

    private var _binding: FragmentNewsBinding? = null
    private val binding get() = _binding!!

    private lateinit var newsAdapter: NewsAdapter

    private var newsList: List<NewsModel> = listOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNewsBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        newsAdapter = NewsAdapter(newsList)

        setupRecyclerView(binding.newsRecyclerView, newsAdapter)
        loadNewsLists()
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.VERTICAL, false)
            this.adapter = adapter
        }
    }

    private fun loadNewsLists() {
        ApiNews.apiNew.listNews.enqueue(object : Callback<DiscoverModel> {
            override fun onResponse(call: Call<DiscoverModel>, response: Response<DiscoverModel>) {
                if (response.isSuccessful) {
                    response.body()?.let { discoverModel ->
                        newsList = discoverModel.Data
                        Log.e("Tin tức", newsList.toString())
                        newsAdapter.updateData(newsList)
                    }
                }
            }

            override fun onFailure(call: Call<DiscoverModel>, t: Throwable) {
                Log.e("Ko đc mô", t.message ?: "Error")
            }
        })
    }
}

