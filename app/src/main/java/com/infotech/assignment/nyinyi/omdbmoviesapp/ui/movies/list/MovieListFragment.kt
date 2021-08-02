package com.infotech.assignment.nyinyi.omdbmoviesapp.ui.movies.list

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.infotech.assignment.nyinyi.omdbmoviesapp.R
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.entity.Movie
import com.infotech.assignment.nyinyi.omdbmoviesapp.databinding.FragmentMovieListBinding
import com.infotech.assignment.nyinyi.omdbmoviesapp.navigator.ApplicationNavigator
import com.infotech.assignment.nyinyi.omdbmoviesapp.navigator.Screens
import com.infotech.assignment.nyinyi.omdbmoviesapp.ui.adapter.MovieAdapter
import com.infotech.assignment.nyinyi.omdbmoviesapp.ui.adapter.MovieLoadingStateAdapter
import com.infotech.assignment.nyinyi.omdbmoviesapp.utils.Prefs
import com.infotech.assignment.nyinyi.omdbmoviesapp.utils.UiAction
import com.infotech.assignment.nyinyi.omdbmoviesapp.utils.UiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MovieListFragment : Fragment(R.layout.fragment_movie_list) {

    private val viewModel: MovieListViewModel by viewModels()

    @Inject
    lateinit var prefs: Prefs

    @Inject
    lateinit var navigator: ApplicationNavigator

    private var _binding: FragmentMovieListBinding? = null
    private val binding get() = _binding!!

    private val adapter = MovieAdapter { movie: Movie ->
        navigator.navigateTo(Screens.MOVIES_DETAIL, bundleOf("id" to movie.imdbID))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentMovieListBinding.bind(view)
        binding.toolbar.inflateMenu(R.menu.menu_item_home)

        binding.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_profile -> {
                    true
                }
                else -> false
            }
        }

        bindState(viewModel.state, viewModel.accept)
    }

    private fun bindState(uiState: StateFlow<UiState>, uiAction: (UiAction) -> Unit) {

        setUpAdapter(uiState)
        setUpSearch(uiState, uiAction)
    }

    private fun setUpSearch(
        uiState: StateFlow<UiState>,
        onQueryChanged: (UiAction.Search) -> Unit
    ) {

        binding.searchView.onActionViewExpanded()
        binding.searchView.clearFocus()

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                query?.let {
                    updateMovieListByInput(it, onQueryChanged)
                }

                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        lifecycleScope.launch {
            uiState
                .map { it.query }
                .distinctUntilChanged()
                .collect {
                    binding.searchView.setQuery(it, true)
                }
        }
    }

    private fun updateMovieListByInput(query: String, onQueryChanged: (UiAction.Search) -> Unit) {
        binding.allMoviesRecyclerView.scrollToPosition(0)
        onQueryChanged(UiAction.Search(query))
        prefs.setQuery(query)
    }

    private fun setUpAdapter(
        uiState: StateFlow<UiState>,
    ) {

        val pagingData = uiState
            .map { it.pagingData }
            .distinctUntilChanged()

        lifecycleScope.launch {
            pagingData.collectLatest {
                adapter.submitData(it)
            }
        }

        binding.allMoviesRecyclerView.apply {

            if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_PORTRAIT) {
                layoutManager = GridLayoutManager(requireContext(), 3)
            } else {
                layoutManager = GridLayoutManager(requireContext(), 5)
            }
            setHasFixedSize(true)
        }
        val header = MovieLoadingStateAdapter { adapter.retry() }
        binding.allMoviesRecyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
            header = header,
            footer = MovieLoadingStateAdapter { retry() }
        )

        lifecycleScope.launch {
            try {
                adapter.loadStateFlow.collectLatest { loadState ->

                    header.loadState = loadState.mediator
                        ?.refresh
                        ?.takeIf { it is LoadState.Error && adapter.itemCount > 0 }
                        ?: loadState.prepend

                    val isListEmpty = loadState.refresh is LoadState.NotLoading && adapter.itemCount == 0

                    binding.errorTxt.isVisible = isListEmpty
                    binding.progress.isVisible = loadState.mediator?.refresh is LoadState.Loading
                    binding.btnRetry.isVisible = loadState.mediator?.refresh is LoadState.Error && adapter.itemCount == 0
                    val errorState = loadState.source.append as? LoadState.Error
                        ?: loadState.source.prepend as? LoadState.Error
                        ?: loadState.append as? LoadState.Error
                        ?: loadState.prepend as? LoadState.Error
                    errorState?.let {
                    }
                }
            } catch (e: Exception) {
            }
        }

        binding.let {

            it.btnRetry.setOnClickListener {
                retry()
            }
        }
    }

    private fun retry() {
        adapter.retry()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
