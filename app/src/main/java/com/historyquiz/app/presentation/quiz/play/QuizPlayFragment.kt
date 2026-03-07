package com.historyquiz.app.presentation.quiz.play

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.historyquiz.app.R
import com.historyquiz.app.databinding.FragmentQuizPlayBinding
import com.historyquiz.app.domain.model.Question
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class QuizPlayFragment : Fragment() {

    private var _binding: FragmentQuizPlayBinding? = null
    private val binding get() = _binding!!

    private val viewModel: QuizPlayViewModel by viewModel()
    
    private val optionButtons by lazy {
        listOf(
            binding.btnOption1,
            binding.btnOption2,
            binding.btnOption3,
            binding.btnOption4,
            binding.btnOption5
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizPlayBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViews()
        observeState()
        
        // TODO: SafeArgs로 전달받은 level 사용 (현재는 임시로 "basic")
        viewModel.loadQuestions("basic")
    }

    private fun setupViews() {
        binding.btnClose.setOnClickListener {
            findNavController().navigateUp()
        }
        
        optionButtons.forEachIndexed { index, button ->
            // 선택지는 1번부터 시작하므로 index + 1
            button.setOnClickListener {
                viewModel.submitAnswer(index + 1)
            }
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is QuizUiState.Loading -> showLoading()
                        is QuizUiState.Error -> showError(state.message)
                        is QuizUiState.Ready -> showQuestion(state.question, state.currentIndex, state.totalCount)
                        is QuizUiState.Answered -> showAnswerResult(state)
                        is QuizUiState.Finished -> moveToResult(state.totalScore, state.correctCount, state.totalCount)
                    }
                }
            }
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.groupQuizContent.visibility = View.GONE
        binding.tvError.visibility = View.GONE
    }

    private fun showError(message: String) {
        binding.progressBar.visibility = View.GONE
        binding.groupQuizContent.visibility = View.GONE
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = message
        
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showQuestion(question: Question, index: Int, total: Int) {
        binding.progressBar.visibility = View.GONE
        binding.tvError.visibility = View.GONE
        binding.groupQuizContent.visibility = View.VISIBLE
        
        // 진행률 업데이트
        binding.tvProgressCount.text = "${index + 1} / $total"
        binding.progressHorizontal.max = total
        binding.progressHorizontal.progress = index + 1
        
        // 문제 본문
        binding.tvQuestionContent.text = question.content
        
        // 선택지 초기화
        resetOptionButtons()
        
        // 4지 / 5지 선다 처리
        question.choices.forEachIndexed { i, choice ->
            if (i < optionButtons.size) {
                optionButtons[i].visibility = View.VISIBLE
                optionButtons[i].text = "${i + 1}. $choice"
            }
        }
        
        // 4지선다인 경우 5번 버튼 숨김
        if (question.choices.size < 5) {
            binding.btnOption5.visibility = View.GONE
        }
    }

    private fun showAnswerResult(state: QuizUiState.Answered) {
        // 모든 버튼 비활성화 (중복 클릭 방지)
        optionButtons.forEach { it.isEnabled = false }
        
        val selectedIndex = state.selectedAnswer - 1
        val correctIndex = state.question.answer - 1
        
        // 정답 버튼을 초록색으로
        if (correctIndex in optionButtons.indices) {
            setButtonColor(optionButtons[correctIndex], R.color.seed) // 임시로 seed color (초록 계열)
        }
        
        // 오답을 선택했다면 해당 버튼을 빨간색으로
        if (!state.isCorrect && selectedIndex in optionButtons.indices) {
            setButtonColor(optionButtons[selectedIndex], android.R.color.holo_red_light)
        }
    }

    private fun resetOptionButtons() {
        val defaultColor = ContextCompat.getColor(requireContext(), R.color.md_theme_surfaceContainerHighest)
        val defaultTextColor = ContextCompat.getColor(requireContext(), R.color.md_theme_onSurface)
        
        optionButtons.forEach { btn ->
            btn.isEnabled = true
            btn.backgroundTintList = ColorStateList.valueOf(defaultColor)
            btn.setTextColor(defaultTextColor)
        }
    }

    private fun setButtonColor(button: Button, colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        val onColor = ContextCompat.getColor(requireContext(), R.color.md_theme_onPrimary)
        
        button.backgroundTintList = ColorStateList.valueOf(color)
        button.setTextColor(onColor)
    }

    private fun moveToResult(score: Int, correct: Int, total: Int) {
        // TODO: TASK-007의 결과 화면으로 이동, 점수 데이터 전달
        findNavController().navigate(R.id.action_quiz_play_to_result)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
