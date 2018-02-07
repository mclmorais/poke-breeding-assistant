package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import marcelo.breguenait.breedingassistant.logic.CombinationHolder
import marcelo.breguenait.breedingassistant.screens.assistant.AssistantContract

/**
 * Created by Marcelo on 07/02/2018.
 */
class AssistantAdapter2(val presenter: AssistantContract.Presenter) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    private val items = ArrayList<ViewType>()

    private val loadingItem = object : ViewType {
        override val viewType = AdapterConstants.LOADING
    }

    private val headerItem = object : ViewType {
        override val viewType = AdapterConstants.HEADER
    }


    init {
        delegateAdapters.put(AdapterConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(AdapterConstants.DIRECT, DirectDelegateAdapter(presenter))
        delegateAdapters.put(AdapterConstants.HEADER, HeaderDelegateAdapter())
        items.add(headerItem)
        items.add(loadingItem)
    }

    fun updateDirectItems(newDirectList: List<CombinationHolder>, flags: Int) {

        //TODO: Animate everything!

        items.remove(loadingItem)

        (delegateAdapters[AdapterConstants.HEADER] as HeaderDelegateAdapter).directFlags = flags
        if(!items.contains(headerItem))
            items.add(headerItem)


        items.removeInCase { it is CombinationHolder }
        items.addAll(newDirectList)
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

fun <T> ArrayList<T>.removeInCase(condition: (T) -> Boolean) {

    this.removeAll(this.filter(condition))
}
