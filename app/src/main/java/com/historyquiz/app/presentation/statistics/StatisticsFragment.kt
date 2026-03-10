package com.historyquiz.app.presentation.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.historyquiz.app.databinding.FragmentStatisticsBinding
import com.historyquiz.app.databinding.ItemPeriodStatsBinding
import com.historyquiz.app.domain.usecase.statistics.PeriodStats
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: StatisticsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        observeState()
    }

    private fun setupViews() {
        binding.btnRefresh.setOnClickListener {
            viewModel.loadStatistics()
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collectLatest { state ->
                    when (state) {
                        is StatisticsUiState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                        is StatisticsUiState.Success -> {
                            binding.progressBar.visibility = View.GONE
                            val stats = state.statistics
                            
                            bindPeriodStats(binding.layoutDaily, stats.daily)
                            bindPeriodStats(binding.layoutWeekly, stats.weekly)
                            bindPeriodStats(binding.layoutMonthly, stats.monthly)
                            
                            binding.tvBasicCount.text = "${stats.levelDistribution["basic"] ?: 0}회"
                            binding.tvAdvancedCount.text = "${stats.levelDistribution["advanced"] ?: 0}회"
                        }
                        is StatisticsUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun bindPeriodStats(itemBinding: ItemPeriodStatsBinding, stats: PeriodStats) {
        itemBinding.tvPeriodName.text = stats.periodName
        itemBinding.tvTotalQuestions.text = stats.totalQuestions.toString()
        itemBinding.tvCorrectQuestions.text = stats.correctQuestions.toString()
        itemBinding.tvAccuracy.text = "${stats.accuracy}%"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
