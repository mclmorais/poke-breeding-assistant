package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.assistant_item_direct.view.*
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.logic.CombinationHolder
import marcelo.breguenait.breedingassistant.screens.assistant.AssistantContract
import marcelo.breguenait.breedingassistant.utils.Genders

/**
 * Created by Marcelo on 07/02/2018.
 */
class ImprovementDelegateAdapter(val assistantPresenter: AssistantContract.Presenter) : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = ImprovementViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as ImprovementViewHolder
        holder.bind(item as CombinationHolder)
    }

    inner class ImprovementViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(parent.inflate(R.layout.assistant_item_direct)) {
        val related: ImageView = itemView.related_icon
        val compatible: ImageView = itemView.compatible_icon

        private val relatedInfo: TextView = itemView.related_info
        private val compatibleInfo: TextView = itemView.compatible_info

        val totalChanceFraction: TextView = itemView.total_chance
        //val totalChancePercentage: TextView = itemView.total_percentage

        val relatedIVs: Array<ImageView?> = arrayOfNulls(6)
        val compatibleIVs: Array<ImageView?> = arrayOfNulls(6)

        init {
            relatedIVs[0] = itemView.related_iv_hp
            relatedIVs[1] = itemView.related_iv_atk
            relatedIVs[2] = itemView.related_iv_def
            relatedIVs[3] = itemView.related_iv_satk
            relatedIVs[4] = itemView.related_iv_sdef
            relatedIVs[5] = itemView.related_iv_spd

            compatibleIVs[0] = itemView.compatible_iv_hp
            compatibleIVs[1] = itemView.compatible_iv_atk
            compatibleIVs[2] = itemView.compatible_iv_def
            compatibleIVs[3] = itemView.compatible_iv_satk
            compatibleIVs[4] = itemView.compatible_iv_sdef
            compatibleIVs[5] = itemView.compatible_iv_spd
        }

        fun setGenderBackground(@Genders.GendersFlag gender: Int, view: ImageView) {

            when (gender) {
                Genders.MALE       -> view.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_circle_color_male)
                Genders.FEMALE     -> view.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_circle_color_female)
                Genders.GENDERLESS -> view.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_circle_color_genderless)
                Genders.DITTO      -> view.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_circle_color_ditto)
                else               -> view.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_circle_color_genderless)
            }
        }

        fun setIcon(id: Int, view: ImageView) {

            val iconId = assistantPresenter.getPokemonIconId(id)

            val icon = ContextCompat.getDrawable(itemView.context, iconId)

            view.setImageDrawable(icon)
        }

        fun bind(item: CombinationHolder) {

            val relatedPokemon = item.couple
                .related
            val compatiblePokemon = item.couple
                .compatible
            val chance = item.chance

            setIcon(relatedPokemon.externalId, related)
            setGenderBackground(relatedPokemon.gender, related)

            setIcon(compatiblePokemon!!.externalId, compatible)
            setGenderBackground(compatiblePokemon.gender, compatible)

            val relatedNature = assistantPresenter.getNatureName(relatedPokemon.natureId)
            val relatedAbility = assistantPresenter.getAbilityName(relatedPokemon.externalId, relatedPokemon.abilitySlot)

            relatedInfo.text = itemView.context.getString(R.string.assistant_goal_extra_info, relatedNature, relatedAbility)

            val compatibleNature = assistantPresenter.getNatureName(compatiblePokemon.natureId)
            val compatibleAbility = assistantPresenter.getAbilityName(compatiblePokemon.externalId, compatiblePokemon.abilitySlot)

            compatibleInfo.text = itemView.context.getString(R.string.assistant_goal_extra_info, compatibleNature, compatibleAbility)

            //Tints the IV drawable according to if the selected goal has it
            for (i in 0..5) {

                val drawable = relatedIVs[i]?.drawable ?: return
                var wrappedDrawable = DrawableCompat.wrap(drawable)
                wrappedDrawable = wrappedDrawable.mutate()

                val ivColor: Int
                if (relatedPokemon.IVs[i] > 0) {
                    ivColor = ContextCompat.getColor(itemView.context, R.color.iv_enabled_light)
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                } else {
                    ivColor = ContextCompat.getColor(itemView.context, R.color.iv_disabled_light)
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                }
            }

            //Tints the IV drawable according to if the selected goal has it
            for (i in 0..5) {

                val drawable = compatibleIVs[i]?.drawable ?: return
                var wrappedDrawable = DrawableCompat.wrap(drawable)
                wrappedDrawable = wrappedDrawable.mutate()

                val ivColor: Int
                if (compatiblePokemon.IVs[i] > 0) {
                    ivColor = ContextCompat.getColor(itemView.context, R.color.iv_enabled_light)
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                } else {
                    ivColor = ContextCompat.getColor(itemView.context, R.color.iv_disabled_light)
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                }
            }


//            if (!directList.isEmpty()) { //TODO: implement connection from improvement to direct so that it can calculate improvement
//                val baseDirectChance = directList[0].chance
//
//                val doubleDirectChance = 1.0 - (1.0 - baseDirectChance) * (1.0 - baseDirectChance)
//
//                val improvement = chance / doubleDirectChance
//
//                totalChanceFraction.text = itemView.context.getString(R.string.improvement_chance, improvement)
//            }
        }
    }


}