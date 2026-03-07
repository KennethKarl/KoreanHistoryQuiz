package com.historyquiz.app.presentation.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.historyquiz.app.R
import com.historyquiz.app.data.datastore.UserPreferencesDataStore
import com.historyquiz.app.databinding.FragmentOnboardingBinding
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private val userPreferences: UserPreferencesDataStore by inject()

    private val pages: List<OnboardingPage> by lazy {
        listOf(
            OnboardingPage(
                titleRes = R.string.onboarding_page1_title,
                descRes = R.string.onboarding_page1_desc,
                imageRes = R.drawable.ic_onboarding_quiz
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_page2_title,
                descRes = R.string.onboarding_page2_desc,
                imageRes = R.drawable.ic_onboarding_study
            ),
            OnboardingPage(
                titleRes = R.string.onboarding_page3_title,
                descRes = R.string.onboarding_page3_desc,
                imageRes = R.drawable.ic_onboarding_stats
            )
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewPager()
        setupButtons()
    }

    private fun setupViewPager() {
        val adapter = OnboardingPagerAdapter(pages)
        binding.vpOnboarding.adapter = adapter

        // TabLayout을 도트 인디케이터로 연결
        TabLayoutMediator(binding.tabIndicator, binding.vpOnboarding) { _, _ -> }.attach()

        // 페이지 변경 시 버튼 텍스트 업데이트
        binding.vpOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateActionButton(position)
            }
        })
    }

    private fun setupButtons() {
        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }

        binding.btnAction.setOnClickListener {
            val current = binding.vpOnboarding.currentItem
            if (current < pages.lastIndex) {
                binding.vpOnboarding.currentItem = current + 1
            } else {
                finishOnboarding()
            }
        }
    }

    private fun updateActionButton(position: Int) {
        if (position == pages.lastIndex) {
            binding.btnAction.setText(R.string.onboarding_start)
            binding.btnSkip.visibility = View.INVISIBLE
        } else {
            binding.btnAction.setText(R.string.onboarding_next)
            binding.btnSkip.visibility = View.VISIBLE
        }
    }

    /**
     * 온보딩 완료 처리:
     * 1. DataStore에 `is_onboarding_done = true` 저장
     * 2. LoginFragment로 이동 (back stack 전체 클리어)
     */
    private fun finishOnboarding() {
        viewLifecycleOwner.lifecycleScope.launch {
            userPreferences.setOnboardingDone(true)
            if (isAdded && view != null) {
                findNavController().navigate(R.id.action_onboarding_to_login)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
