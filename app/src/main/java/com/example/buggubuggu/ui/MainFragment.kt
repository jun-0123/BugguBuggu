package com.example.buggubuggu.ui

import android.content.Intent
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.buggubuggu.R
import com.example.buggubuggu.databinding.FragmentMainBinding
import com.example.buggubuggu.widget.ItemsAdapter


class MainFragment : Fragment() {
    private var _binding:FragmentMainBinding?=null
    private val binding get()=_binding!!

    private val model:MainViewModel by viewModels()
    private val adapter = ItemsAdapter()

    private var num = 0


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerViewUsers.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewUsers.adapter = adapter

        // 어댑터에 리스너 추가 및 로그 추가
        adapter.addListener {
                val bundle = Bundle().apply {
                    putString("itemName",it.BSSH_NM)
                    putString("itemSector",it.INDUTY)
                    putString("itemDate",it.APNTDT)
                    putString("itemAddress",it.LOCPLC)
                }
                findNavController().navigate(R.id.action_mainFragment_to_detailFragment2, bundle)
        }

        model.items.observe(viewLifecycleOwner) { items ->
            adapter.updateData(items)
            Log.i("MainFragment", "Items updated: $items")
        }

        binding.buttonNext.setOnClickListener {
            num++
            if (num > 9) num = 0
            binding.seekBar4.progress = num
            model.getItems(num + 1)
        }

        binding.buttonPrev.setOnClickListener{
            num--
            if (num < 0) num = 9
            binding.seekBar4.progress = num
            model.getItems(num + 1)
        }

        binding.seekBar4.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                num = progress
                model.getItems(num + 1)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                // Do nothing
            }
        })


        model.getItems(num+1) // 데이터 가져오기
    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding= FragmentMainBinding.inflate(inflater, container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}

