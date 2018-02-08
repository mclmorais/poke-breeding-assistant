package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.RecyclerView
import android.view.View
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
class DirectDelegateAdapter(val assistantPresenter: AssistantContract.Presenter) : ViewTypeDelegateAdapter {


    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder = DirectViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType) {
        holder as DirectViewHolder
        holder.bind(item as CombinationHolder)
    }

    inner class DirectViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(parent.inflate(R.layout.assistant_item_direct)) {

        private val related: ImageView = itemView.related_icon
        private val compatible: ImageView = itemView.compatible_icon

        private val relatedInfo: TextView = itemView.related_info
        private val compatibleInfo: TextView = itemView.compatible_info

        private val totalChanceFraction: TextView = itemView.total_chance
        private val totalChancePercentage: TextView = itemView.total_percentage

        private val relatedIVs = arrayOf(
            itemView.related_iv_hp,
            itemView.related_iv_atk,
            itemView.related_iv_def,
            itemView.related_iv_satk,
            itemView.related_iv_sdef,
            itemView.related_iv_spd)

        private val compatibleIVs = arrayOf(
            itemView.compatible_iv_hp,
            itemView.compatible_iv_atk,
            itemView.compatible_iv_def,
            itemView.compatible_iv_satk,
            itemView.compatible_iv_sdef,
            itemView.compatible_iv_spd)

        private val problemNature: TextView = itemView.problem_nature
        private val problemAbility: TextView = itemView.problem_ability
        private val problemIVs: TextView = itemView.problem_ivs

        private val problemsLayout: View = itemView.problems_layout

        private fun setGenderBackground(@Genders.GendersFlag gender: Int, view: ImageView) {

            when (gender) {
                Genders.MALE       -> view.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_circle_color_male)
                Genders.FEMALE     -> view.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_circle_color_female)
                Genders.GENDERLESS -> view.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_circle_color_genderless)
                Genders.DITTO      -> view.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_circle_color_ditto)
                else               -> view.background = ContextCompat.getDrawable(itemView.context, R.drawable.shape_circle_color_genderless)
            }
        }

        private fun setIcon(id: Int, view: ImageView) {

            val iconId = assistantPresenter.getPokemonIconId(id)

            val icon = ContextCompat.getDrawable(itemView.context, iconId)

            view.setImageDrawable(icon)
        }

        fun bind(item: CombinationHolder) {

            val relatedPokemon = item.couple.related
            val compatiblePokemon = item.couple.compatible
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

            val missingIVs = item.combinationProblems?.missingIVs

            for (i in 0..5) {
                val drawable = relatedIVs[i].drawable
                var wrappedDrawable = DrawableCompat.wrap(drawable)
                wrappedDrawable = wrappedDrawable.mutate()

                val ivColor: Int
                if (relatedPokemon.IVs[i] > 0) {
                    ivColor = ContextCompat.getColor(itemView.context, R.color.iv_enabled_light)
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                } else {
                    ivColor = if (missingIVs!![i] == 1)
                        ContextCompat.getColor(itemView.context, R.color.colorAccentLight)
                    else
                        ContextCompat.getColor(itemView.context, R.color.iv_disabled_light)
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                }
            }

            for (i in 0..5) {

                val drawable = compatibleIVs[i].drawable
                var wrappedDrawable = DrawableCompat.wrap(drawable)
                wrappedDrawable = wrappedDrawable.mutate()

                val ivColor: Int
                if (compatiblePokemon.IVs[i] > 0) {
                    ivColor = ContextCompat.getColor(itemView.context, R.color.iv_enabled_light)
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                } else {
                    ivColor =
                            if (missingIVs!![i] == 1)
                                ContextCompat.getColor(itemView.context, R.color.colorAccentLight)
                            else
                                ContextCompat.getColor(itemView.context, R.color.iv_disabled_light)
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                }
            }

            if (chance > 0.0001)
                totalChancePercentage.text = itemView.context.getString(R.string.percent_chance, chance * 100)
            else
                totalChancePercentage.text = itemView.context.getString(R.string.low_percent_chance, 0.01)

            var fractionChance = (1 / chance).toInt()

            if (fractionChance == 1) fractionChance = 2

            totalChanceFraction.text = itemView.context.getString(R.string.fraction_chance, fractionChance)

            problemNature.visibility = if (item.combinationProblems!!.hasNoMatchingNature()) View.VISIBLE else View.GONE

            problemAbility.visibility = if (item.combinationProblems!!.hasNoMatchingAbility()) View.VISIBLE else View.GONE

            problemIVs.visibility = View.GONE
            for (iv in item.combinationProblems!!.missingIVs) {

                if (iv == 1) {
                    problemIVs.visibility = View.VISIBLE
                    break
                }
            }

            if (problemNature.visibility != View.GONE || problemAbility.visibility != View.GONE || problemIVs.visibility != View.GONE)
                problemsLayout.visibility = View.VISIBLE
            else problemsLayout.visibility = View.GONE

            problemNature.visibility = if (item.combinationProblems!!.hasNoMatchingNature()) View.VISIBLE else View.GONE
        }

    }
}