package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import android.support.annotation.LayoutRes
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.ybq.android.spinkit.style.DoubleBounce
import kotlinx.android.synthetic.main.assistant_item_loading.view.*
import marcelo.breguenait.breedingassistant.R

/**
 * Created by Marcelo on 07/02/2018.
 */
class LoadingDelegateAdapter : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = LoadingViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as LoadingViewHolder
        holder.bind()
    }

    class LoadingViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.assistant_item_loading)) {

        private val loadingIcon = itemView.loading_icon

        fun bind() {
            val customIcon = DoubleBounce()
            customIcon.color = ContextCompat.getColor(itemView.context, R.color.colorAccent)
            loadingIcon.indeterminateDrawable = customIcon
        }

    }
}

