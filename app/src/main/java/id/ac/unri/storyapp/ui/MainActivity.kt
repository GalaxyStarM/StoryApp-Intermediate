package id.ac.unri.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import id.ac.unri.storyapp.R
import id.ac.unri.storyapp.adapter.LoadingStateAdapter
import id.ac.unri.storyapp.adapter.StoryAdapter
import id.ac.unri.storyapp.data.local.entity.Story
import id.ac.unri.storyapp.databinding.ActivityMainBinding
import id.ac.unri.storyapp.utils.animateVisibility
import id.ac.unri.storyapp.viewmodel.MainViewModel
import kotlinx.coroutines.launch

@ExperimentalPagingApi
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
                mainViewModel.getAllStories(token).observe(this@MainActivity) { listStory ->
                    setUpdateStories(listStory)
                }
            }
        }
    }

    private fun setUpdateStories(liststory: PagingData<Story>){
        val recyclerViewState = recyclerView.layoutManager?.onSaveInstanceState()
        listAdapter.submitData(lifecycle, liststory)
        recyclerView.layoutManager?.onRestoreInstanceState(recyclerViewState)
    }

    private fun setupRecyclerView() {
        recyclerView = binding?.rvStory!!
        recyclerView.layoutManager = LinearLayoutManager(this)

        listAdapter.addLoadStateListener { loadState ->
            if(loadState.source.refresh is LoadState.NotLoading
                && loadState.append.endOfPaginationReached
                && listAdapter.itemCount < 1
                || loadState.source.refresh is LoadState.Error) {
                binding?.apply {
                    imgNotFound.animateVisibility(true)
                    rvStory.animateVisibility(false)
                }
            } else {
                binding?.apply {
                    imgNotFound.animateVisibility(false)
                    rvStory.animateVisibility(true)
                }
            }
            binding?.srLayout?.isRefreshing = loadState.source.refresh is LoadState.Loading
        }

        try {
            recyclerView = binding?.rvStory!!
            recyclerView.apply {
                adapter = listAdapter.withLoadStateFooter(
                    footer = LoadingStateAdapter {
                        listAdapter.retry()
                    }
                )
                recyclerView.layoutManager = LinearLayoutManager(this@MainActivity)
            }
        }catch (e: NullPointerException) {
            e.printStackTrace()
        }
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
            R.id.menu_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}