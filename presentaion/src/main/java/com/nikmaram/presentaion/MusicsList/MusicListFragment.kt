package com.nikmaram.presentaion.MusicsList

import android.database.Observable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.nikmaram.presentaion.databinding.FragmentMusicListBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicListFragment : Fragment() {
    private val viewModel: MusicListViewModel by viewModels()
    private lateinit var binding: FragmentMusicListBinding
    private lateinit var musicListAdapter:MusicListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.musicListState.observe(viewLifecycleOwner){musicListState ->
            when(musicListState){
                is MusicListViewModel.MusicListState.Error -> TODO()
                is MusicListViewModel.MusicListState.Loaded -> {
                    musicListAdapter.submitList(musicListState.musicList)
                }
                MusicListViewModel.MusicListState.Loading -> TODO()
            }
        }
    }

    private fun setupRecyclerView() {
        musicListAdapter = MusicListAdapter {

        }
        binding.recyclerView.apply {
            adapter = musicListAdapter
            setHasFixedSize(true)
        }
    }
}

