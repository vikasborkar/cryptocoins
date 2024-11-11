package com.example.cryptocoins

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cryptocoins.databinding.FragmentCryptoListBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class CryptoListFragment : Fragment() {
    private var _binding: FragmentCryptoListBinding? = null
    private val binding
        get() = checkNotNull(_binding) {
            "Cannot access binding because it is null. Is the view visible?"
        }

    private val viewModel: CryptoListViewModel by viewModels()
    private var adapter = CryptoAdapter()
    private var searchView: SearchView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCryptoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpViews()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUI(state)
                }
            }
        }
    }

    private fun setUpViews() {

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(0, 0, 0, systemBars.bottom)
            insets
        }

        setupMenu()

        binding.apply {
            rvCryptoList.layoutManager = LinearLayoutManager(context)
            rvCryptoList.adapter = adapter
            rvCryptoList.itemAnimator = DefaultItemAnimator()

            toolbar.title = getString(R.string.title_crypto_list_fragment)

            btnFilter.setOnClickListener {
                findNavController().navigate(
                    CryptoListFragmentDirections.selectFilter(viewModel.uiState.value.filter)
                )
            }

            layoutError.btnRetry.setOnClickListener {
                if (requireContext().isNetworkAvailable()) {
                    viewModel.retry()
                }
            }
        }

        setFragmentResultListener(FilterBottomSheetFragment.REQ_FILTER) { _, bundle ->
            val filter = bundle.getParcelable(FilterBottomSheetFragment.ARG_FILTER) as? Filter
            filter?.let {
                viewModel.setFilter(it)
            }
        }
    }

    private fun setupMenu() {
        val menu = binding.toolbar.menu
        val searchItem = menu.findItem(R.id.menu_search)
        searchView = searchItem.actionView as? SearchView
        searchView?.apply {
            imeOptions = EditorInfo.IME_ACTION_DONE
            queryHint = getString(R.string.search_hint)
            maxWidth = Integer.MAX_VALUE

            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    viewModel.setQuery(query ?: "")
                    clearFocus()
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    return true
                }
            })

            setOnCloseListener {
                if (viewModel.uiState.value.query.isNotEmpty())
                    viewModel.setQuery("")
                false
            }
        }
    }

    private fun updateUI(state: CryptoListUiState) {
        adapter.submitList(state.cryptos)
        searchView?.setQuery(state.query, false)

        binding.apply {
            layoutLoading.root.visibility = if (state.isApiLoading) View.VISIBLE else View.GONE
            rvCryptoList.visibility =
                if (!state.isApiLoading && state.isApiSuccess) View.VISIBLE else View.GONE
            btnFilter.visibility =
                if (!state.isApiLoading && state.isApiSuccess) View.VISIBLE else View.GONE
            searchView?.visibility =
                if (!state.isApiLoading && state.isApiSuccess) View.VISIBLE else View.GONE
            layoutError.root.visibility =
                if (!state.isApiLoading && !state.isApiSuccess) View.VISIBLE else View.GONE

            if (!state.isApiSuccess && !requireContext().isNetworkAvailable()) {
                layoutError.tvLabelError.text = getString(R.string.no_network_connection)
            } else {
                layoutError.tvLabelError.text =
                    state.apiErrorMessage.ifEmpty { getString(R.string.error_generic) }
            }
        }

        if (!state.isApiLoading && state.isApiSuccess && state.cryptos.isEmpty()) {
            Snackbar.make(binding.root, getString(R.string.no_data_found), Snackbar.LENGTH_SHORT)
                .show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (context as? AppCompatActivity)?.supportActionBar?.title =
            getString(R.string.title_crypto_list_fragment)
    }

    override fun onDetach() {
        super.onDetach()
        (context as? AppCompatActivity)?.supportActionBar?.title = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}