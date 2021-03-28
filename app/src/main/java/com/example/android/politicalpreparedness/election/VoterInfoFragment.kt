package com.example.android.politicalpreparedness.election

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import com.example.android.politicalpreparedness.R
import com.example.android.politicalpreparedness.base.BaseFragment
import com.example.android.politicalpreparedness.databinding.FragmentVoterInfoBinding
import com.example.android.politicalpreparedness.utils.setDisplayHomeAsUpEnabled
import com.example.android.politicalpreparedness.utils.setTitle
import org.koin.android.ext.android.inject

class VoterInfoFragment : BaseFragment() {
    private lateinit var binding: FragmentVoterInfoBinding
    override val _viewModel: VoterInfoViewModel by inject()
    private val args: VoterInfoFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_voter_info, container, false)

        binding.viewModel = _viewModel

        setTitle(args.election.name)
        setDisplayHomeAsUpEnabled(true)


        //TODO: Handle loading of URLs

        //TODO: Handle save button UI state
        //TODO: cont'd Handle save button clicks
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = this
        _viewModel.selectedElection.value = args.election
        _viewModel.getVoterInfo()
        setupObservers()

    }

    private fun setupObservers() {
//        _viewModel.selectedElection.observe(viewLifecycleOwner, Observer { election ->
//            election?.let {
//                _viewModel.saveElection()
//            }
//        })

        _viewModel.webUrl.observe(viewLifecycleOwner, Observer { url ->
            url?.let {
                openWebUrl(url)
            }
        })
    }

    private fun openWebUrl(url: String?) {
        val openWebUrlIntent = Intent(Intent.ACTION_VIEW, url?.toUri())
        startActivity(openWebUrlIntent)
    }


}