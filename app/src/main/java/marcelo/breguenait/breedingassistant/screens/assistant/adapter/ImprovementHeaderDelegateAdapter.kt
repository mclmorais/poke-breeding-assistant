package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import marcelo.breguenait.breedingassistant.R

/**
 * Created by Marcelo on 08/02/2018.
 */
class ImprovementHeaderDelegateAdapter : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ImprovementHeaderViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {}

    inner class ImprovementHeaderViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.assistant_item_improvement_header))
}