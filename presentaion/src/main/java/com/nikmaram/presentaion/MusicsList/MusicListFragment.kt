package com.nikmaram.presentaion.MusicsList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.nikmaram.data.model.MusicFile
import com.nikmaram.presentaion.R
import com.nikmaram.presentaion.databinding.FragmentMusicListBinding
import com.nikmaram.presentaion.model.ServiceContentWrapper
import com.nikmaram.presentaion.readExternalStoragePermission
import com.nikmaram.presentaion.service.MusicPlayerService
import com.nikmaram.presentaion.utility.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicListFragment : Fragment() {
    private val viewModel: MusicListViewModel by viewModels()
    private lateinit var binding: FragmentMusicListBinding
    private lateinit var musicListAdapter:MusicListAdapter
    private lateinit var requestPermissionResultLauncher:ActivityResultLauncher<String>
    private var musics = ArrayList<MusicFile>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if(::binding.isInitialized) return binding.root
        binding = FragmentMusicListBinding.inflate(inflater, container, false)
        checkPermission()
        setupRecyclerView()
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.musicListState.observe(viewLifecycleOwner){musicListState ->
            when(musicListState){
                is MusicListViewModel.MusicListState.Error -> {binding.progress.visibility = View.GONE}
                is MusicListViewModel.MusicListState.Loaded -> {
                    binding.progress.visibility = View.GONE
                    musics.addAll(musicListState.musicList)
                    musicListAdapter.submitList(musicListState.musicList)
                }
                MusicListViewModel.MusicListState.Loading -> {}
            }
        }
    }

    private fun setupRecyclerView() {
        musicListAdapter = MusicListAdapter {
            startPlayer(position = it)
            val action = MusicListFragmentDirections.actionMusicListFragmentToMusicDetailFragment()
            findNavController().navigate(action)
        }
        binding.recyclerView.apply {
            adapter = musicListAdapter
            setHasFixedSize(true)
        }
    }
    private fun startPlayer(position: Int) {
        if (musics.size == 0) {
            Toast.makeText(requireContext(), "Nothing to play", Toast.LENGTH_SHORT).show()
            return
        }
        MusicPlayerService.startService(requireContext(), ServiceContentWrapper(
            position = position,
            playlist = musics,
        )
        )
    }
    private fun checkPermission() {
        requestPermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted, proceed with getting music files
                viewModel.loadMusicList()
            } else {
                // Permission is denied
                Toast.makeText(
                    requireContext(),
                    getString(R.string.Permission_denied_to_read_external_storage),
                    Toast.LENGTH_SHORT
                ).show()
                requestReadExternalStoragePermission()
            }
        }
        // Request read external storage permission if not granted yet
        requestReadExternalStoragePermission()
    }
         private fun requestReadExternalStoragePermission() {
            when {
                PermissionUtils.isPermissionGranted(
                    requireContext(),
                    readExternalStoragePermission
                ) -> {
                    // Permission is already granted, proceed with getting music files
                    viewModel.loadMusicList()
                }
                shouldShowRequestPermissionRationale(readExternalStoragePermission) -> {
                    // Display a rationale to the user
                    showAlertDialog()
                }
                else -> {
                    // Request permission
                    requestPermissionResultLauncher.launch(readExternalStoragePermission)
                }
            }
    }

    private fun showAlertDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.dialog_title))
            .setMessage(getString(R.string.dialog_message))
            .setPositiveButton(getString(R.string.ok)) { _, _ ->
                // Request permission when the user clicks OK
                requestPermissionResultLauncher.launch(readExternalStoragePermission)
            }
            .setNegativeButton(getString(R.string.cancel), null)
            .create()
            .show()
    }

}

