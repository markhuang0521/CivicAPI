package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.base.NavigationCommand
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import org.koin.androidx.viewmodel.ext.android.viewModel

class ElectionsFragment : BaseFragment() {

    //TODO: Declare ViewModel
    private lateinit var binding: FragmentElectionBinding
    override val _viewModel: ElectionsViewModel by viewModel()
    private lateinit var upcomingAdapter: ElectionListAdapter
    private lateinit var saveAdapter: ElectionListAdapter


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)
        binding.viewModel = _viewModel
        setDisplayHomeAsUpEnabled(true)

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this

        setUpRecyclerView()
        setupObserver()
    }

    //TODO: Refresh adapters when fragment loads

    private fun setupObserver() {
        _viewModel.nagivateToVoterInfo.observe(viewLifecycleOwner, Observer { election ->
            if (election == null) {

            }
            election?.let {
//                findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election))
                _viewModel.navigationCommand.postValue(
                        NavigationCommand.To(
                                ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election)
                        )
                )
            }

        })
        _viewModel.upcomingElections.observe(viewLifecycleOwner, Observer {
            it?.let {
                upcomingAdapter.submitList(it)
            }
        })
        _viewModel.saveElections.observe(viewLifecycleOwner, Observer {
            it?.let {
                saveAdapter.submitList(it)
            }
        })
    }

    private fun setUpRecyclerView() {
        upcomingAdapter = ElectionListAdapter(ElectionListener {
            _viewModel.onElectionSelect(it)
        })
        saveAdapter = ElectionListAdapter(ElectionListener {
            _viewModel.onElectionSelect(it)
        })
        binding.recyclerSaveElection.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = saveAdapter
        }
        binding.recyclerUpcomingElection.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = upcomingAdapter
        }
    }

    override fun onResume() {
        super.onResume()
        _viewModel.loadUpcomingElections()
        _viewModel.loadSaveElections()
    }


}