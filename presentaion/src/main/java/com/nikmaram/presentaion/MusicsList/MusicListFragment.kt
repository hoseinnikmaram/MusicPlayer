package com.nikmaram.presentaion.MusicsList

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.nikmaram.data.model.MusicFile
import com.nikmaram.presentaion.R
import com.nikmaram.presentaion.databinding.FragmentMusicListBinding
import com.nikmaram.presentaion.model.ServiceContentWrapper
import com.nikmaram.presentaion.readExternalStoragePermission
import com.nikmaram.presentaion.service.MusicPlayerService
import com.nikmaram.presentaion.utility.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MusicListFragment : Fragment() {
    private val viewModel: MusicListViewModel by viewModels()
    private lateinit var binding: FragmentMusicListBinding
    private lateinit var musicListAdapter: MusicListAdapter
    private lateinit var requestPermissionResultLauncher: ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (::binding.isInitialized) return binding.root
        binding = FragmentMusicListBinding.inflate(inflater, container, false)
        checkPermission()
        setupRecyclerView()
        observeViewModel()
        return binding.root
    }

    private fun observeViewModel() {
        viewModel.musicPagingData.observe(viewLifecycleOwner){ pagingData ->
            binding.progress.visibility = View.GONE
            lifecycleScope.launch {
                musicListAdapter.submitData(pagingData)
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
        if (musicListAdapter.snapshot().items.isEmpty()) {
            Toast.makeText(requireContext(), getString(R.string.nothing_to_play), Toast.LENGTH_SHORT).show()
            return
        }
        MusicPlayerService.startService(requireContext(), ServiceContentWrapper(
            position = position,
            playlist = musicListAdapter.snapshot().items.toMutableList(),
        )
        )
    }
    private fun checkPermission() {
        requestPermissionResultLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted, proceed with getting music files
                viewModel.getMusicFiles()
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
                    viewModel.getMusicFiles()
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

