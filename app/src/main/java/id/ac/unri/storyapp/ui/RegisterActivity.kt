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
import id.ac.unri.storyapp.databinding.ActivityRegisterBinding
import id.ac.unri.storyapp.utils.Message
import id.ac.unri.storyapp.viewmodel.RegisterViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RegisterActivity : AppCompatActivity() {

    private var binding: ActivityRegisterBinding? = null
    private val registerViewModel: RegisterViewModel by viewModels()
    private var registerJob: Job = Job()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.title = resources.getString(R.string.register)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        setData()
        binding?.btnbacklogin?.setOnClickListener{
            val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(intent)
        }
        playPropertyAnimation()
    }

    private fun playPropertyAnimation() {
        ObjectAnimator.ofFloat(binding?.imgRegister, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 5000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val desc = ObjectAnimator.ofFloat(binding?.titleTextView, View.ALPHA, 1f).setDuration(300)
        val edtUsername = ObjectAnimator.ofFloat(binding?.usernameEdt, View.ALPHA, 1f).setDuration(300)
        val edtEmail = ObjectAnimator.ofFloat(binding?.emailEdt, View.ALPHA, 1f).setDuration(300)
        val edtPass = ObjectAnimator.ofFloat(binding?.passwordEdt, View.ALPHA, 1f).setDuration(300)
        val btnRegister = ObjectAnimator.ofFloat(binding?.btnRegister, View.ALPHA, 1f).setDuration(300)
        val btnBackLogin = ObjectAnimator.ofFloat(binding?.btnbacklogin, View.ALPHA, 1f).setDuration(300)

        AnimatorSet().apply {
            playSequentially( desc, edtUsername, edtEmail, edtPass, btnRegister, btnBackLogin)
            start()
        }
    }

    private fun setData(){
        binding?.apply {
            btnRegister.setOnClickListener {
                val name = binding?.usernameEdt?.text.toString().trim()
                val email = binding?.emailEdt?.text.toString().trim()
                val password = binding?.passwordEdt?.text.toString().trim()
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
                    Message.setMessage(this@RegisterActivity, getString(R.string.warning_input))
                } else {
                    setLoadingState(true)
                    lifecycle.coroutineScope.launchWhenResumed {
                        if (registerJob.isActive) registerJob.cancel()
                        registerJob = launch {
                            registerViewModel.register(name, email, password).collect { result ->
                                result.onSuccess {
                                    Toast.makeText(this@RegisterActivity, getString(R.string.success_regAcc), Toast.LENGTH_SHORT).show()

                                    Intent(this@RegisterActivity, LoginActivity::class.java).also {
                                        startActivity(it)
                                        finish()
                                    }
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
    }

    private fun setLoadingState(loading: Boolean) {
        when(loading) {
            true -> {
                binding?.btnRegister?.visibility = View.INVISIBLE
                binding?.progressBar?.visibility = View.VISIBLE
            }
            false -> {
                binding?.btnRegister?.visibility = View.VISIBLE
                binding?.progressBar?.visibility = View.INVISIBLE
            }
        }
    }
}