package com.infotech.assignment.nyinyi.omdbmoviesapp.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.infotech.assignment.nyinyi.omdbmoviesapp.databinding.NetworkStateItemBinding

class MovieLoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<MovieLoadingStateAdapter.LoadStateViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        return LoadStateViewHolder(
            NetworkStateItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {

        val progress = holder.binding.progressBarItem
        val retryBtn = holder.binding.retyBtn
        val txtErrorMessage = holder.binding.errorMsgItem

        if (loadState is LoadState.Error) {
            txtErrorMessage.text = loadState.error.localizedMessage
        }
        progress.isVisible = loadState is LoadState.Loading
        retryBtn.isVisible = loadState is LoadState.Error
        txtErrorMessage.isVisible = loadState is LoadState.Error

        retryBtn.setOnClickListener {
            retry.invoke()
        }
    }

    inner class LoadStateViewHolder(val binding: NetworkStateItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}
