package marcelo.breguenait.breedingassistant.screens.assistant

import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import kotlinx.android.synthetic.main.assistant_activity.*
import kotlinx.android.synthetic.main.assistant_main_fragment.*
import kotlinx.android.synthetic.main.assistant_main_fragment.view.*
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.logic.CombinationHolder
import marcelo.breguenait.breedingassistant.screens.assistant.adapter.AssistantAdapter
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity
import javax.inject.Inject


class AssistantFragment : Fragment(), AssistantContract.AssistantView {


    @Inject
    lateinit var presenter: AssistantContract.Presenter

    private var goalIvImageViews: Array<ImageView?> = arrayOfNulls(6)

    private lateinit var assistantAdapter: AssistantAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        val root = inflater.inflate(R.layout.assistant_main_fragment, container, false)

        presenter = (activity as AssistantActivity).getAssistantPresenter()
        presenter.setAssistantView(this)

        assistantAdapter = AssistantAdapter(presenter)

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
                goalIvImageViews[i]?.drawable?.setTint(enabledColor)
            } else {
                goalIvImageViews[i]?.drawable?.setTint(disabledColor)
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

    override fun provideDirectItems(chances: List<CombinationHolder>, flags: Int) =
        assistantAdapter.updateDirectItems(chances, flags)


    override fun provideImprovementItems(improvements: List<CombinationHolder>) =
        assistantAdapter.updateImprovementItems(improvements)

    override fun showLoading() =
        assistantAdapter.clear()


    override fun runLayoutAnimation() {
        val recyclerView = best_matches_list
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        recyclerView.layoutAnimation = controller
        recyclerView.adapter.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }


}
