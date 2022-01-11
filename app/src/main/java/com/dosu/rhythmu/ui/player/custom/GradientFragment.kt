package com.dosu.rhythmu.ui.player.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dosu.rhythmu.databinding.FragmentGradientBinding
import com.dosu.rhythmu.utils.TouchDataWriter

class GradientFragment(private val touchDataWriter: TouchDataWriter) : Fragment() {
    private var _binding: FragmentGradientBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGradientBinding.inflate(inflater, container, false)
        binding.gradientView.touchDataListener = touchDataWriter
        return binding.root;
    }
}