package com.johnturkson.sync.ui

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.fragment.app.commit
import com.johnturkson.sync.SyncApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    
    private val viewModel: MainViewModel by viewModels()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
        
        setContent {
            val visible by viewModel.authenticated.collectAsState()
            
            if (visible) {
                SyncApp()
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(BiometricAuthenticationFragment(), BiometricAuthenticationFragment::class.qualifiedName)
        }
    }
    
    override fun onPause() {
        super.onPause()
        viewModel.unauthenticate()
    }
}
