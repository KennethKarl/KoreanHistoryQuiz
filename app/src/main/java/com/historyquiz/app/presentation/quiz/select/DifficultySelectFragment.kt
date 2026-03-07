package com.historyquiz.app.presentation.quiz.select

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.historyquiz.app.R
import com.historyquiz.app.databinding.FragmentDifficultySelectBinding

class DifficultySelectFragment : Fragment() {

    private var _binding: FragmentDifficultySelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDifficultySelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
    }

    private fun setupViews() {
        binding.btnBasic.setOnClickListener {
            // TODO: Pass difficulty level parameter to QuizPlayFragment when arguments are implemented
            findNavController().navigate(R.id.action_difficulty_to_quiz_play)
        }
        
        binding.btnAdvanced.setOnClickListener {
            // TODO: Pass difficulty level parameter to QuizPlayFragment when arguments are implemented
            findNavController().navigate(R.id.action_difficulty_to_quiz_play)
        }
        
        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
