package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import marcelo.breguenait.breedingassistant.logic.CombinationHolder
import marcelo.breguenait.breedingassistant.screens.assistant.AssistantContract

/**
 * Adapter used to show information to the user regarding the best practices for breeding based on what the user has inputted. This adapter takes
 * multiple types of views in order to build a sequential visual structure utilizing headers followed by the content itself.
 * Created by Marcelo on 07/02/2018.
 */
class AssistantAdapter(val presenter: AssistantContract.Presenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    private val items = ArrayList<ViewType>()

    private val loadingItem = object : ViewType {
        override val viewType = AdapterConstants.LOADING
    }

    private val directHeaderItem = object : ViewType {
        override val viewType = AdapterConstants.DIRECT_HEADER
    }

    private val improvementHeaderItem = object : ViewType {
        override val viewType = AdapterConstants.IMPROVEMENT_HEADER
    }


    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.DIRECT_HEADER, DirectHeaderDelegateAdapter())
        delegateAdapters.put(AdapterConstants.DIRECT_SUGGESTION, DirectDelegateAdapter(presenter))
        delegateAdapters.put(AdapterConstants.IMPROVEMENT_HEADER, ImprovementHeaderDelegateAdapter())
        delegateAdapters.put(AdapterConstants.IMPROVEMENT_SUGGESTION, ImprovementDelegateAdapter(presenter))
    }

    fun updateDirectItems(newDirectList: List<CombinationHolder>, flags: Int) {
        items.remove(loadingItem)

        (delegateAdapters[AdapterConstants.DIRECT_HEADER] as DirectHeaderDelegateAdapter).directFlags = flags
        if (!items.contains(directHeaderItem))
            items.add(directHeaderItem)


        items.removeInCase { it is CombinationHolder }
        items.addAll(newDirectList)

        //notifyItemRangeChanged(0, items.size - 1)
    }

    internal fun updateImprovementItems(newImprovementsList: List<CombinationHolder>) {

        if(newImprovementsList.isEmpty()) return

        if (!items.contains(improvementHeaderItem))
            items.add(improvementHeaderItem)

        items.addAll(newImprovementsList)
    }

    fun clear() {
        items.clear()
        items.add(loadingItem)
    }


    override fun getItemCount() = items.size

    override fun getItemViewType(position: Int) = items[position].viewType

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        delegateAdapters.get(viewType).onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) =
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, items[position])


}

