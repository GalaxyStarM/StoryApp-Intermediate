package id.ac.unri.storyapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import id.ac.unri.storyapp.data.remote.response.ListStoryItem
import id.ac.unri.storyapp.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    private var binding : ActivityDetailBinding? = null

    companion object{
        const val EXTRA_STORY_USER = "extra_story_user"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getData()
    }

    private fun getData(){
        val story = intent.getParcelableExtra<ListStoryItem>(EXTRA_STORY_USER)
        binding?.apply {
            tvStoryUsername.text = story?.name
            tvStoryDesc.text = story?.description

            Glide.with(this@DetailActivity)
                .load(story?.photoUrl)
                .into(ivStory)
        }
    }

    override fun onSupportNavigateUp(): Boolean{
        finish()
        return super.onSupportNavigateUp()
    }
}