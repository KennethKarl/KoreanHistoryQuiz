package com.historyquiz.app.presentation.quiz.play

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
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

        // nav_graph argument 또는 Bundle로 전달받은 level 사용
        val level = arguments?.getString("level") ?: "basic"
        viewModel.loadQuestions(level)
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

        binding.btnNext.setOnClickListener {
            viewModel.moveToNext()
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
        
        // 이미지 로딩 로직
        // TASK-014: id 형식 변경 "seed_basic_69_1" → "69_basic_1"
        // id를 그대로 이미지 경로로 사용: images/{id}.png
        var imageLoaded = false
        try {
            val imageFileName = "images/${question.id}.png"
            val inputStream = requireContext().assets.open(imageFileName)
            val drawable = android.graphics.drawable.Drawable.createFromStream(inputStream, null)
            binding.ivQuestionImage.setImageDrawable(drawable)
            binding.ivQuestionImage.visibility = View.VISIBLE
            binding.tvRoundNumber.text = question.category
            binding.tvRoundNumber.visibility = View.VISIBLE
            imageLoaded = true
        } catch (e: Exception) {
            // 이미지 파일이 없거나 로드 실패 시 무시
        }

        if (!imageLoaded) {
            binding.ivQuestionImage.visibility = View.GONE
            binding.tvRoundNumber.visibility = View.GONE
        }

        // 문제 본문 텍스트:
        //  - 이미지가 표시됐으면 → 텍스트 숨김 (이미지가 곧 문제)
        //  - 이미지가 없고 파싱 실패 메시지면 → 텍스트 숨김 (빈 화면, 이미지 생성 전 임시)
        //  - 이미지가 없고 실제 내용이면 → 텍스트 표시
        if (imageLoaded || question.content.startsWith("(문제 파싱 실패)")) {
            binding.tvQuestionContent.visibility = View.GONE
        } else {
            binding.tvQuestionContent.visibility = View.VISIBLE
            binding.tvQuestionContent.text = question.content
        }
        
        // 선택지 초기화
        resetOptionButtons()
        binding.btnNext.visibility = View.GONE
        
        // 4지 / 5지 선다 처리
        // ①②③④ 만 표시하는 경우:
        //   1) 이미지가 표시 중 (텍스트는 이미지에 이미 포함)
        //   2) 선택지 파싱 실패 ("(파싱 실패" 로 시작)
        // 나머지: "N. 실제텍스트" 표시
        val optionsParseFailed = question.options.firstOrNull()?.startsWith("(파싱 실패") == true
        val useCircledOnly = imageLoaded || optionsParseFailed
        val circledNums = listOf("①", "②", "③", "④", "⑤")
        question.options.forEachIndexed { i, choice ->
            if (i < optionButtons.size) {
                optionButtons[i].visibility = View.VISIBLE
                optionButtons[i].text = if (useCircledOnly) {
                    circledNums.getOrElse(i) { "${i + 1}" }
                } else {
                    "${i + 1}. $choice"
                }
            }
        }
        
        // 4지선다인 경우 5번 버튼 숨김
        if (question.options.size < 5) {
            binding.btnOption5.visibility = View.GONE
        }
    }

    private fun showAnswerResult(state: QuizUiState.Answered) {
        // 모든 버튼 비활성화 (중복 클릭 방지)
        optionButtons.forEach { it.isEnabled = false }
        
        val selectedIndex = state.selectedAnswer - 1
        val correctIndex = state.question.answerIndex
        
        // 정답 버튼을 초록색으로
        if (correctIndex in optionButtons.indices) {
            setButtonColor(optionButtons[correctIndex], R.color.quiz_correct)
        }
        
        // 오답을 선택했다면 해당 버튼을 빨간색으로
        if (!state.isCorrect && selectedIndex in optionButtons.indices) {
            setButtonColor(optionButtons[selectedIndex], R.color.quiz_wrong)
        }

        // 정답 확인 후 "다음" 버튼 표시
        binding.btnNext.visibility = View.VISIBLE
    }

    private fun resetOptionButtons() {
        val defaultColor = ContextCompat.getColor(requireContext(), R.color.quiz_option_default)
        val defaultTextColor = ContextCompat.getColor(requireContext(), R.color.color_ink)
        
        optionButtons.forEach { btn ->
            btn.isEnabled = true
            btn.backgroundTintList = ColorStateList.valueOf(defaultColor)
            btn.setTextColor(defaultTextColor)
        }
    }

    private fun setButtonColor(button: Button, colorResId: Int) {
        val color = ContextCompat.getColor(requireContext(), colorResId)
        val onColor = ContextCompat.getColor(requireContext(), R.color.md_theme_on_primary)
        
        button.backgroundTintList = ColorStateList.valueOf(color)
        button.setTextColor(onColor)
    }

    private fun moveToResult(score: Int, correct: Int, total: Int) {
        val level = arguments?.getString("level") ?: "basic"
        findNavController().navigate(
            R.id.action_quiz_play_to_result,
            bundleOf(
                "level"        to level,
                "totalScore"   to score,
                "correctCount" to correct,
                "totalCount"   to total
            )
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
