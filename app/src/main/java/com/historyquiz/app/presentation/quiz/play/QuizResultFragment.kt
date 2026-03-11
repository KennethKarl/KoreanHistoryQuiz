package com.historyquiz.app.presentation.quiz.play

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
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
        
        // 이전 화면(QuizPlay)에서 전달받은 인수
        val level        = arguments?.getString("level") ?: "basic"
        val totalScore   = arguments?.getInt("totalScore",   0) ?: 0
        val correctCount = arguments?.getInt("correctCount", 0) ?: 0
        val totalCount   = arguments?.getInt("totalCount",   0) ?: 0

        // 점수 및 정답 수 표시
        binding.tvScoreValue.text   = "${totalScore}점"
        binding.tvCorrectCount.text = "정답 ${correctCount} / 전체 ${totalCount}"

        binding.btnGoHome.setOnClickListener {
            findNavController().navigate(R.id.action_result_to_home)
        }

        binding.btnRetry.setOnClickListener {
            // 동일 난이도로 퀴즈 재시작
            findNavController().navigate(
                R.id.action_result_to_quiz_play,
                bundleOf("level" to level)
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
