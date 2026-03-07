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
import com.historyquiz.app.R
import com.historyquiz.app.databinding.FragmentHomeBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    // TASK-005 ViewModel
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
        binding.btnStartBasicQuiz.setOnClickListener {
            findNavController().navigate(R.id.action_home_to_difficulty_select)
        }
        
        binding.btnStartAdvancedQuiz.setOnClickListener {
            // "심화 퀴즈 시작" 버튼을 위한 argument 전달 등은 TASK-006 개발 시 보강 (현재는 스텁)
            findNavController().navigate(R.id.action_home_to_difficulty_select)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    // 스켈레톤 로딩 처리는 일단 생략하고 텍스트만 업데이트
                    
                    binding.tvQuestionsSolvedValue.text = "${state.questionsSolvedToday}문제"
                    binding.tvAccuracyValue.text = "${state.accuracyToday}%"
                    binding.tvStreakDays.text = "${state.streakDays}일 연속 학습 중!"
                    
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
