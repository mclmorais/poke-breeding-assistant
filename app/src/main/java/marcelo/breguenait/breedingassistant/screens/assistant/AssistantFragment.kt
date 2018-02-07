package marcelo.breguenait.breedingassistant.screens.assistant

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import com.github.ybq.android.spinkit.style.DoubleBounce
import kotlinx.android.synthetic.main.assistant_activity.*
import kotlinx.android.synthetic.main.assistant_item_direct.view.*
import kotlinx.android.synthetic.main.assistant_main_fragment.*
import kotlinx.android.synthetic.main.assistant_main_fragment.view.*
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.logic.BreedingManager
import marcelo.breguenait.breedingassistant.logic.CombinationHolder
import marcelo.breguenait.breedingassistant.screens.assistant.adapter.AssistantAdapter2
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity
import marcelo.breguenait.breedingassistant.utils.Genders
import javax.inject.Inject
import android.view.animation.AnimationUtils.loadLayoutAnimation
import android.view.animation.LayoutAnimationController




class AssistantFragment : Fragment(), AssistantContract.AssistantView {


    @Inject
    lateinit var presenter: AssistantContract.Presenter

    private var goalIvImageViews: Array<ImageView?> = arrayOfNulls(6)

    private lateinit var assistantAdapter: AssistantAdapter2


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val root = inflater.inflate(R.layout.assistant_main_fragment, container, false)

        presenter = (activity as AssistantActivity).getAssistantPresenter()
        presenter.setAssistantView(this)

        assistantAdapter = AssistantAdapter2(presenter)

        goalIvImageViews = arrayOfNulls(6)
        goalIvImageViews[0] = activity?.pokemon_iv_hp
        goalIvImageViews[1] = activity?.pokemon_iv_atk
        goalIvImageViews[2] = activity?.pokemon_iv_def
        goalIvImageViews[3] = activity?.pokemon_iv_satk
        goalIvImageViews[4] = activity?.pokemon_iv_sdef
        goalIvImageViews[5] = activity?.pokemon_iv_spd

        activity?.fab_add?.setOnClickListener { presenter.storeNewPokemon() }


        val linearLayoutManager = LinearLayoutManager(context)

