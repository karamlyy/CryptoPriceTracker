package com.example.cryptotracker.ui


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptotracker.adapter.SearchAdapter
import com.example.cryptotracker.databinding.FragmentSearchCryptoBinding
import com.example.cryptotracker.utils.EmptyDataObserver
import com.example.cryptotracker.utils.Resource
import com.example.cryptotracker.viewmodel.CryptoViewModel
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.recyclerview.animators.SlideInDownAnimator

@AndroidEntryPoint
class SearchCrypto : Fragment() {

    private val searchViewModel by viewModels<CryptoViewModel>()

    private lateinit var searchAdapter : SearchAdapter
    private var _binding: FragmentSearchCryptoBinding? = null
    private val binding: FragmentSearchCryptoBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentSearchCryptoBinding.inflate(layoutInflater)

        searchAdapter = SearchAdapter()
        setUpRecyclerView(searchAdapter)

        //API Call
        binding.etCryptoSearch.setOnEditorActionListener{v, actionId, event ->
            return@setOnEditorActionListener when(actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    if (binding.etCryptoSearch.text.isNotEmpty()) {
                        val input = binding.etCryptoSearch.text.toString().lowercase()
                        searchViewModel.searchData(input)
                    } else {
                        Toast.makeText(requireContext(),"Enter Currency Name",Toast.LENGTH_SHORT).show()
                    }
                    true
                }
                else -> false
            }
        }

        searchViewModel.searchList.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Success -> {
                    response.data.let {
                        searchAdapter.submitList(listOf(it!!.data))
                    }
                }
                is Resource.Error -> {
                    response.message?.let {
                        Toast.makeText(requireContext(),"Invalid Currency Name",Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {

                }
            }
        })
        setHasOptionsMenu(true)
        return binding.root
    }
    private fun setUpRecyclerView(myAdapter: SearchAdapter) {
        binding.searchRecview.apply {
            adapter = myAdapter
            layoutManager = LinearLayoutManager(requireContext())
            //
            val emptyDataObserver = EmptyDataObserver(binding.searchRecview,binding.emptyStateSearch)
            searchAdapter.registerAdapterDataObserver(emptyDataObserver)
            itemAnimator = SlideInDownAnimator().apply {
                addDuration = 400L
            }

        }
    }
}