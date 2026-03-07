package com.historyquiz.app.core.util

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Fragment에서 StateFlow/SharedFlow를 안전하게 수집한다.
 * Lifecycle.State.STARTED 상태에서만 수집 → 백그라운드 시 자동 중단.
 *
 * 사용 예:
 *   viewLifecycleOwner.collectWhenStarted(viewModel.uiState) { state ->
 *       render(state)
 *   }
 */
fun <T> Fragment.collectWhenStarted(
    flow: Flow<T>,
    action: suspend (T) -> Unit,
) {
    viewLifecycleOwner.lifecycleScope.launch {
        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collect { action(it) }
        }
    }
}
