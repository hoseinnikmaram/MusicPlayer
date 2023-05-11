package com.nikmaram.presentaion.musicDetail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.exoplayer2.MediaMetadata
import com.google.android.exoplayer2.Player
import com.google.android.material.slider.Slider
import com.nikmaram.presentaion.R
import com.nikmaram.presentaion.databinding.FragmentDetailMusicBinding
import com.nikmaram.presentaion.service.MusicPlayerService
import com.nikmaram.presentaion.utility.formatAsDate
import com.nikmaram.presentaion.utility.formatForDisplaying
import com.nikmaram.presentaion.utility.setOnBackFragmentNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicDetailFragment : Fragment(),View.OnClickListener{

    private var binding: FragmentDetailMusicBinding? = null
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
        MusicPlayerService.getCurrentMetadata().observe(viewLifecycleOwner) {
            if (it != null && binding != null)
                updateUI(it)
        }
        binding!!.ibPlayPause.setOnClickListener(this)
        binding!!.ibNextTrack.setOnClickListener(this)
        binding!!.ibPreviousTrack.setOnClickListener(this)
        setupTimeSlider()
        setCurrentMetadataObserver()
        setIsPlayingObserver()
        setTimelineObserver()
    }
    private fun setupTimeSlider() {
        binding!!.sliderTime.addOnChangeListener { slider, value, fromUser ->
            if (fromUser)
                MusicPlayerService.onSeekTo(value.toLong())
        }
    }
    private fun setIsPlayingObserver() = MusicPlayerService.isPlaying().observe(viewLifecycleOwner) {
        if (it) {
            binding!!.ibPlayPause.setImageResource(R.drawable.ic_baseline_pause_circle_filled_64)
        } else {
            binding!!.ibPlayPause.setImageResource(R.drawable.ic_baseline_play_circle_filled_64)
        }
    }
    private fun setTimelineObserver() = MusicPlayerService.getCurrentDuration().observe(viewLifecycleOwner) {

        val duration = MusicPlayerService.getDuration()

        if (duration < 0 || it < 0)
            return@observe

        binding!!.sliderTime.apply {
            valueFrom = 0f
            valueTo = (duration).toFloat()
            value = (it).toFloat()
        }

        binding!!.tvCurrentTrackTime.text = it.formatAsDate()
        binding!!.tvRemainingTrackTime.text = (duration - it).formatAsDate()
    }

    private fun setCurrentMetadataObserver() = MusicPlayerService.getCurrentMetadata().observe(viewLifecycleOwner) {
        if (it != null)
            updateUI(it)
    }

    private fun updateUI(mediaMetadata: MediaMetadata) {

        binding!!.sliderTime.setLabelFormatter {
            (it.toLong()).formatAsDate()
        }
        binding!!.tvTrackName.text = mediaMetadata.title.formatForDisplaying()
        binding!!.tvPerformer.text = mediaMetadata.artist.formatForDisplaying()

        val bytes = mediaMetadata.artworkData

        setupImage(bytes)
    }
    private fun setupImage(bytes: ByteArray?) {

        Glide.with(this)
            .load(bytes)
            .placeholder(
                    R.drawable.ic_round_audiotrack_24
            )
            .into(binding!!.ivCover)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            binding!!.ibPlayPause.id -> {
                MusicPlayerService.togglePlayback()
            }
            binding!!.ibNextTrack.id -> {
                MusicPlayerService.onNext()
            }
            binding!!.ibPreviousTrack.id -> {
                MusicPlayerService.onPrevious()
            }
        }
    }

}
