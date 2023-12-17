package id.ac.unri.storyapp.ui

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unri.storyapp.R
import id.ac.unri.storyapp.databinding.ActivityLoginBinding
import id.ac.unri.storyapp.ui.MainActivity.Companion.EXTRA_TOKEN
import id.ac.unri.storyapp.utils.Message
import id.ac.unri.storyapp.viewmodel.LoginViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private var binding: ActivityLoginBinding? = null
    private val loginViewModel: LoginViewModel by viewModels()
    private var loginJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.hide()
        userLogin()

        binding?.btnCreateAcc?.setOnClickListener{
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
        playPropertyAnimation()

    }

    private fun playPropertyAnimation() {
        ObjectAnimator.ofFloat(binding?.imgLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val title = ObjectAnimator.ofFloat(binding?.textView, View.ALPHA, 1f).setDuration(500)
        val desc = ObjectAnimator.ofFloat(binding?.textView2, View.ALPHA, 1f).setDuration(500)
        val edtEmail = ObjectAnimator.ofFloat(binding?.emailEdt, View.ALPHA, 1f).setDuration(500)
        val edtPass = ObjectAnimator.ofFloat(binding?.passwordEdt, View.ALPHA, 1f).setDuration(500)
        val btnLogin = ObjectAnimator.ofFloat(binding?.btnLogin, View.ALPHA, 1f).setDuration(500)
        val btnRegister = ObjectAnimator.ofFloat(binding?.btnCreateAcc, View.ALPHA, 1f).setDuration(500)

        AnimatorSet().apply {
            playSequentially(title, desc, edtEmail, edtPass, btnLogin, btnRegister)
            start()
        }
    }

    private fun userLogin(){

        binding?.btnLogin?.setOnClickListener{
            val email = binding?.emailEdt?.text.toString().trim()
            val password = binding?.passwordEdt?.text.toString().trim()
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Message.setMessage(this, getString(R.string.warning_input))
            }else{
                setLoadingState(true)
                lifecycle.coroutineScope.launchWhenResumed {
                    if (loginJob.isActive) loginJob.cancel()
                    loginJob = launch {
                        loginViewModel.login(email, password).collect { result ->
                            result.onSuccess { credential ->
                                credential.loginResult?.token?.let { token ->
                                    loginViewModel.saveAuthToken(token)
                                    Intent(this@LoginActivity, MainActivity::class.java).also {
                                        it.putExtra(EXTRA_TOKEN, token)
                                        startActivity(it)
                                        finish()
                                    }
                                }
                                Toast.makeText(this@LoginActivity, getString(R.string.success_login), Toast.LENGTH_SHORT).show()
                            }
                            result.onFailure {
                                Snackbar.make(binding!!.root, it.message.toString(), Snackbar.LENGTH_SHORT).show()
                                setLoadingState(false)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun setLoadingState(loading: Boolean) {
        when(loading) {
            true -> {
                binding?.btnLogin?.visibility = View.INVISIBLE
                binding?.progressBar?.visibility = View.VISIBLE
            }
            false -> {
                binding?.btnLogin?.visibility = View.VISIBLE
                binding?.progressBar?.visibility = View.INVISIBLE
            }
        }
    }
}
