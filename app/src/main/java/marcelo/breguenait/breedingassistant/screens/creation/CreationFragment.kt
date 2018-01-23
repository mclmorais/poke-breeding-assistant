package marcelo.breguenait.breedingassistant.screens.creation

import android.animation.ValueAnimator
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.graphics.drawable.VectorDrawableCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.creation_activity.*
import kotlinx.android.synthetic.main.creation_fragment.*
import kotlinx.android.synthetic.main.creation_module_ability.*
import kotlinx.android.synthetic.main.creation_module_info.*
import kotlinx.android.synthetic.main.creation_module_nature.*
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
import marcelo.breguenait.breedingassistant.screens.selection.SelectionDialogFragment
import marcelo.breguenait.breedingassistant.utils.Genders
import marcelo.breguenait.breedingassistant.utils.Stats

class CreationFragment : Fragment(), CreationContract.View,
    SelectionDialogFragment.PokemonSelectionListener {


    private var initializedViews = 0

    internal var lastStatValues = intArrayOf(0, 0, 0, 0, 0, 0)

    private lateinit var presenter: CreationContract.Presenter

    internal var isFemale = false

    private var IVs = arrayOfNulls<CheckBox>(6)

    private var statBars = arrayOfNulls<ImageView>(6)

    internal var statTexts = arrayOfNulls<TextView>(6)

    internal var statLabels = arrayOfNulls<TextView>(6)

    private var globalLayoutListener = ViewTreeObserver.OnGlobalLayoutListener {
        initializedViews++
        if (initializedViews >= 6) {
            animateBars()
            initializedViews = 0
        }
    }

    override val selectedIVs: IntArray
        get() {
            val data = IntArray(6)
            for (i in IVs.indices) {
                data[i] = if (IVs[i]?.isChecked ?: false) 1 else 0 //TODO: validate better
            }
            return data
        }

    override val selectedNatureId: Int
        get() = (spinner_nature.selectedItem as ExternalNature?)?.id ?: -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.creation_fragment, container, false)

        presenter = (activity as CreationActivity).creationPresenter
        presenter.setView(this)

        //Anchors the floating action buttons to the main card on the fragment
        val fabParams =
            (activity as CreationActivity).finish_fab.layoutParams as CoordinatorLayout.LayoutParams
        fabParams.anchorId = R.id.main_card

        val params =
            (activity as CreationActivity).gender_fab.layoutParams as CoordinatorLayout.LayoutParams
        params.anchorId = R.id.main_card

        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.selectPokemon()
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
            bar?.viewTreeObserver?.addOnGlobalLayoutListener(globalLayoutListener)
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

        if (restriction == 0) {
            val maleColor = ContextCompat.getColor(context!!, R.color.male_strong)
            genderFab?.backgroundTintList = ColorStateList.valueOf(maleColor)
            genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_male,
                activity?.theme))
            genderFab?.isClickable = false
            isFemale = false
        } else if (restriction == 1000) {
            val femaleColor = ContextCompat.getColor(context!!, R.color.female_strong)
            genderFab?.backgroundTintList = ColorStateList.valueOf(femaleColor)
            genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_female,
                activity?.theme))
            genderFab?.isClickable = false
            isFemale = true
        } else if (restriction == 2000) {
            val genderlessColor = ContextCompat.getColor(context!!, R.color.genderless)
            genderFab?.backgroundTintList = ColorStateList.valueOf(genderlessColor)
            genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_genderless,
                activity?.theme))
            genderFab?.isClickable = false
        } else if (restriction == 3000) {
            val dittoColor = ContextCompat.getColor(context!!, R.color.ditto)
            genderFab?.backgroundTintList = ColorStateList.valueOf(dittoColor)
            genderFab?.setImageDrawable(VectorDrawableCompat.create(resources, R.drawable.ic_gender_genderless,
                activity?.theme))
            genderFab?.isClickable = false
        } else {
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

    override fun onPokemonSelected(id: Int) {
        presenter.updateSelection(id)
    }

    override fun onSelectorDismissed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSelectPokemonFragment() {

        val selectionFragment = SelectionDialogFragment()
        selectionFragment.setTargetFragment(this, 300)
        selectionFragment.show(fragmentManager, "fragment_edit_name")

    }

    internal fun animateBars() {

        val nextStats = IntArray(6)

        for (i in 0..5) {
            nextStats[i] = presenter.getNextStat(i)
        }


        for (i in 0..5)
            statBars[i]?.viewTreeObserver?.removeOnGlobalLayoutListener(globalLayoutListener)

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

    internal fun animateSingleBar(@Stats.StatFlag statId: Int) {
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

    internal fun barAnimation(@Stats.StatFlag statId: Int, iterator: Int, initialStatValue: Int, finalStatValue: Int, steps: Int) {


        lastStatValues[statId] = initialStatValue + (finalStatValue - initialStatValue) * iterator / steps

        statTexts[statId]?.text = context?.getString(R.string.creation_stat_value, lastStatValues[statId]) ?: ""

        val statMaxValue: Int

        if (statId == Stats.HP)
            statMaxValue = resources.getInteger(R.integer.max_hp_value)
        else
            statMaxValue = resources.getInteger(R.integer.max_stat_value)
        val barMaxWidth = resources.getDimension(R.dimen.stat_bar_max_width).toInt()
        val barMinWidth = resources.getDimension(R.dimen.stat_bar_min_width).toInt()

        val ratio = statMaxValue.toFloat() / barMaxWidth

        var barSize: Int
        if (lastStatValues[statId] < statMaxValue)
            barSize = (lastStatValues[statId] / ratio).toInt()
        else
            barSize = barMaxWidth

        if (barSize < barMinWidth) barSize = barMinWidth

        statBars[statId]?.layoutParams?.width = barSize
        statBars[statId]?.requestLayout()

    }

    internal fun colorStatLabel(statId: Int) {

        if (context == null) return

        if (IVs[statId]?.isChecked ?: return)
            statLabels[statId]?.setTextColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
        else
            statLabels[statId]?.setTextColor(ContextCompat.getColor(context!!, android.R.color.tertiary_text_light))
    }

    internal fun colorNatureBars() {
        val nature = spinner_nature.selectedItem as ExternalNature
        for (i in 0..5) {
            statLabels[i]?.typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)

            if (nature.decreasedStatId != nature.increasedStatId) {
                if (i == nature.increasedStatId)
                    statLabels[i]?.typeface = Typeface.create("sans-serif-condensed", Typeface.BOLD)
                else if (i == nature.decreasedStatId)
                    statLabels[i]?.typeface = Typeface.create("sans-serif-condensed", Typeface.ITALIC)
                else
                    statLabels[i]?.typeface = Typeface.create("sans-serif-condensed", Typeface.NORMAL)
            }

        }
    }


    companion object {
        fun newInstance(/*param1: String, param2: String*/): CreationFragment {
            return CreationFragment()
        }
    }
}
