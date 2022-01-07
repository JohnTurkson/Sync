package com.johnturkson.sync.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commit
import com.johnturkson.sync.SyncApp
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalMaterial3Api
@AndroidEntryPoint
class MainActivity : FragmentActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE,
        )
        
        setContent {
            SyncApp()
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        supportFragmentManager.commit {
            add(BiometricAuthenticationFragment(),
                BiometricAuthenticationFragment::class.qualifiedName)
        }
    }
    
    override fun onPause() {
        super.onPause()
        viewModel.unauthenticate()
    }
}
