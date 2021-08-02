package com.infotech.assignment.nyinyi.omdbmoviesapp.ui.movies.detail

import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.infotech.assignment.nyinyi.omdbmoviesapp.R
import com.infotech.assignment.nyinyi.omdbmoviesapp.databinding.FragmentMovieDetailBinding
import com.infotech.assignment.nyinyi.omdbmoviesapp.models.MovieDetailResponse
import com.infotech.assignment.nyinyi.omdbmoviesapp.navigator.ApplicationNavigator
import com.infotech.assignment.nyinyi.omdbmoviesapp.utils.Status
import com.infotech.assignment.nyinyi.omdbmoviesapp.utils.isNetworkAvailable
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class MovieDetailFragment : Fragment(R.layout.fragment_movie_detail) {

    private val viewModel: MovieDetailViewModel by viewModels()

    @Inject
    lateinit var navigator: ApplicationNavigator

    private var myJob: Job? = null

    private var _binding: FragmentMovieDetailBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString("id")?.also {
            viewModel.selectItem(it)
        }
        _binding = FragmentMovieDetailBinding.bind(view)

        binding.btnBack.setOnClickListener {
            activity?.onBackPressed()
        }

        if (requireContext().isNetworkAvailable()) {
            viewModel.data.observe(viewLifecycleOwner, Observer {
                when (it.status) {
                    Status.LOADING -> {
                        binding.rlLoading.isVisible = true
                        binding.progress.isVisible = true
                        binding.scrollView.isVisible = false
                    }
                    Status.SUCCESS -> {

                        binding.rlLoading.isVisible = false
                        binding.progress.isVisible = false
                        binding.scrollView.isVisible = true
                        bindDetailData(it.data)
                    }
                    Status.ERROR -> {

                        binding.rlLoading.isVisible = true
                        binding.progress.isVisible = false
                        binding.tvError.text = it?.message ?: "No Response"
                        binding.scrollView.isVisible = false
                    }
                }
            })
        }else {

            binding.rlLoading.isVisible = true
            binding.progress.isVisible = false
            binding.tvError.text = "No internet connection."
            binding.scrollView.isVisible = false
        }
    }

    private fun bindDetailData(data: MovieDetailResponse?) {
        val multi = MultiTransformation<Bitmap>(
            CenterCrop(),
            RoundedCornersTransformation(20, 16, RoundedCornersTransformation.CornerType.ALL)
        )

        if (data?.poster.isNullOrEmpty()) {
            Glide.with(binding.root)
                .load(R.drawable.placeholder_movie)
                .error(R.drawable.placeholder_movie)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivPoster)

            Glide.with(binding.root)
                .load(R.drawable.placeholder_movie)
                .error(R.drawable.placeholder_movie)
                .apply(RequestOptions.bitmapTransform(multi))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivPoster)
        } else {

            Glide.with(binding.root)
                .load(data?.poster)
                .error(R.drawable.placeholder_movie)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivPoster)

            Glide.with(binding.root)
                .load(data?.poster)
                .error(R.drawable.placeholder_movie)
                .apply(RequestOptions.bitmapTransform(multi))
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.ivMovie)
        }

        binding.tvTitle.text = data?.title
        binding.tvYear.text = data?.year

        binding.tvReview.text = data?.imdbRating ?: "0"
        binding.tvReviewTitle.text = getString(R.string.review_count, data?.imdbVotes)

        binding.tvRating.text = data?.rated
        binding.tvDuration.text = data?.runtime
        binding.tvRelease.text = data?.released
        binding.tvGenre.text = data?.genre
        binding.tvAbout.text = data?.plot
        binding.tvActors.text = data?.actors
        binding.tvDirector.text = data?.director
        binding.tvWriter.text = data?.writer
        binding.tvLanguage.text = data?.language
        binding.tvAwards.text = data?.awards

    }

    override fun onDestroy() {
        myJob?.cancel()
        super.onDestroy()
    }


}