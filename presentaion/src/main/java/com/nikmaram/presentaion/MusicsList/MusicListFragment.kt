package com.nikmaram.presentaion.MusicsList

import android.Manifest
import android.content.pm.PackageManager
import android.database.Observable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.fragment.findNavController
import com.nikmaram.presentaion.R
import com.nikmaram.presentaion.REQUEST_PERMISSION_READ_EXTERNAL_STORAGE
import com.nikmaram.presentaion.databinding.FragmentMusicListBinding
import com.nikmaram.presentaion.readExternalStoragePermission
import com.nikmaram.presentaion.utility.PermissionUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MusicListFragment : Fragment() {
    private val viewModel: MusicListViewModel by viewModels()
    private lateinit var binding: FragmentMusicListBinding
    private lateinit var musicListAdapter:MusicListAdapter
    private lateinit var requestPermissionResultLauncher:ActivityResultLauncher<String>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMusicListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkPermission()
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
            val action = MusicListFragmentDirections.actionMusicListFragmentToMusicDetailFragment(it.id)
            findNavController().navigate(action)
        }
        binding.recyclerView.apply {
            adapter = musicListAdapter
            setHasFixedSize(true)
        }
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
            // Request read external storage permission if not granted yet
            requestReadExternalStoragePermission()
        }
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

