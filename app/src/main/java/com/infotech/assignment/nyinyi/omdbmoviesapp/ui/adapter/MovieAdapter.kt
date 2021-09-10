package com.infotech.assignment.nyinyi.omdbmoviesapp.ui.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.request.RequestOptions
import com.infotech.assignment.nyinyi.omdbmoviesapp.R
import com.infotech.assignment.nyinyi.omdbmoviesapp.data.local.entity.Movie
import com.infotech.assignment.nyinyi.omdbmoviesapp.databinding.AdapterItemBinding
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class MovieAdapter(private val clicked: (Movie) -> Unit) :
    PagingDataAdapter<Movie, MovieAdapter.MoviesViewHolder>(MoviesDiffCallback()) {

    override fun onBindViewHolder(holder: MoviesViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoviesViewHolder {
        return MoviesViewHolder(
            AdapterItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    inner class MoviesViewHolder(private val binding: AdapterItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(data: Movie?) {
            binding.let {

                val multi = MultiTransformation<Bitmap>(
                    CenterCrop(),
                    RoundedCornersTransformation(
                        20,
                        16,
                        RoundedCornersTransformation.CornerType.ALL
                    )
                )

                it.title.text = data?.title

                if (data?.poster.isNullOrEmpty() || data?.poster.equals("N/A")) {
                    Glide.with(it.root)
                        .load(R.drawable.placeholder_movie)
                        .error(R.drawable.placeholder_movie)
                        .apply(RequestOptions.bitmapTransform(multi))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(it.ivMovie)
                } else {

                    Glide.with(it.root)
                        .load(data?.poster)
                        .error(R.drawable.placeholder_movie)
                        .apply(RequestOptions.bitmapTransform(multi))
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(it.ivMovie)
                }

                it.root.setOnClickListener {
                    data?.let { it1 ->
                        clicked.invoke(it1)
                    }
                }
            }
        }
    }

    private class MoviesDiffCallback : DiffUtil.ItemCallback<Movie>() {
        override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem.imdbID == newItem.imdbID
        }

        override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
            return oldItem == newItem
        }
    }
}
