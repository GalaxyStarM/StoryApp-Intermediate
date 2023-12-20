package id.ac.unri.storyapp.utils

import id.ac.unri.storyapp.data.local.entity.Story
import id.ac.unri.storyapp.data.remote.response.ListStoryItem
import id.ac.unri.storyapp.data.remote.response.StoryResponse

object DataDummy {

    fun generateDummyStoryResponse(): List<StoryResponse> {
        val error = false
        val message = "Stories fetched succesfully"
        val listStory = mutableListOf<ListStoryItem>()

        for (i in 0 until 10 ) {
            val story = ListStoryItem(
                id = "story-OKk7SuFbs6vS87PG",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1703084563921_ltnjw18S.jpg",
                createdAt = "2023-12-20T15:02:43.923Z",
                name = "munmun",
                description = "cuba lagi",
                lon = 0.46846846846846846,
                lat = 0.46846846846846846
            )
            listStory.add(story)
        }
        return listOf(StoryResponse(listStory, error, message))
    }

    fun generateDummyListStory(): List<Story> {
        val items = arrayListOf<Story>()

        for (i in 0 .. 100){
            val story = Story(
                id = "story-OKk7SuFbs6vS87PG",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos-1703084563921_ltnjw18S.jpg",
                createdAt = "2023-12-20T15:02:43.923Z",
                name = "munmun",
                description = "cuba lagi",
                lon = 0.46846846846846846,
                lat = 0.46846846846846846
            )
            items.add(story)
        }
        return items
    }

    fun generateEmptyDummyListStory(): List<Story> {
        return emptyList()
    }
}