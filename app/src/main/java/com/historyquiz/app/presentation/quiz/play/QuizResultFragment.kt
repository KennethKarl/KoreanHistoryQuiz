package com.historyquiz.app.presentation.quiz.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.historyquiz.app.R
import com.historyquiz.app.databinding.FragmentQuizResultBinding

/**
 * 퀴즈 결과 화면.
 * 실제 구현은 TASK-007 (QuizResultViewModel, 등)에서 수행.
 */
class QuizResultFragment : Fragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // TASK-007 진행 시 파라미터 전달받아 뷰 업데이트 구현

        binding.btnGoHome.setOnClickListener {
            findNavController().navigate(R.id.action_result_to_home)
        }
        
        binding.btnRetry.setOnClickListener {
            // 동일 난이도로 다시 시작
            findNavController().navigate(R.id.action_result_to_difficulty_select)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
