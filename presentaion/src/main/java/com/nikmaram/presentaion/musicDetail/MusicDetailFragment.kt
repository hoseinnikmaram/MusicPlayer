package com.nikmaram.presentaion.musicDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.nikmaram.presentaion.databinding.FragmentDetailMusicBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicDetailFragment : Fragment() {

    private var binding: FragmentDetailMusicBinding? = null
    private val args: MusicDetailFragmentArgs by navArgs()
    private val viewModel: MusicDetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentDetailMusicBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val musicId = args.musicId
        viewModel.loadMusicById(musicId)
        viewModel.music.observe(viewLifecycleOwner) { music ->
            binding?.apply {
                // set text and image views with music data
                binding?.model = music
                binding?.lifecycleOwner = lifecycleOwner
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
