package com.example.nuberjam.ui.main.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.nuberjam.R
import com.example.nuberjam.databinding.FragmentLibraryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LibraryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setToolbar()
        setData()
        setFavoriteItem()
    }

    private fun setToolbar() {
        val searchButton = binding.appbar.btnSearch
        val viewTypeButton = binding.appbar.btnViewType
        val plusButton = binding.appbar.btnPlus

        searchButton.setOnClickListener {
            // TODO: navigate to search
            Toast.makeText(requireActivity(), "You clicked me.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setData() {
        viewModel.getAccountState().observe(viewLifecycleOwner) { account ->
            if (account != null) {
                // TODO: MP-370
            }
        }
    }

    private fun setFavoriteItem() {
        binding.apply {
            favoriteItem.tvPlaylistType.text = getString(R.string.favorite)
            favoriteItem.cvLibraryItem.setOnClickListener {
                // TODO: Navigate to Favorite Screen
            }
        }

    }
}