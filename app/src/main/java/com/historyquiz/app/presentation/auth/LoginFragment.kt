package com.historyquiz.app.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.historyquiz.app.databinding.FragmentLoginBinding

/**
 * 로그인 화면 스텁.
 * Google 로그인 실제 구현은 TASK-003 (AuthViewModel, SignInWithGoogleUseCase)에서 수행.
 */
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // TASK-003에서 AuthViewModel 연결 및 Google Sign-In 로직 구현
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
