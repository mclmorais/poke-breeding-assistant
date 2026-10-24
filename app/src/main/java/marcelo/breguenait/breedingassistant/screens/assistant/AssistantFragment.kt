package marcelo.breguenait.breedingassistant.screens.assistant

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.databinding.AssistantMainFragmentBinding
import marcelo.breguenait.breedingassistant.logic.CombinationHolder
import marcelo.breguenait.breedingassistant.screens.assistant.adapter.AssistantAdapter
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity
import javax.inject.Inject


class AssistantFragment : Fragment(), AssistantContract.AssistantView {


    @Inject
    lateinit var presenter: AssistantContract.Presenter

    private var goalIvImageViews: Array<ImageView?> = arrayOfNulls(6)

    private lateinit var assistantAdapter: AssistantAdapter

    private val createStoredLauncher = creationLauncher(CreationActivity.REQUEST_CREATE_STORED)

    private val editGoalLauncher = creationLauncher(CreationActivity.REQUEST_EDIT_GOAL)

    private fun creationLauncher(requestCode: Int) =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            //assistantAdapter.clear() TODO: REPLACE WITH NEW ADAPTER
            binding.bestMatchesList.adapter = assistantAdapter
            presenter.result(requestCode, it.resultCode)
        }

    private var _binding: AssistantMainFragmentBinding? = null
    private val binding get() = _binding!!

    private val activityBinding get() = (activity as AssistantActivity?)?.binding


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {


        _binding = AssistantMainFragmentBinding.inflate(inflater, container, false)

        presenter = (activity as AssistantActivity).getAssistantPresenter()
        presenter.setAssistantView(this)

        assistantAdapter = AssistantAdapter(presenter)

        goalIvImageViews = arrayOfNulls(6)
        goalIvImageViews[0] = activityBinding?.pokemonIvHp
        goalIvImageViews[1] = activityBinding?.pokemonIvAtk
        goalIvImageViews[2] = activityBinding?.pokemonIvDef
        goalIvImageViews[3] = activityBinding?.pokemonIvSatk
        goalIvImageViews[4] = activityBinding?.pokemonIvSdef
        goalIvImageViews[5] = activityBinding?.pokemonIvSpd

        activityBinding?.fabAdd?.setOnClickListener { presenter.storeNewPokemon() }


        val linearLayoutManager = LinearLayoutManager(context)

        binding.bestMatchesList.layoutManager = linearLayoutManager
        binding.bestMatchesList.adapter = assistantAdapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        presenter.startAssistant()
    }

    fun requestGoalEdit() {
        presenter.editGoal()
    }

    override fun updateSelectedPokemonName(name: String) {
        activityBinding?.pokemonName?.text = name
    }

    override fun updateSelectedPokemonIcon(iconId: Int) {

        val icon = context?.let { ContextCompat.getDrawable(it, iconId) }
        activityBinding?.pokemonIcon?.setImageDrawable(icon)

    }

    override fun updateSelectedPokemonExtraInfo(nature: String, ability: String) {

        activityBinding?.pokemonExtraInfo?.text = String.format(getString(R.string.assistant_goal_extra_info), nature, ability)

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
        createStoredLauncher.launch(intent)
    }

    override fun showEditGoal() {
        val intent = Intent(context, CreationActivity::class.java)
        intent.putExtra(CreationActivity.TYPE_ID, CreationActivity.GOAL)
        intent.putExtra(CreationActivity.EXISTENT_ID, presenter.currentGoal?.internalId)
        editGoalLauncher.launch(intent)
    }

    override fun provideDirectItems(chances: List<CombinationHolder>, flags: Int) =
        assistantAdapter.updateDirectItems(chances, flags)


    override fun provideImprovementItems(improvements: List<CombinationHolder>) =
        assistantAdapter.updateImprovementItems(improvements)

    override fun showLoading() =
        assistantAdapter.clear()


    override fun runLayoutAnimation() {
        val recyclerView = binding.bestMatchesList
        val context = recyclerView.context
        val controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_animation_fall_down)

        recyclerView.layoutAnimation = controller
        recyclerView.adapter?.notifyDataSetChanged()
        recyclerView.scheduleLayoutAnimation()
    }


}
