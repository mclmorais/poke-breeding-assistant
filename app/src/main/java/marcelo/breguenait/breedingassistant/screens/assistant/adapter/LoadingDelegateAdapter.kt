package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import marcelo.breguenait.breedingassistant.R

/**
 * Created by Marcelo on 07/02/2018.
 */
class LoadingDelegateAdapter : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = LoadingViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) = Unit

    class LoadingViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.assistant_item_loading))
}
