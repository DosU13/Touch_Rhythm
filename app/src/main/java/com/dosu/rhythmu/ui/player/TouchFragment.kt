package com.dosu.rhythmu.ui.player

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.dosu.rhythmu.R
import com.dosu.rhythmu.databinding.FragmentTouchBinding
import com.dosu.rhythmu.utils.TouchDataWriter
import com.dosu.rhythmu.utils.TxtWriter

class TouchFragment(private val touchDataWriter: TouchDataWriter) : Fragment() {
    private var _binding: FragmentTouchBinding? = null
    private val binding get() = _binding!!
    private var editMode = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTouchBinding.inflate(inflater, container, false)
        binding.touchRhythm.touchDataListener = touchDataWriter
        initEditMode()
        return binding.root;
    }

    private val txtWriter = TxtWriter()
    private fun initEditMode() {
        binding.info.setOnClickListener{
            Toast.makeText(context, resources.getString(R.string.how_to_use), Toast.LENGTH_LONG).show()
        }
        binding.editBtn.setOnClickListener {
            editMode = true
            refreshEditMode()
        }
        binding.submitBtn.setOnClickListener {
            editMode = false
            touchDataWriter.saveEdit()
            refreshEditMode()
        }
        binding.cancelBtn.setOnClickListener {
            editMode = false
            touchDataWriter.cancelEdit()
            refreshEditMode()
        }
    }

    private fun refreshEditMode(){
        binding.editBtn.visibility = if(editMode) View.GONE else View.VISIBLE
        binding.submitBtn.visibility = if(editMode) View.VISIBLE else View.GONE
        binding.cancelBtn.visibility = if(editMode) View.VISIBLE else View.GONE
        binding.touchRhythm.editMode = editMode
        touchDataWriter.editMode = editMode
    }
}