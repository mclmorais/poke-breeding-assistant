package marcelo.breguenait.breedingassistant.screens.creation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.view.doOnLayout
import kotlinx.android.synthetic.main.creation_activity.*
import kotlinx.android.synthetic.main.creation_fragment.*
import kotlinx.android.synthetic.main.creation_fragment.view.*
import kotlinx.android.synthetic.main.creation_item_nature_selection.view.*
import kotlinx.android.synthetic.main.creation_item_stat_bars.view.*
import kotlinx.android.synthetic.main.creation_module_ability.*
import kotlinx.android.synthetic.main.creation_module_ability.view.*
import kotlinx.android.synthetic.main.creation_module_info.*
import kotlinx.android.synthetic.main.creation_module_info.view.*
import kotlinx.android.synthetic.main.creation_module_ivs.view.*
import kotlinx.android.synthetic.main.creation_module_nature.*
import kotlinx.android.synthetic.main.creation_module_nature.view.*
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
import marcelo.breguenait.breedingassistant.screens.selection.SelectionDialogFragment
import marcelo.breguenait.breedingassistant.utils.Genders
import marcelo.breguenait.breedingassistant.utils.Stats
import java.util.*

class CreationFragment : Fragment(), CreationContract.View,
    SelectionDialogFragment.PokemonSelectionListener {


    internal var lastIncreasedStat = -1

    internal var lastDecreasedStat = -1

    private var initializedViews = 0

    private var lastStatValues = intArrayOf(0, 0, 0, 0, 0, 0)

    private lateinit var presenter: CreationContract.Presenter

    private var isFemale = false

    @Suppress("PrivatePropertyName")
    private var IVs = arrayOfNulls<CheckBox>(6)

    private var statBars = arrayOfNulls<ImageView>(6)

    private var statTexts = arrayOfNulls<TextView>(6)

    private var statLabels = arrayOfNulls<TextView>(6)

    override val selectedIVs: IntArray
        get() {
            val data = IntArray(6)
            //TODO: validate better
            for (i in IVs.indices) data[i] = if (IVs[i]?.isChecked == true) 1 else 0
            return data
        }

    override val selectedNatureId: Int
        get() = (spinner_nature.selectedItem as ExternalNature?)?.id ?: -1


    private fun initIVs(root: View) {
        IVs[0] = root.ivs_module.pokemon_iv_hp
        IVs[1] = root.ivs_module.pokemon_iv_atk
        IVs[2] = root.ivs_module.pokemon_iv_def
        IVs[3] = root.ivs_module.pokemon_iv_satk
        IVs[4] = root.ivs_module.pokemon_iv_sdef
        IVs[5] = root.ivs_module.pokemon_iv_spd


        val ivListener = View.OnClickListener {
            presenter.notifyStatChanged((0..5).find { i -> IVs[i] === it } ?: -1)
        }

        for (i in 0..5) {
            IVs[i]?.setOnClickListener(ivListener)
        }

    }

    private fun initStats(root: View) {
        statBars[0] = root.bar_hp
        statBars[1] = root.bar_atk
        statBars[2] = root.bar_def
        statBars[3] = root.bar_satk
        statBars[4] = root.bar_sdef
        statBars[5] = root.bar_spd

        statTexts[0] = root.value_hp
        statTexts[1] = root.value_atk
        statTexts[2] = root.value_def
        statTexts[3] = root.value_satk
        statTexts[4] = root.value_sdef
        statTexts[5] = root.value_spd

        statLabels[0] = root.stat_bar_label_hp
        statLabels[1] = root.stat_bar_label_atk
        statLabels[2] = root.stat_bar_label_def
        statLabels[3] = root.stat_bar_label_satk
        statLabels[4] = root.stat_bar_label_sdef
        statLabels[5] = root.stat_bar_label_spd
    }

    private fun initNatures(root: View) {
        root.spinner_nature.adapter = NatureSpinnerAdapter(presenter.natures)
        root.spinner_nature.setSelection(0, false)

        root.spinner_nature.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                val nature = root.spinner_nature.adapter.getItem(position) as ExternalNature
                presenter.notifyStatChanged(nature.increasedStatId)
                presenter.notifyStatChanged(nature.decreasedStatId)
                if (lastIncreasedStat != -1 &&
                    lastIncreasedStat != nature.increasedStatId &&
                    lastIncreasedStat != nature.decreasedStatId)
                    presenter.notifyStatChanged(lastIncreasedStat)

                if (lastDecreasedStat != -1 &&
                    lastDecreasedStat != nature.increasedStatId &&
                    lastDecreasedStat != nature
                        .decreasedStatId)
                    presenter.notifyStatChanged(lastDecreasedStat)
                lastIncreasedStat = nature.increasedStatId
                lastDecreasedStat = nature.decreasedStatId

            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
    }

    private fun initAbilities(root: View) {
        val radioClickListener = View.OnClickListener { v ->
            when {
                v === radio_1st_ability    -> {
                    radio_2nd_ability.isChecked = false
                    radio_hidden_ability.isChecked = false
                }
                v === radio_2nd_ability    -> {
                    radio_1st_ability.isChecked = false
                    radio_hidden_ability.isChecked = false
                }
                v === radio_hidden_ability -> {
                    radio_1st_ability.isChecked = false
                    radio_2nd_ability.isChecked = false
                }
            }
        }

        root.radio_1st_ability.setOnClickListener(radioClickListener)
        root.radio_2nd_ability.setOnClickListener(radioClickListener)
        root.radio_hidden_ability.setOnClickListener(radioClickListener)
    }

    private fun initGenderFab(floatingActionButton: FloatingActionButton?) {
        val params = floatingActionButton?.layoutParams as CoordinatorLayout.LayoutParams
        params.anchorId = R.id.main_card
        val maleColor = ContextCompat.getColor(context!!, R.color.male)
        val femaleColor = ContextCompat.getColor(context!!, R.color.female)

        //TODO: UNGARBAGIZE
        floatingActionButton.setOnClickListener {
            if (!isFemale) {
                val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), maleColor, femaleColor)
                colorAnimator.addUpdateListener { animation -> floatingActionButton.backgroundTintList = ColorStateList.valueOf(animation.animatedValue as Int) }
                colorAnimator.duration = 250
                colorAnimator.start()
                floatingActionButton.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_female,
                    activity?.theme))
                isFemale = true
            } else {
                val colorAnimator = ValueAnimator.ofObject(ArgbEvaluator(), femaleColor, maleColor)
                colorAnimator.addUpdateListener { animation -> floatingActionButton.backgroundTintList = ColorStateList.valueOf(animation.animatedValue as Int) }
                colorAnimator.duration = 250
                colorAnimator.start()
                floatingActionButton.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_male,
                    activity?.theme))
                isFemale = false
            }
        }

        (floatingActionButton.layoutParams as CoordinatorLayout.LayoutParams).anchorId = R.id.main_card
    }

    private fun initFinishFab(floatingActionButton: FloatingActionButton?) {

        (floatingActionButton?.layoutParams as CoordinatorLayout.LayoutParams).anchorId = R.id.main_card

        floatingActionButton.setOnClickListener {
            try {
                presenter.finishCreation()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.creation_fragment, container, false)

        presenter = (activity as CreationActivity).creationPresenter
        presenter.setView(this)

        root.selected_pokemon_edit.setOnClickListener { presenter.selectPokemon() }

        initIVs(root)
        initStats(root)
        initNatures(root)
        initAbilities(root)
        initGenderFab(activity?.gender_fab)
        initFinishFab(activity?.finish_fab)

        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            try {
                when ((presenter as CreationPresenter).transactionType) {
                    CreationActivity.REQUEST_CREATE_GOAL ->
                        actionBar.setTitle(R.string.title_create_a_goal);

                    CreationActivity.REQUEST_CREATE_STORED ->
                        actionBar.setTitle(R.string.title_store_a_pokemon);
                    CreationActivity.REQUEST_EDIT_GOAL ->
                        actionBar.setTitle(R.string.title_edit_goal);
                    CreationActivity.REQUEST_EDIT_STORED ->
                        actionBar.setTitle(R.string.title_edit_stored);
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun updateSelectedPokemonName(name: String) {
        selected_pokemon_name.text = name
    }

    override fun updateSelectedPokemonIcon(iconId: Int) {
        val icon = context?.getDrawable(iconId)
        selected_pokemon_icon?.setImageDrawable(icon)
        //val icon = ContextCompat.getDrawable(context, iconId)
    }

    override fun updateSelectedPokemonStats() {

        for (bar in statBars)
            bar?.doOnLayout {
                initializedViews++
                if (initializedViews >= 6) {
                    animateBars()
                    initializedViews = 0
                }
            }
    }

    override fun updateSelectedPokemonStat(statId: Int) {
        animateSingleBar(statId)
        colorStatLabel(statId)
        colorNatureBars()
    }

    override fun updateSelectedPokemonAbilities(abilities: Array<String?>) {

        if (abilities[0] != null) {
            radio_1st_ability.text = abilities[0]
        }

        if (abilities[1] != null) {
            radio_2nd_ability.visibility = View.VISIBLE
            radio_2nd_ability.text = abilities[1]
        } else
            radio_2nd_ability.visibility = View.GONE

        if (abilities[2] != null) {
            radio_hidden_ability.visibility = View.VISIBLE
            radio_hidden_ability.text = String.format(getString(R.string.label_hidden), abilities[2])
        } else {
            radio_hidden_ability.visibility = View.GONE
        }


    }

    override fun updateGenderRestrictions(restriction: Int) {

        val genderFab = activity?.gender_fab

        when (restriction) {
            0    -> {
                val maleColor = ContextCompat.getColor(context!!, R.color.male_strong)
                genderFab?.backgroundTintList = ColorStateList.valueOf(maleColor)
                genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_male,
                    activity?.theme))
                genderFab?.isClickable = false
                isFemale = false
            }
            1000 -> {
                val femaleColor = ContextCompat.getColor(context!!, R.color.female_strong)
                genderFab?.backgroundTintList = ColorStateList.valueOf(femaleColor)
                genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_female,
                    activity?.theme))
                genderFab?.isClickable = false
                isFemale = true
            }
            2000 -> {
                val genderlessColor = ContextCompat.getColor(context!!, R.color.genderless)
                genderFab?.backgroundTintList = ColorStateList.valueOf(genderlessColor)
                genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_genderless,
                    activity?.theme))
                genderFab?.isClickable = false
            }
            3000 -> {
                val dittoColor = ContextCompat.getColor(context!!, R.color.ditto)
                genderFab?.backgroundTintList = ColorStateList.valueOf(dittoColor)
                genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_genderless,
                    activity?.theme))
                genderFab?.isClickable = false
            }
            else -> {
                if (isFemale) {
                    val maleColor = ContextCompat.getColor(context!!, R.color.female)
                    genderFab?.backgroundTintList = ColorStateList.valueOf(maleColor)
                    genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_female,
                        activity?.theme))
                } else {
                    val maleColor = ContextCompat.getColor(context!!, R.color.male)
                    genderFab?.backgroundTintList = ColorStateList.valueOf(maleColor)
                    genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_male,
                        activity?.theme))
                }
                genderFab?.isClickable = true

            }
        }
    }

    override fun updateSelectedPokemonChosenGender(@Genders.GendersFlag gender: Int) {
        if (gender == Genders.FEMALE) {
            isFemale = true
        } else {
            isFemale = false
            val maleColor = ContextCompat.getColor(context!!, R.color.male)
            activity?.gender_fab?.backgroundTintList = ColorStateList.valueOf(maleColor)
            activity?.gender_fab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_male,
                activity?.theme))
        }
    }

    override fun updateSelectedPokemonChosenAbilitySlot(slot: Int) {
        //TODO: check if not invalid
        when (slot) {
            0 -> {
                radio_1st_ability.isChecked = true
                radio_2nd_ability.isChecked = false
                radio_hidden_ability.isChecked = false
            }
            1 -> {
                radio_1st_ability.isChecked = false
                radio_2nd_ability.isChecked = true
                radio_hidden_ability.isChecked = false
            }
            2 -> {
                radio_1st_ability.isChecked = false
                radio_2nd_ability.isChecked = false
                radio_hidden_ability.isChecked = true
            }
        }
    }

    override fun updateSelectedPokemonChosenNature(natureId: Int) {

        var index = 0

        for (i in 0..spinner_nature.count - 1) {
            val nature = spinner_nature.getItemAtPosition(i) as ExternalNature
            if (nature.id == natureId) {
                index = i
                break
            }
        }

        spinner_nature.setSelection(index, false)
    }

    override fun updateSelectedPokemonChosenIVs(IVs: IntArray) {
        for (i in 0..5) {
            this.IVs[i]?.isChecked = IVs[i] == 1
        }
    }


    override fun onPokemonSelected(id: Int) {
        presenter.updateSelection(id)
    }

    override fun onSelectorDismissed() {
        if (presenter.currentSelectionId == -1) activity?.finish()
    }

    override fun showSelectPokemonFragment() {

        val selectionFragment = SelectionDialogFragment()
        selectionFragment.setTargetFragment(this, 300)
        selectionFragment.show(fragmentManager, "fragment_edit_name")

    }

    private fun animateBars() {

        val nextStats = IntArray(6)

        for (i in 0..5) {
            nextStats[i] = presenter.getNextStat(i)
        }


        val steps = 200

        val slideAnimator = ValueAnimator
            .ofInt(1, steps)
            .setDuration(1200)
        slideAnimator.interpolator = AccelerateDecelerateInterpolator()

        val initialStats = lastStatValues.clone()

        slideAnimator.addUpdateListener { animation ->
            val iterator = animation.animatedValue as Int

            for (i in 0..5) {
                barAnimation(i, iterator, initialStats[i], nextStats[i], steps)
            }
        }
        slideAnimator.start()
    }

    private fun animateSingleBar(@Stats.StatFlag statId: Int) {
        val slideAnimator = ValueAnimator
            .ofInt(1, 100)
            .setDuration(400)

        val finalStat = presenter.getNextStat(statId)

        slideAnimator.interpolator = DecelerateInterpolator()

        val initialStat = lastStatValues[statId]

        slideAnimator.addUpdateListener { animation ->
            val iterator = animation.animatedValue as Int
            val steps = 100

            barAnimation(statId, iterator, initialStat, finalStat, steps)
        }
        slideAnimator.start()

    }

    private fun barAnimation(@Stats.StatFlag statId: Int, iterator: Int, initialStatValue: Int, finalStatValue: Int, steps: Int) {


        lastStatValues[statId] = initialStatValue + (finalStatValue - initialStatValue) * iterator / steps

        statTexts[statId]?.text = context?.getString(R.string.creation_stat_value, lastStatValues[statId]) ?: ""

        val statMaxValue: Int = if (statId == Stats.HP)
            resources.getInteger(R.integer.max_hp_value)
        else
            resources.getInteger(R.integer.max_stat_value)

        val barMaxWidth = resources.getDimension(R.dimen.stat_bar_max_width).toInt()
        val barMinWidth = resources.getDimension(R.dimen.stat_bar_min_width).toInt()

        val ratio = statMaxValue.toFloat() / barMaxWidth

        var barSize =
            if (lastStatValues[statId] < statMaxValue)
                (lastStatValues[statId] / ratio).toInt()
            else
                barMaxWidth

        if (barSize < barMinWidth) barSize = barMinWidth

        statBars[statId]?.layoutParams?.width = barSize
        statBars[statId]?.requestLayout()

    }

    private fun colorStatLabel(statId: Int) {

        if (context == null) return

        if (IVs[statId]?.isChecked ?: return)
            statLabels[statId]?.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        else
            statLabels[statId]?.setTextColor(ContextCompat.getColor(context!!, android.R.color.tertiary_text_light))
    }

    private fun colorNatureBars() {
        val nature = spinner_nature?.selectedItem as ExternalNature?
        for (i in 0..5) {
            statLabels[i]?.typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)

            if (nature?.decreasedStatId != nature?.increasedStatId) {
                when (i) {
                    nature?.increasedStatId -> statLabels[i]?.typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
                    nature?.decreasedStatId -> statLabels[i]?.typeface = Typeface.create("sans-serif-condensed", Typeface.ITALIC)
                    else                    -> statLabels[i]?.typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
                }
            }

        }
    }

    override val selectedGender: Int
        @Genders.GendersFlag
        get() = if (isFemale) Genders.FEMALE else Genders.MALE

    override val selectedAbilitySlot: Int
        get() {
            return when {
                radio_1st_ability.isChecked    -> 0
                radio_2nd_ability.isChecked    -> 1
                radio_hidden_ability.isChecked -> 2
                else                           -> -1
            }
        }

    override fun exitActivity(createdId: String?) {
        if (createdId != null) {
            val intent = Intent()
            intent.putExtra("created_id", createdId)
            activity?.setResult(CreationActivity.SUCCESSFUL, intent)

        } else
            activity?.setResult(CreationActivity.UNSUCCESSFUL)
        activity?.finish()
    }

    private inner class NatureSpinnerAdapter internal constructor(internal var natures: ArrayList<ExternalNature>) : BaseAdapter() {


        init {
            Collections.sort(this.natures) { o1, o2 -> o1.increasedStatId - o2.increasedStatId }
        }


        override fun getCount(): Int {
            return natures.size
        }

        override fun getItem(position: Int): Any {
            return natures[position]
        }

        override fun getItemId(position: Int): Long {
            return 0
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
            var natureView: View? = convertView
            val holder: ViewHolder

            if (natureView == null) {
                val safeNature = LayoutInflater.from(parent.context).inflate(R.layout.creation_item_nature_selection, parent, false)
                holder = ViewHolder()

                holder.viewNatureName = safeNature.nature_name
                holder.viewIncreasedStatName = safeNature.nature_inc_stat
                holder.viewDecreasedStatName = safeNature.nature_dec_stat


                natureView = safeNature
                natureView.tag = holder
            } else {
                holder = natureView.tag as ViewHolder
            }

            holder.viewNatureName!!.text = natures[position].getName(9)

            if (natures[position].increasedStatId != natures[position].decreasedStatId) {
                holder.viewIncreasedStatName!!.visibility = View.VISIBLE
                holder.viewIncreasedStatName!!.text = getString(R.string.creation_nature_plus_stat, presenter.getStatName(natures[position].increasedStatId))
                holder.viewDecreasedStatName!!.text = getString(R.string.creation_nature_minus_stat, presenter.getStatName(natures[position].decreasedStatId))
            } else {
                holder.viewIncreasedStatName!!.visibility = View.GONE
                holder.viewDecreasedStatName!!.text = getString(R.string.creation_nature_neutral_stat)
            }

            return natureView
        }

    }

    private inner class ViewHolder {

        internal var viewNatureName: TextView? = null

        internal var viewIncreasedStatName: TextView? = null

        internal var viewDecreasedStatName: TextView? = null

    }

    companion object {
        fun newInstance(/*param1: String, param2: String*/): CreationFragment {
            return CreationFragment()
        }
    }
}
