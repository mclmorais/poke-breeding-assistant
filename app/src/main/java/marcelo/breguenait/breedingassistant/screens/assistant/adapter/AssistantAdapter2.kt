package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.Adapter
import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.logic.BreedingManager
import marcelo.breguenait.breedingassistant.logic.CombinationHolder
import marcelo.breguenait.breedingassistant.screens.assistant.AssistantContract
import marcelo.breguenait.breedingassistant.screens.assistant.AssistantPresenter

/**
 * Created by Marcelo on 07/02/2018.
 */
class AssistantAdapter2(val presenter: AssistantContract.Presenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var initialized = false

    private var directFlags = BreedingManager.DIRECT_NONE

    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    private val items = ArrayList<ViewType>()

    private val loadingItem = object : ViewType {
        override val viewType = AdapterConstants.LOADING
    }

    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.DIRECT, DirectDelegateAdapter(presenter))
        items.add(loadingItem)
    }

    fun updateDirectItems(newDirectList: List<CombinationHolder>, flags: Int) {
        this.directFlags = flags

        initialized = true

        items.addAll(newDirectList)
    }


    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        delegateAdapters.get(viewType).onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, items[position])
}