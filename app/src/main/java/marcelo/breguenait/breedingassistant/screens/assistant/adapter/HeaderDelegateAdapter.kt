package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.logic.BreedingManager

/**
 * Created by Marcelo on 07/02/2018.
 */
class HeaderDelegateAdapter : ViewTypeDelegateAdapter {

    var directFlags = BreedingManager.DIRECT_NONE

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return when (directFlags) {
            BreedingManager.DIRECT_OK                    -> HeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_dok))
            BreedingManager.DIRECT_NO_PARENTS_GENDERED   -> HeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_npc))
            BreedingManager.DIRECT_NO_PARENTS_GENDERLESS -> HeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_npg))
            BreedingManager.DIRECT_NO_HIDDEN_ABILITY     -> HeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_nha))
            BreedingManager.DIRECT_IVS_TOO_LOW           -> HeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_nei))
            else                                         -> HeaderViewHolder(parent.inflate(R.layout.assistant_item_direct_header_dok))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {

    }

    inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}