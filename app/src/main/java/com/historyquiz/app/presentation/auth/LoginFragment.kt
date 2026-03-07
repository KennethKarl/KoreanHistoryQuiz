package com.historyquiz.app.presentation.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.historyquiz.app.R
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
        // TODO(TASK-003): AuthViewModel 연결 및 실제 Firebase Auth 구현
        // 현재: 버튼 클릭 시 HomeFragment로 직행 (테스트용)
        binding.btnGoogleSignIn.setOnClickListener {
            findNavController().navigate(R.id.action_login_to_home)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
