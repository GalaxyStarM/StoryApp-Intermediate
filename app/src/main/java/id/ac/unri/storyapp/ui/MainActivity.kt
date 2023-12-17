package id.ac.unri.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unri.storyapp.R
import id.ac.unri.storyapp.adapter.StoryAdapter
import id.ac.unri.storyapp.data.remote.response.ListStoryItem
import id.ac.unri.storyapp.databinding.ActivityMainBinding
import id.ac.unri.storyapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@Suppress("DEPRECATION")
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    companion object{
        const val EXTRA_TOKEN = "extra_token"
    }
    private var binding: ActivityMainBinding? = null
    private var token: String = " "

    private lateinit var recyclerView: RecyclerView
    private lateinit var listAdapter: StoryAdapter
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar(binding?.toolbar)

        token = intent.getStringExtra(EXTRA_TOKEN).toString()

        listAdapter = StoryAdapter()
        recyclerView = binding?.rvStory!!
        recyclerView.adapter = listAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launchWhenCreated {
            launch {
                mainViewModel.getAuthToken().collect {itToken ->
                    if (!itToken.isNullOrEmpty())
                        token = itToken
                    getAllStories(token)
                    setSwipeLayout()
                }
            }
        }

        binding?.fabAddStory?.setOnClickListener{
            Intent(this, AddStoryActivity::class.java).also { intent ->
                startActivity(intent)
            }
        }
    }

    private fun setSwipeLayout(){
        binding?.srLayout?.setOnRefreshListener {
            getAllStories(token)
            binding!!.srLayout.isRefreshing = false
        }
    }

    private fun getAllStories(token:String){
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                mainViewModel.getAllStories(token).collect{ result ->
                    result.onSuccess {
                        val response = result.getOrNull()
                        val liststory = response?.listStory as List<ListStoryItem>
                        Log.d("MainActivity", "List Story Size: ${liststory.size}")

                        setupRecyclerView()
                        setUpadateStories(liststory)
                    }
                    result.onFailure {
                        val response = result.exceptionOrNull()
                        response?.printStackTrace()
                    }
                }
            }
        }
    }

    private fun setUpadateStories(liststory: List<ListStoryItem>){
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        listAdapter.submitList(liststory)
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun setupRecyclerView() {
        recyclerView = binding?.rvStory!!
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = listAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.menu_logout -> {
                mainViewModel.saveAuthToken("")
                finish()
                true
            }
            R.id.menu_language -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}