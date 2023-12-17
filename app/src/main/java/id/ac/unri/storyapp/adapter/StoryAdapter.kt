package id.ac.unri.storyapp.adapter


import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import id.ac.unri.storyapp.data.remote.response.ListStoryItem
import id.ac.unri.storyapp.databinding.ItemStoryBinding
import id.ac.unri.storyapp.ui.DetailActivity
import id.ac.unri.storyapp.ui.DetailActivity.Companion.EXTRA_STORY_USER

class StoryAdapter: ListAdapter<ListStoryItem, StoryAdapter.MyViewHolder>(DIFF_CALL) {

    companion object {
        private val DIFF_CALL = object : DiffUtil.ItemCallback<ListStoryItem>(){
            override fun areItemsTheSame(oldItem: ListStoryItem, newItem: ListStoryItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: ListStoryItem,
                newItem: ListStoryItem
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class MyViewHolder(val binding: ItemStoryBinding):
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val binding = ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent,false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val listStory = getItem(position)
        holder.binding.apply {
            tvStoryUsername.text = listStory.name
            tvStoryDesc.text = listStory.description
            Glide.with(holder.itemView)
                .load(listStory.photoUrl)
                .into(ivStory)

            root.setOnClickListener{
                val optionsCompat : ActivityOptionsCompat =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                        root.context as Activity,
                        Pair(tvStoryUsername, "name"),
                        Pair(tvStoryDesc, "description"),
                        Pair(ivStory, "story_iamge")
                    )
                Intent(root.context, DetailActivity::class.java).also { intent ->
                    intent.putExtra(EXTRA_STORY_USER, listStory)
                    root.context.startActivity(intent, optionsCompat.toBundle())
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return currentList.size
    }
}