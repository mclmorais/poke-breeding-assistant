package marcelo.breguenait.breedingassistant.screens.assistant.adapter

/**
 * Created by Marcelo on 07/02/2018.
 */
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup

interface ViewTypeDelegateAdapter {

    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder

    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType)
}