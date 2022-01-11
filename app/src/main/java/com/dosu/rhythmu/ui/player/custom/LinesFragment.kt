package com.dosu.rhythmu.ui.player.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dosu.rhythmu.databinding.FragmentLinesBinding
import com.dosu.rhythmu.utils.TouchDataWriter

class LinesFragment(private val touchDataWriter: TouchDataWriter) : Fragment() {
    private var _binding: FragmentLinesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLinesBinding.inflate(inflater, container, false)
        binding.linesView.touchDataListener = touchDataWriter
        return binding.root;
    }
}