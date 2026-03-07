package com.historyquiz.app.presentation.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.historyquiz.app.R
import com.historyquiz.app.data.datastore.UserPreferencesDataStore
import com.historyquiz.app.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    private val userPreferences: UserPreferencesDataStore by inject()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        startSplashAndNavigate()
    }

    private fun startSplashAndNavigate() {
        // 로고·앱이름 페이드인 애니메이션
        binding.ivLogo.animate().alpha(1f).setDuration(600).start()
        binding.tvAppName.animate().alpha(1f).setDuration(600).setStartDelay(200).start()

        viewLifecycleOwner.lifecycleScope.launch {
            delay(SPLASH_DURATION_MS)
            navigate()
        }
    }

    private suspend fun navigate() {
        val isOnboardingDone = userPreferences.isOnboardingDone.first()
        val isLoggedIn = Firebase.auth.currentUser != null

        val actionId = when {
            !isOnboardingDone -> R.id.action_splash_to_onboarding
            isLoggedIn -> R.id.action_splash_to_home
            else -> R.id.action_splash_to_login
        }

        // Fragment가 아직 화면에 붙어있을 때만 네비게이션 실행
        if (isAdded && view != null) {
            findNavController().navigate(actionId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val SPLASH_DURATION_MS = 1_500L
    }
}
