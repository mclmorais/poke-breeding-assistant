package marcelo.breguenait.breedingassistant.screens.creation

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.*
import androidx.core.view.doOnLayout
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.databinding.CreationFragmentBinding
import marcelo.breguenait.breedingassistant.databinding.CreationItemNatureSelectionBinding
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
import marcelo.breguenait.breedingassistant.screens.selection.SelectionDialogFragment
import marcelo.breguenait.breedingassistant.utils.Genders
import marcelo.breguenait.breedingassistant.utils.Stats
import java.util.*

class CreationFragment : Fragment(), CreationContract.View,
    SelectionDialogFragment.PokemonSelectionListener {

    private lateinit var slideAnimator: ValueAnimator

    internal var lastIncreasedStat = -1

    internal var lastDecreasedStat = -1

    private var initializedViews = 0

    private var lastStatValues = intArrayOf(0, 0, 0, 0, 0, 0)

    private lateinit var presenter: CreationContract.Presenter

    private var _binding: CreationFragmentBinding? = null
    private val binding get() = _binding!!

    private val activityBinding get() = (activity as CreationActivity?)?.binding

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
        get() = (binding.natureModule.spinnerNature.selectedItem as ExternalNature?)?.id ?: -1


    private fun initIVs() {
        IVs[0] = binding.ivsModule.pokemonIvHp
        IVs[1] = binding.ivsModule.pokemonIvAtk
        IVs[2] = binding.ivsModule.pokemonIvDef
        IVs[3] = binding.ivsModule.pokemonIvSatk
        IVs[4] = binding.ivsModule.pokemonIvSdef
        IVs[5] = binding.ivsModule.pokemonIvSpd


        val ivListener = View.OnClickListener {
            presenter.notifyStatChanged((0..5).find { i -> IVs[i] === it } ?: -1)
        }

        for (i in 0..5) {
            IVs[i]?.setOnClickListener(ivListener)
        }

    }

    private fun initStats() {
        statBars[0] = binding.infoInclude.statBarsModule.barHp
        statBars[1] = binding.infoInclude.statBarsModule.barAtk
        statBars[2] = binding.infoInclude.statBarsModule.barDef
        statBars[3] = binding.infoInclude.statBarsModule.barSatk
        statBars[4] = binding.infoInclude.statBarsModule.barSdef
        statBars[5] = binding.infoInclude.statBarsModule.barSpd

        statTexts[0] = binding.infoInclude.statBarsModule.valueHp
        statTexts[1] = binding.infoInclude.statBarsModule.valueAtk
        statTexts[2] = binding.infoInclude.statBarsModule.valueDef
        statTexts[3] = binding.infoInclude.statBarsModule.valueSatk
        statTexts[4] = binding.infoInclude.statBarsModule.valueSdef
        statTexts[5] = binding.infoInclude.statBarsModule.valueSpd

        statLabels[0] = binding.infoInclude.statBarsModule.statBarLabelHp
        statLabels[1] = binding.infoInclude.statBarsModule.statBarLabelAtk
        statLabels[2] = binding.infoInclude.statBarsModule.statBarLabelDef
        statLabels[3] = binding.infoInclude.statBarsModule.statBarLabelSatk
        statLabels[4] = binding.infoInclude.statBarsModule.statBarLabelSdef
        statLabels[5] = binding.infoInclude.statBarsModule.statBarLabelSpd
    }

    private fun initNatures() {
        binding.natureModule.spinnerNature.adapter = NatureSpinnerAdapter(presenter.natures)
        binding.natureModule.spinnerNature.setSelection(0, false)

        binding.natureModule.spinnerNature.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {

                val nature = binding.natureModule.spinnerNature.adapter.getItem(position) as ExternalNature
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

    private fun initAbilities() {
        val radioClickListener = View.OnClickListener { v ->
            when {
                v === binding.abilityModule.radio1stAbility    -> {
                    binding.abilityModule.radio2ndAbility.isChecked = false
                    binding.abilityModule.radioHiddenAbility.isChecked = false
                }
                v === binding.abilityModule.radio2ndAbility    -> {
                    binding.abilityModule.radio1stAbility.isChecked = false
                    binding.abilityModule.radioHiddenAbility.isChecked = false
                }
                v === binding.abilityModule.radioHiddenAbility -> {
                    binding.abilityModule.radio1stAbility.isChecked = false
                    binding.abilityModule.radio2ndAbility.isChecked = false
                }
            }
        }

        binding.abilityModule.radio1stAbility.setOnClickListener(radioClickListener)
        binding.abilityModule.radio2ndAbility.setOnClickListener(radioClickListener)
        binding.abilityModule.radioHiddenAbility.setOnClickListener(radioClickListener)
    }

    private fun initGenderFab(floatingActionButton: FloatingActionButton?) {
        val params = floatingActionButton?.layoutParams as CoordinatorLayout.LayoutParams
        params.anchorId = R.id.main_card
        val maleColor = ContextCompat.getColor(requireContext(), R.color.male)
        val femaleColor = ContextCompat.getColor(requireContext(), R.color.female)

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
        _binding = CreationFragmentBinding.inflate(inflater, container, false)

        presenter = (activity as CreationActivity).creationPresenter
        presenter.setView(this)

        binding.infoInclude.selectedPokemonEdit.setOnClickListener { presenter.selectPokemon() }

        initIVs()
        initStats()
        initNatures()
        initAbilities()
        initGenderFab(activityBinding?.genderFab)
        initFinishFab(activityBinding?.finishFab)

        val actionBar = (activity as AppCompatActivity).supportActionBar
        if (actionBar != null) {
            try {
                when ((presenter as CreationPresenter).transactionType) {
                    CreationActivity.REQUEST_CREATE_GOAL ->
                        actionBar.setTitle(R.string.title_create_a_goal)

                    CreationActivity.REQUEST_CREATE_STORED ->
                        actionBar.setTitle(R.string.title_store_a_pokemon)
                    CreationActivity.REQUEST_EDIT_GOAL ->
                        actionBar.setTitle(R.string.title_edit_goal)
                    CreationActivity.REQUEST_EDIT_STORED ->
                        actionBar.setTitle(R.string.title_edit_stored)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        presenter.start()
    }

    override fun updateSelectedPokemonName(name: String) {
        binding.selectedPokemonName.text = name
    }

    override fun updateSelectedPokemonIcon(iconId: Int) {
        val icon = context?.getDrawable(iconId)
        binding.infoInclude.selectedPokemonIcon?.setImageDrawable(icon)
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
            binding.abilityModule.radio1stAbility.text = abilities[0]
        }

        if (abilities[1] != null) {
            binding.abilityModule.radio2ndAbility.visibility = View.VISIBLE
            binding.abilityModule.radio2ndAbility.text = abilities[1]
        } else
            binding.abilityModule.radio2ndAbility.visibility = View.GONE

        if (abilities[2] != null) {
            binding.abilityModule.radioHiddenAbility.visibility = View.VISIBLE
            binding.abilityModule.radioHiddenAbility.text = String.format(getString(R.string.label_hidden), abilities[2])
        } else {
            binding.abilityModule.radioHiddenAbility.visibility = View.GONE
        }


    }

    override fun updateGenderRestrictions(restriction: Int) {

        val genderFab = activityBinding?.genderFab

        when (restriction) {
            0    -> {
                val maleColor = ContextCompat.getColor(requireContext(), R.color.male_strong)
                genderFab?.backgroundTintList = ColorStateList.valueOf(maleColor)
                genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_male,
                    activity?.theme))
                genderFab?.isClickable = false
                isFemale = false
            }
            1000 -> {
                val femaleColor = ContextCompat.getColor(requireContext(), R.color.female_strong)
                genderFab?.backgroundTintList = ColorStateList.valueOf(femaleColor)
                genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_female,
                    activity?.theme))
                genderFab?.isClickable = false
                isFemale = true
            }
            2000 -> {
                val genderlessColor = ContextCompat.getColor(requireContext(), R.color.genderless)
                genderFab?.backgroundTintList = ColorStateList.valueOf(genderlessColor)
                genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_genderless,
                    activity?.theme))
                genderFab?.isClickable = false
            }
            3000 -> {
                val dittoColor = ContextCompat.getColor(requireContext(), R.color.ditto)
                genderFab?.backgroundTintList = ColorStateList.valueOf(dittoColor)
                genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_genderless,
                    activity?.theme))
                genderFab?.isClickable = false
            }
            else -> {
                if (isFemale) {
                    val maleColor = ContextCompat.getColor(requireContext(), R.color.female)
                    genderFab?.backgroundTintList = ColorStateList.valueOf(maleColor)
                    genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_female,
                        activity?.theme))
                } else {
                    val maleColor = ContextCompat.getColor(requireContext(), R.color.male)
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
            val maleColor = ContextCompat.getColor(requireContext(), R.color.male)
            activityBinding?.genderFab?.backgroundTintList = ColorStateList.valueOf(maleColor)
            activityBinding?.genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_male,
                activity?.theme))
        }
    }

    override fun updateSelectedPokemonChosenAbilitySlot(slot: Int) {
        //TODO: check if not invalid
        when (slot) {
            0 -> {
                binding.abilityModule.radio1stAbility.isChecked = true
                binding.abilityModule.radio2ndAbility.isChecked = false
                binding.abilityModule.radioHiddenAbility.isChecked = false
            }
            1 -> {
                binding.abilityModule.radio1stAbility.isChecked = false
                binding.abilityModule.radio2ndAbility.isChecked = true
                binding.abilityModule.radioHiddenAbility.isChecked = false
            }
            2 -> {
                binding.abilityModule.radio1stAbility.isChecked = false
                binding.abilityModule.radio2ndAbility.isChecked = false
                binding.abilityModule.radioHiddenAbility.isChecked = true
            }
        }
    }

    override fun updateSelectedPokemonChosenNature(natureId: Int) {

        var index = 0

        for (i in 0 until binding.natureModule.spinnerNature.count) {
            val nature = binding.natureModule.spinnerNature.getItemAtPosition(i) as ExternalNature
            if (nature.id == natureId) {
                index = i
                break
            }
        }

        binding.natureModule.spinnerNature.setSelection(index, false)
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
        selectionFragment.show(childFragmentManager, "fragment_edit_name")

    }

    private fun animateBars() {

        val nextStats = IntArray(6)

        for (i in 0..5) {
            nextStats[i] = presenter.getNextStat(i)
        }


        val steps = 200


        slideAnimator = ValueAnimator
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

        if(activity == null) return

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
            statLabels[statId]?.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorPrimaryDark))
        else
            statLabels[statId]?.setTextColor(ContextCompat.getColor(requireContext(), R.color.tertiary_text))
    }

    private fun colorNatureBars() {
        val nature = binding.natureModule.spinnerNature?.selectedItem as ExternalNature?
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
                binding.abilityModule.radio1stAbility.isChecked    -> 0
                binding.abilityModule.radio2ndAbility.isChecked    -> 1
                binding.abilityModule.radioHiddenAbility.isChecked -> 2
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
                val natureBinding = CreationItemNatureSelectionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                val safeNature = natureBinding.root
                holder = ViewHolder()

                holder.viewNatureName = natureBinding.natureName
                holder.viewIncreasedStatName = natureBinding.natureIncStat
                holder.viewDecreasedStatName = natureBinding.natureDecStat


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
