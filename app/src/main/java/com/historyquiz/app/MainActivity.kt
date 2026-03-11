package com.historyquiz.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.historyquiz.app.data.datastore.UserPreferencesDataStore
import com.historyquiz.app.databinding.ActivityMainBinding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    
    private val userPreferencesDataStore: UserPreferencesDataStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        // 테마 적용 (setContentView 이전, super.onCreate 이전에 실행하는 것이 안전)
        applyAppTheme()
        
        installSplashScreen()
        super.onCreate(savedInstanceState)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
        
        // Drawer 설정
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.statisticsFragment, R.id.settingsFragment),
            binding.drawerLayout
        )
        
        // NavigationView와 NavController 연결
        binding.navView.setupWithNavController(navController)
        
        // 홈 화면에서만 Drawer를 열 수 있도록 제어하거나, 
        // 특정 화면에서 햄버거 아이콘이 보이도록 설정하려면 아래 주석 해제 후 구현
        // setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun applyAppTheme() {
        val theme = runBlocking { userPreferencesDataStore.appTheme.first() }
        when (theme) {
            "celadon" -> setTheme(R.style.Theme_HistoryQuiz_Celadon)
            else -> setTheme(R.style.Theme_HistoryQuiz_Dancheong)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    
    fun openDrawer() {
        binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
    }
}
