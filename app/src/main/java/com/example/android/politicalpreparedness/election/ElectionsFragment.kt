package com.example.android.politicalpreparedness.election

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.databinding.FragmentElectionBinding
import com.example.android.politicalpreparedness.election.adapter.ElectionListAdapter
import com.example.android.politicalpreparedness.election.adapter.ElectionListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class ElectionsFragment : BaseFragment() {

    //TODO: Declare ViewModel
    private lateinit var binding: FragmentElectionBinding
    override val _viewModel: ElectionsViewModel by viewModel()


    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container, false)

        //TODO: Add ViewModel values and create ViewModel

        //TODO: Add binding values

        //TODO: Link elections to voter info

        //TODO: Initiate recycler adapters

        //TODO: Populate recycler adapters
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        binding.viewModel = _viewModel

        setUpRecyclerView()
        setupObserver()
    }

    //TODO: Refresh adapters when fragment loads

    private fun setupObserver() {
        _viewModel.nagivateToVoterInfo.observe(viewLifecycleOwner, Observer {
            _viewModel.nagivateToVoterInfo?.let { election ->

                findNavController().navigate(ElectionsFragmentDirections.actionElectionsFragmentToVoterInfoFragment(election.value!!))
            }
        })
    }

    private fun setUpRecyclerView() {
        val upcomingAdapter = ElectionListAdapter(ElectionListener {
            _viewModel.onElectionSelect(it)
        })
        val saveAdapter = ElectionListAdapter(ElectionListener {
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


}