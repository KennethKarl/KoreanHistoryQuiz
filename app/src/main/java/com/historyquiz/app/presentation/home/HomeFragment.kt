package com.historyquiz.app.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.historyquiz.app.MainActivity
import com.historyquiz.app.R
import com.historyquiz.app.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeState()
    }

    private fun setupViews() {
        binding.btnMenu.setOnClickListener {
            (activity as? MainActivity)?.openDrawer()
        }

        binding.btnStartBasicQuiz.setOnClickListener {
            val bundle = Bundle().apply { putString("level", "basic") }
            findNavController().navigate(R.id.action_home_to_quiz_play, bundle)
        }

        binding.btnStartAdvancedQuiz.setOnClickListener {
            val bundle = Bundle().apply { putString("level", "advanced") }
            findNavController().navigate(R.id.action_home_to_quiz_play, bundle)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    binding.tvStreakDays.text = "${state.streakDays}일 연속 학습 중!"
                    binding.tvQuestionsSolvedValue.text = "${state.questionsSolvedToday}문제"
                    binding.tvAccuracyValue.text = "${state.accuracyToday}%"
                    
                    if (state.lastScore != null) {
                        binding.cardRecentResult.visibility = View.VISIBLE
                        binding.tvRecentScore.text = "최근 점수: ${state.lastScore}점"
                    } else {
                        binding.cardRecentResult.visibility = View.GONE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
