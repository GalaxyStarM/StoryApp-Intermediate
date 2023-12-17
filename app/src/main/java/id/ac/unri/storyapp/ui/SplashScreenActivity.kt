package id.ac.unri.storyapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unri.storyapp.databinding.ActivitySplashScreenBinding
import id.ac.unri.storyapp.viewmodel.SplashScreenViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SplashScreenActivity : AppCompatActivity() {

    private var binding: ActivitySplashScreenBinding? = null
    private val splashViewModel: SplashScreenViewModel by viewModels()
    private var splashScreenJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()

        show()
    }

    private fun show() {
        lifecycle.coroutineScope.launchWhenCreated {
            splashScreenJob = launch{
                splashViewModel.getAuthToken().collect{ token ->
                    if(token.isNullOrEmpty()){
                        binding?.imageView?.alpha = 0f
                        binding?.imageView?.animate()?.setDuration(5000)?.alpha(1f)?.withEndAction{
                            val intent = Intent(this@SplashScreenActivity, LoginActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }else{
                        binding?.imageView?.alpha = 0f
                        binding?.imageView?.animate()?.setDuration(5000)?.alpha(1f)?.withEndAction {
                            val intent = Intent(this@SplashScreenActivity, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }

            }
        }
    }
}