package com.nikmaram.presentaion.musicDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.nikmaram.data.model.MusicFile
import com.nikmaram.presentaion.R
import com.nikmaram.presentaion.databinding.FragmentDetailMusicBinding
import com.nikmaram.presentaion.service.MusicPlayerService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MusicDetailFragment : Fragment() {

    private var binding: FragmentDetailMusicBinding? = null
    private val args: MusicDetailFragmentArgs by navArgs()
    @Inject
    lateinit var musicPlayerService: MusicPlayerService
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
        val musicFile = args.musicFile
            binding?.apply {
                // set text and image views with music data
                binding?.model = musicFile
                binding?.lifecycleOwner = lifecycleOwner
                binding?.executePendingBindings()
        }
        binding?.playPauseButton?.setOnClickListener { onPlayButtonClicked() }
        binding?.nextButton?.setOnClickListener { onNextButtonClicked() }
        binding?.prevButton?.setOnClickListener { onPreviousButtonClicked() }
    }
    private fun onPlayButtonClicked() {
        val isPlaying = musicPlayerService.togglePlayback()
        binding?.playPauseButton?.setImageResource(
            if (isPlaying) R.drawable.ic_play
            else R.drawable.ic_pause
        )
    }

    private fun onNextButtonClicked() {
        val musicFile = musicPlayerService.playNext()
        updateUI(musicFile)
    }

    private fun onPreviousButtonClicked() {
        val musicFile = musicPlayerService.playPrevious()
        updateUI(musicFile)
    }

    private fun updateUI(musicFile: MusicFile?) {
        binding?.model = musicFile
        binding?.executePendingBindings()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
