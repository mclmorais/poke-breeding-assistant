package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.logic.BreedingManager

/**
 * Created by Marcelo on 07/02/2018.
 */
class DirectHeaderDelegateAdapter : ViewTypeDelegateAdapter {

    var directFlags = BreedingManager.DIRECT_NONE

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return when (directFlags) {
            BreedingManager.DIRECT_OK                    -> DirectHeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_dok))
            BreedingManager.DIRECT_NO_PARENTS_GENDERED   -> DirectHeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_npc))
            BreedingManager.DIRECT_NO_PARENTS_GENDERLESS -> DirectHeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_npg))
            BreedingManager.DIRECT_NO_HIDDEN_ABILITY     -> DirectHeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_nha))
            BreedingManager.DIRECT_IVS_TOO_LOW           -> DirectHeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_nei))
            else                                         -> DirectHeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_dok))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {

    }

    inner class DirectHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}