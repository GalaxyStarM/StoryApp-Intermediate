package id.ac.unri.storyapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingData
import androidx.recyclerview.widget.ListUpdateCallback
import id.ac.unri.storyapp.adapter.StoryAdapter
import id.ac.unri.storyapp.data.AuthRepository
import id.ac.unri.storyapp.data.StoryRepository
import id.ac.unri.storyapp.data.local.entity.Story
import id.ac.unri.storyapp.utils.CoroutinesTestRule
import id.ac.unri.storyapp.utils.DataDummy
import id.ac.unri.storyapp.utils.PagingTestDataSource
import id.ac.unri.storyapp.utils.getOrAwaitValue
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner

@ExperimentalCoroutinesApi
@ExperimentalPagingApi
@RunWith(MockitoJUnitRunner::class)
class MainViewModelTest{

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()
    private lateinit var mainViewModel: MainViewModel

    @Mock private lateinit var storyRepository: StoryRepository

    @Mock private lateinit var authRepository: AuthRepository

    private val token = "auth_token"

    @Test
    fun `when Get Story Should Not Null and Return Data`() = runTest {
        val dummyStory = DataDummy.generateDummyListStory()
        val data: PagingData<Story> = PagingTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(authRepository, storyRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALL,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = coroutinesTestRule.testDispatcher,
            mainDispatcher = coroutinesTestRule.testDispatcher
        )

        differ.submitData(actualStory)

        advanceUntilIdle()

        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }

    @Test
    fun `Get correct number of stories`() = runTest {
        val dummyStory = DataDummy.generateDummyListStory()
        val data: PagingData<Story> = PagingTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(authRepository, storyRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALL,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = coroutinesTestRule.testDispatcher,
            mainDispatcher = coroutinesTestRule.testDispatcher
        )

        differ.submitData(actualStory)

        advanceUntilIdle()

        val expectedSize = dummyStory.size
        val actualSize = differ.snapshot().size
        assertEquals(expectedSize, actualSize)
    }

    @Test
    fun `Get first story successfully`() = runTest {
        val dummyStory = DataDummy.generateDummyListStory()
        val data: PagingData<Story> = PagingTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(authRepository, storyRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALL,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = coroutinesTestRule.testDispatcher,
            mainDispatcher = coroutinesTestRule.testDispatcher
        )

        differ.submitData(actualStory)

        advanceUntilIdle()
        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStory[0].id, differ.snapshot()[0]?.id)
    }

    @Test
    fun `when Get Story Empty Should Return No Data`() = runTest {
        val dummyStory = DataDummy.generateEmptyDummyListStory()
        val data: PagingData<Story> = PagingTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(authRepository, storyRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALL,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = coroutinesTestRule.testDispatcher,
            mainDispatcher = coroutinesTestRule.testDispatcher
        )

        differ.submitData(actualStory)
        Assert.assertEquals(0, differ.snapshot().size)
    }

    @Test
    fun `Get all stories successfully`() = runTest {
        val dummyStory = DataDummy.generateDummyListStory()
        val data: PagingData<Story> = PagingTestDataSource.snapshot(dummyStory)
        val stories = MutableLiveData<PagingData<Story>>()
        stories.value = data

        `when`(storyRepository.getAllStories(token)).thenReturn(stories)

        mainViewModel = MainViewModel(authRepository, storyRepository)
        val actualStory = mainViewModel.getAllStories(token).getOrAwaitValue()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALL,
            updateCallback = noopListUpdateCallback,
            workerDispatcher = coroutinesTestRule.testDispatcher,
            mainDispatcher = coroutinesTestRule.testDispatcher
        )

        differ.submitData(actualStory)

        advanceUntilIdle()
        Assert.assertNotNull(differ.snapshot())
        assertEquals(dummyStory.size, differ.snapshot().size)
        assertEquals(dummyStory[0], differ.snapshot()[0])
    }




}

private val noopListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}