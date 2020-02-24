package hu.ppke.itk.mindtechpokemon.basic

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import hu.ppke.itk.mindtechpokemon.*
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlin.coroutines.CoroutineContext
import kotlin.math.max

class Listing<T : ModelBase>(
    parentContext: CoroutineContext,
    private val localSource: LiveData<List<T>>,
    private val config: ListingConfig = ListingConfig(),
    private val networkLoadCall: suspend (from: Int, count: Int, delete: Boolean, state: MutableLiveData<NetworkState>) -> LoadResult?
) : CoroutineScope {
    override val coroutineContext: CoroutineContext = SupervisorJob(parentContext[Job]) + Dispatchers.Default
    private val TAG = "Listing"
    data class LoadResult(val loadedCount: Int, val totalCount: Int)
    data class ListingState<T>(val data: List<T>, val totalItemCount: Int, val networkState: NetworkState)
    data class ListingConfig(
        val showLoadingWhenEmpty: Boolean = true,
        val clearOnFirstLoad: Boolean = true
    )

    private val _networkState = MediatorLiveData<NetworkState>()
    val networkState: LiveData<NetworkState> = _networkState
    private suspend fun createCallState(): MutableLiveData<NetworkState> = createCallState(_networkState)

    private var itemCount = 0
    private var totalCount =
        UNKNOWN_ITEM_COUNT
    private val loadMutex = Mutex()

    private var hasError = false
    private val hasLoadingItem get() = itemCount < totalCount
    private val hasEmptyItem get() = totalCount == 0

    private val listingState: LiveData<ListingState<T>> = MediatorLiveData<ListingState<T>>().apply {
        value = ListingState(
            emptyList(),
            -1,
            NetworkState.LOADING
        )
        addSource(localSource) {
            value = value?.copy(data = it, totalItemCount = if (it.isNotEmpty()) max(it.size, totalCount) else totalCount)
        }
        addSource(_networkState) {
            value = value?.copy(totalItemCount = it.total?.toInt() ?: totalCount, networkState = it)
        }
    }

    private var processJob: Job? = null
        set(value) = synchronized(this) {
            if (field != value) field?.cancel()
            field = value
        }
    val displayList: LiveData<List<ListItem<T>>> = MediatorLiveData<List<ListItem<T>>>().apply {
        addSource(listingState) { state ->
            Log.v(this@Listing.TAG, "process started: ${state.data.size}")
            processJob = launch {
                processState(state)?.let { displayItems ->
                    Log.v(this@Listing.TAG, "process finished: ${state.data.size}")
                    if (this.isActive) postValue(displayItems)
                }
            }
        }
    }

    fun isEmpty() = hasEmptyItem

    private suspend fun processState(state: ListingState<T>): List<ListItem<T>>? = withContext(Dispatchers.Default) {
        totalCount = state.totalItemCount
        hasError = state.networkState.status == Status.FAILED

        val displayList: MutableList<ListItem<T>> = state.data.map {
            if (this.isActive) ListItem.Data(it) else return@withContext null
        }.toMutableList()

        val nextIndex = displayList.size
        if (hasError) {
            displayList.add(ListItem.Error(::retry))
        } else if (displayList.isEmpty()) {
            if (hasEmptyItem) displayList.add(ListItem.Empty())
            else if (config.showLoadingWhenEmpty) displayList.add(
                ListItem.Loading(
                    nextIndex
                )
            )
        } else if (hasLoadingItem) {
            displayList.add(
                ListItem.Loading(
                    nextIndex
                )
            )
        }
        return@withContext displayList
    }

    init {
        launch { loadFirstPage(config.clearOnFirstLoad) }
    }

    fun onItemBound(position: Int, type: Int) {
        val shouldLoad = !hasError && when (type) {
            ListItem.VIEW_TYPE_DATA -> itemCount - position <= Constants.PAGE_PREFETCH
            ListItem.VIEW_TYPE_LOADING -> true
            else -> false
        }
        if (shouldLoad) launch { loadNextPage() }
    }

    fun refresh() {
        launch {
            loadFirstPage(true)
            /*; Log.i("location", "home page refresh") */
            //; setupPermissions()
        }
    }

    fun retry() {
        launch { loadNextPage() }
    }

    private suspend fun loadFirstPage(clear: Boolean): Boolean = loadPage(0,
        Constants.PAGE_SIZE_FIRST, clear)

    private suspend fun loadNextPage(): Boolean = when {
        totalCount == UNKNOWN_ITEM_COUNT -> loadFirstPage(false)
        itemCount <= totalCount -> loadPage(itemCount,
            Constants.PAGE_SIZE, false)
        else -> false
    }

    private var loadJob: Job? = null
    private suspend fun loadPage(from: Int, count: Int, clear: Boolean): Boolean = coroutineScope {
        if (clear) loadJob?.cancelAndJoin() // cancel last call, wait and always execute refresh
        if (loadMutex.tryLock()) { // one page at a time
            try {
                loadJob = coroutineContext[Job] // save the Job holding the lock
                Log.v(this@Listing.TAG, "Start loading: $from-${from + count} ${if (clear) "clear" else ""}")
                val (loaded, total) = networkLoadCall(from, count, clear, createCallState()) ?: return@coroutineScope false
                if (clear) itemCount = 0
                itemCount += loaded
                totalCount = total
                return@coroutineScope true
            } finally {
                loadMutex.unlock()
            }
        } else {
            Log.v(this@Listing.TAG, "Already loading, backing off. ${this@Listing}")
            return@coroutineScope false
        }
    }

    companion object {
        const val UNKNOWN_ITEM_COUNT = -1
    }

    //Network
    suspend fun createCallState(combinedNetworkState: MediatorLiveData<NetworkState>): MutableLiveData<NetworkState> =
        withContext(Dispatchers.Main) {
            MutableLiveData<NetworkState>().also { state ->
                state.value =
                    NetworkState.LOADING
                combinedNetworkState.addSource(state) {
                    combinedNetworkState.value = it
                    if (it.status == Status.FAILED || it.status == Status.SUCCESS)
                        combinedNetworkState.removeSource(state)
                }
            }
        }
}