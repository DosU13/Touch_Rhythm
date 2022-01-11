package com.dosu.rhythmu.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.dosu.rhythmu.databinding.FragmentInfoBinding
import com.dosu.rhythmu.utils.TouchDataWriter

class InfoFragment(private val touchDataWriter: TouchDataWriter) : Fragment() {
    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)
        binding.btn.setOnClickListener {
            var txt = ""
            touchDataWriter.touchDataMap.forEach { (t, u) ->
                txt += "$t : "
                u.forEach{ (id, pData) ->
                    txt += "$id:${pData.x}:${pData.y} "
                }
                txt += "\n"
            }
            binding.text.text = txt
        }
        return binding.root
    }
}