        root.best_matches_list.layoutManager = linearLayoutManager
        root.best_matches_list.adapter = assistantAdapter

        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.startAssistant()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //assistantAdapter.clear() TODO: REPLACE WITH NEW ADAPTER
        best_matches_list.adapter = assistantAdapter
        presenter.result(requestCode, resultCode)
    }

    fun requestGoalEdit() {
        presenter.editGoal()
    }

    override fun updateSelectedPokemonName(name: String) {
        activity?.pokemon_name?.text = name
    }

    override fun updateSelectedPokemonIcon(iconId: Int) {

        val icon = context?.let { ContextCompat.getDrawable(it, iconId) }
        activity?.pokemon_icon?.setImageDrawable(icon)

    }

    override fun updateSelectedPokemonExtraInfo(nature: String, ability: String) {

        activity?.pokemon_extra_info?.text = String.format(getString(R.string.assistant_goal_extra_info), nature, ability)

    }

    override fun updateSelectedPokemonIVs(IVs: IntArray) {

        val enabledColor = context?.let { ContextCompat.getColor(it, R.color.iv_enabled_dark) } ?: 0
        val disabledColor = context?.let { ContextCompat.getColor(it, R.color.iv_disabled_dark) } ?: 0

        for (i in 0..5) {
            if (IVs[i] == 1) {
                val normalDrawable = goalIvImageViews[i]?.drawable
                val wrapDrawable = DrawableCompat.wrap(normalDrawable ?: return)
                DrawableCompat.setTint(wrapDrawable, enabledColor)
            } else {
                val normalDrawable = goalIvImageViews[i]?.drawable ?: return
                val wrapDrawable = DrawableCompat.wrap(normalDrawable)
                DrawableCompat.setTint(wrapDrawable, disabledColor)
            }
        }
    }

    override fun showCreatePokemon() {
        val intent = Intent(context, CreationActivity::class.java)
        intent.putExtra(CreationActivity.TYPE_ID, CreationActivity.STORED)
        intent.putExtra(CreationActivity.FILTER_ID, presenter.currentGoal?.externalId)
        startActivityForResult(intent, CreationActivity.REQUEST_CREATE_STORED)
    }

    override fun showEditGoal() {
        val intent = Intent(context, CreationActivity::class.java)
        intent.putExtra(CreationActivity.TYPE_ID, CreationActivity.GOAL)
        intent.putExtra(CreationActivity.EXISTENT_ID, presenter.currentGoal?.internalId)
        startActivityForResult(intent, CreationActivity.REQUEST_EDIT_GOAL)
    }

    override fun provideDirectItems(chances: List<CombinationHolder>, flags: Int) {
//        progress_bar.visibility = View.GONE
//        best_matches_list.visibility = View.VISIBLE

        assistantAdapter.updateDirectItems(chances, flags)// TODO: REPLACE WITH NEW ADAPTER

    }

    override fun provideImprovementItems(improvements: List<CombinationHolder>) {
        assistantAdapter.updateImprovementItems(improvements) //TODO: REPLACE WITH NEW ADAPTER

        best_matches_list.adapter = assistantAdapter
    }

    override fun showLoading() {
        assistantAdapter.clear()
        assistantAdapter.notifyDataSetChanged()
//        best_matches_list.visibility = View.GONE
//        progress_bar.visibility = View.VISIBLE
    }

    override fun runLayoutAnimation() {
        val recyclerView = best_matches_list
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        recyclerView.layoutAnimation = controller
        recyclerView.adapter.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }

    inner class AssistantAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        private var initialized = false

        private var directList: ArrayList<CombinationHolder> = ArrayList()

        private var improvementsList: ArrayList<CombinationHolder> = ArrayList()

        private var directFlags = BreedingManager.DIRECT_NONE

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val context = parent.context
            val inflater = LayoutInflater.from(context)


            if (viewType == 0) {
                return when (directFlags) {
                    BreedingManager.DIRECT_OK                    -> HeaderViewHolder(inflater.inflate(R.layout.assistant_item_direct_header_dok, parent, false))
                    BreedingManager.DIRECT_NO_PARENTS_GENDERED   -> HeaderViewHolder(inflater.inflate(R.layout.assistant_item_direct_header_npc, parent, false))
                    BreedingManager.DIRECT_NO_PARENTS_GENDERLESS -> HeaderViewHolder(inflater.inflate(R.layout.assistant_item_direct_header_npg, parent, false))
                    BreedingManager.DIRECT_NO_HIDDEN_ABILITY     -> HeaderViewHolder(inflater.inflate(R.layout.assistant_item_direct_header_nha, parent, false))
                    BreedingManager.DIRECT_IVS_TOO_LOW           -> HeaderViewHolder(inflater.inflate(R.layout.assistant_item_direct_header_nei, parent, false))
                    else                                         -> HeaderViewHolder(inflater.inflate(R.layout.assistant_item_direct_header_dok, parent, false))
                }
            } else {
                return when (viewType) {
                    1    -> DirectItemViewHolder(inflater.inflate(R.layout.assistant_item_direct,
                        parent,
                        false))
                    2    -> HeaderViewHolder(inflater.inflate(R.layout.assistant_item_improvement_header, parent,
                        false))
                    3    -> ImprovementItemViewHolder(inflater.inflate(R.layout.assistant_item_direct,
                        parent,
                        false))
                    else -> HeaderViewHolder(inflater.inflate(R.layout.assistant_item_direct_header_dok,
                        parent,
                        false))
                }
            }
        }

        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

            if (holder is DirectItemViewHolder) {
                bindDirectItem(holder, directList[position - 1])

            } else if (holder is ImprovementItemViewHolder) {
                bindImprovementItem(holder, improvementsList[position - 2 - directList.size])
            }

        }

        private fun bindDirectItem(holder: DirectItemViewHolder, item: CombinationHolder) {

            val related = item.couple.related
            val compatible = item.couple.compatible
            val chance = item.chance

            holder.setIcon(related.externalId, holder.related)
            holder.setGenderBackground(related.gender, holder.related)

            holder.setIcon(compatible!!.externalId, holder.compatible)
            holder.setGenderBackground(compatible.gender, holder.compatible)

            val relatedNature = presenter.getNatureName(related.natureId)
            val relatedAbility = presenter.getAbilityName(related.externalId, related.abilitySlot)

            holder.relatedInfo.text = getString(R.string.assistant_goal_extra_info, relatedNature, relatedAbility)

            val compatibleNature = presenter.getNatureName(compatible.natureId)
            val compatibleAbility = presenter.getAbilityName(
                compatible.externalId, compatible.abilitySlot)

            holder.compatibleInfo.text = getString(R.string.assistant_goal_extra_info, compatibleNature, compatibleAbility)

            val missingIVs = item.combinationProblems!!.missingIVs

            for (i in 0..5) {
                val drawable = holder.relatedIVs[i].drawable
                var wrappedDrawable = DrawableCompat.wrap(drawable)
                wrappedDrawable = wrappedDrawable.mutate()

                val ivColor: Int
                if (related.IVs[i] > 0) {
                    ivColor = context?.let { ContextCompat.getColor(it, R.color.iv_enabled_light) } ?: 0
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                } else {
                    ivColor =
                            if (missingIVs[i] == 1)
                                context?.let { ContextCompat.getColor(it, R.color.colorAccentLight) } ?: 0
                            else
                                context?.let { ContextCompat.getColor(it, R.color.iv_disabled_light) } ?: 0
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                }
            }

            for (i in 0..5) {

                val drawable = holder.compatibleIVs[i].drawable
                var wrappedDrawable = DrawableCompat.wrap(drawable)
                wrappedDrawable = wrappedDrawable.mutate()

                val ivColor: Int
                if (compatible.IVs[i] > 0) {
                    ivColor = context?.let { ContextCompat.getColor(it, R.color.iv_enabled_light) } ?: 0
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                } else {
                    ivColor =
                            if (missingIVs[i] == 1)
                                context?.let { ContextCompat.getColor(it, R.color.colorAccentLight) } ?: 0
                            else
                                context?.let { ContextCompat.getColor(it, R.color.iv_disabled_light) } ?: 0
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                }
            }

            if (chance > 0.0001)
                holder.totalChancePercentage.text = getString(R.string.percent_chance, chance * 100)
            else
                holder.totalChancePercentage.text = getString(R.string.low_percent_chance, 0.01)

            var fractionChance = (1 / chance).toInt()

            if (fractionChance == 1) fractionChance = 2

            holder.totalChanceFraction.text = getString(R.string.fraction_chance, fractionChance)

            holder.problemNature.visibility = if (item.combinationProblems!!.hasNoMatchingNature()) View.VISIBLE else View.GONE

            holder.problemAbility.visibility = if (item.combinationProblems!!.hasNoMatchingAbility()) View.VISIBLE else View.GONE

            holder.problemIVs.visibility = View.GONE
            for (iv in item.combinationProblems!!.missingIVs) {

                if (iv == 1) {
                    holder.problemIVs.visibility = View.VISIBLE
                    break
                }
            }

            if (holder.problemNature.visibility != View.GONE || holder.problemAbility.visibility != View.GONE || holder.problemIVs.visibility != View.GONE)
                holder.problemsLayout.visibility = View.VISIBLE
            else holder.problemsLayout.visibility = View.GONE

            holder.problemNature.visibility = if (item.combinationProblems!!.hasNoMatchingNature()) View.VISIBLE else View.GONE
        }

        private fun bindImprovementItem(holder: ImprovementItemViewHolder, item: CombinationHolder) {

            val related = item.couple
                .related
            val compatible = item.couple
                .compatible
            val chance = item.chance

            holder.setIcon(related.externalId, holder.related)
            holder.setGenderBackground(related.gender, holder.related)

            holder.setIcon(compatible!!.externalId, holder.compatible)
            holder.setGenderBackground(compatible.gender, holder.compatible)

            val relatedNature = presenter.getNatureName(related.natureId)
            val relatedAbility = presenter.getAbilityName(related.externalId, related.abilitySlot)

            holder.relatedInfo.text = getString(R.string.assistant_goal_extra_info, relatedNature,
                relatedAbility)

            val compatibleNature = presenter.getNatureName(compatible.natureId)
            val compatibleAbility = presenter.getAbilityName(compatible.externalId, compatible
                .abilitySlot)

            holder.compatibleInfo.text = getString(R.string.assistant_goal_extra_info, compatibleNature,
                compatibleAbility)

            //Tints the IV drawable according to if the selected goal has it
            for (i in 0..5) {

                val drawable = holder.relatedIVs[i]?.drawable ?: return
                var wrappedDrawable = DrawableCompat.wrap(drawable)
                wrappedDrawable = wrappedDrawable.mutate()

                val ivColor: Int
                if (related.IVs[i] > 0) {
                    ivColor = context?.let { ContextCompat.getColor(it, R.color.iv_enabled_light) } ?: 0
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                } else {
                    ivColor = context?.let { ContextCompat.getColor(it, R.color.iv_disabled_light) } ?: 0
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                }
            }

            //Tints the IV drawable according to if the selected goal has it
            for (i in 0..5) {

                val drawable = holder.compatibleIVs[i]?.drawable ?: return
                var wrappedDrawable = DrawableCompat.wrap(drawable)
                wrappedDrawable = wrappedDrawable.mutate()

                val ivColor: Int
                if (compatible.IVs[i] > 0) {
                    ivColor = context?.let { ContextCompat.getColor(it, R.color.iv_enabled_light) } ?: 0
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                } else {
                    ivColor = context?.let { ContextCompat.getColor(it, R.color.iv_disabled_light) } ?: 0
                    DrawableCompat.setTint(wrappedDrawable, ivColor)
                }
            }


            if (!directList.isEmpty()) {
                val baseDirectChance = directList[0].chance

                val doubleDirectChance = 1.0 - (1.0 - baseDirectChance) * (1.0 - baseDirectChance)

                val improvement = chance / doubleDirectChance

                holder.totalChanceFraction.text = getString(R.string.improvement_chance, improvement)
            }
        }

        override fun getItemCount(): Int {
            return if (!initialized)
                0
            else {
                if (improvementsList.isEmpty())
                    1 + directList.size
                else
                    1 + directList.size + 1 + improvementsList.size
            }
        }

        fun updateDirectItems(newDirectList: List<CombinationHolder>, flags: Int) {
            this.directFlags = flags

            initialized = true

            directList = ArrayList(newDirectList)
        }

        internal fun updateImprovementItems(newImprovementsList: List<CombinationHolder>) {
            improvementsList = newImprovementsList as ArrayList<CombinationHolder>
        }

        internal fun clear() {
            initialized = false
            directList.clear()
            improvementsList.clear()
        }

        override fun getItemViewType(position: Int): Int {
            return when {
                position == 0                               -> 0 //Direct Header
                position > 0 && position <= directList.size -> 1 //Direct Items
                position == directList.size + 1             -> 2 //Improvement Header
                else                                        -> 3 //Improvement Items
            }
        }

        internal inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        internal inner class ImprovementItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val related: ImageView = itemView.related_icon
            val compatible: ImageView = itemView.compatible_icon

            val relatedInfo: TextView = itemView.related_info
            val compatibleInfo: TextView = itemView.compatible_info

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
                    Genders.MALE       -> view.background = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_circle_color_male) }
                    Genders.FEMALE     -> view.background = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_circle_color_female) }
                    Genders.GENDERLESS -> view.background = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_circle_color_genderless) }
                    Genders.DITTO      -> view.background = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_circle_color_ditto) }
                    else               -> view.background = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_circle_color_genderless) }
                }
            }

            fun setIcon(id: Int, view: ImageView) {

                val iconId = presenter.getPokemonIconId(id)

                val icon = context?.let { ContextCompat.getDrawable(it, iconId) }

                view.setImageDrawable(icon)
            }

        }

        internal inner class DirectItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            val related: ImageView = itemView.related_icon
            val compatible: ImageView = itemView.compatible_icon

            val relatedInfo: TextView = itemView.related_info
            val compatibleInfo: TextView = itemView.compatible_info

            val totalChanceFraction: TextView = itemView.total_chance
            val totalChancePercentage: TextView = itemView.total_percentage

            val relatedIVs = arrayOf(itemView.related_iv_hp,
                itemView.related_iv_atk,
                itemView.related_iv_def,
                itemView.related_iv_satk,
                itemView.related_iv_sdef,
                itemView.related_iv_spd)

            val compatibleIVs = arrayOf(itemView.compatible_iv_hp,
                itemView.compatible_iv_atk,
                itemView.compatible_iv_def,
                itemView.compatible_iv_satk,
                itemView.compatible_iv_sdef,
                itemView.compatible_iv_spd)

            val problemNature: TextView = itemView.problem_nature
            val problemAbility: TextView = itemView.problem_ability
            val problemIVs: TextView = itemView.problem_ivs

            val problemsLayout: View = itemView.problems_layout

            fun setGenderBackground(@Genders.GendersFlag gender: Int, view: ImageView) {

                when (gender) {
                    Genders.MALE       -> view.background = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_circle_color_male) }
                    Genders.FEMALE     -> view.background = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_circle_color_female) }
                    Genders.GENDERLESS -> view.background = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_circle_color_genderless) }
                    Genders.DITTO      -> view.background = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_circle_color_ditto) }
                    else               -> view.background = context?.let { ContextCompat.getDrawable(it, R.drawable.shape_circle_color_genderless) }
                }
            }

            fun setIcon(id: Int, view: ImageView) {

                val iconId = presenter.getPokemonIconId(id)

                val icon = context?.let { ContextCompat.getDrawable(it, iconId) }

                view.setImageDrawable(icon)
            }

        }
    }

}
