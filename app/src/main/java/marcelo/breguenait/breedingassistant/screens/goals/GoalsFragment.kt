package marcelo.breguenait.breedingassistant.screens.goals


import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.annotation.IntDef
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.view.ActionMode
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Pair
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.goals_activity.*
import kotlinx.android.synthetic.main.goals_fragment.*
import kotlinx.android.synthetic.main.goals_fragment.view.*
import kotlinx.android.synthetic.main.goals_item.view.*
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.screens.assistant.AssistantActivity
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity
import java.util.*


class GoalsFragment : Fragment(), GoalsContract.View {


    private lateinit var presenter: GoalsContract.Presenter

    private lateinit var goalsAdapter: GoalsAdapter

    var selectionMode: ActionMode? = null
        private set

    private val selectionModeCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            activity?.menuInflater?.inflate(R.menu.menu_goal_storage_contextual, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            val counter = goalsAdapter.selectedItemsCount

            mode.title = resources.getQuantityString(R.plurals.selected_title, counter, counter)

            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            return when (item.itemId) {
                android.R.id.home         -> {
                    mode.finish()
                    true
                }
                R.id.menu_delete_selected -> {
                    goalsAdapter.removeSelected()
                    true
                }
                else                      ->
                    false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            goalsAdapter.deselectAll()
            selectionMode = null
        }
    }

    private fun initGoalsAdapter(root: View) {
        goalsAdapter = GoalsAdapter(presenter.goals as ArrayList<InternalPokemon>)
        goalsAdapter.setHasStableIds(true)

        root.goals_list.adapter = goalsAdapter
        root.goals_list.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN && root.goals_list.findChildViewUnder(event.x, event.y) == null) {
                selectionMode?.finish()
            }
            false
        }
        root.goals_list.setHasFixedSize(true)

        val linearLayoutManager = LinearLayoutManager(context)

        val dividerItemDecoration = DividerItemDecoration(root.goals_list.context, linearLayoutManager.orientation)
        root.goals_list.addItemDecoration(dividerItemDecoration)

        root.goals_list.layoutManager = linearLayoutManager
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.goals_fragment, container, false)

        //Recovers a reference to the presenter injected in the activity
        presenter = (activity as GoalsActivity).goalsPresenter

        //Links this view to the presenter
        presenter.setGoalsView(this)

        initGoalsAdapter(root)

        (activity as GoalsActivity).add_goal_fab.setOnClickListener { presenter.addNewGoal() }

        return root
    }

    override fun onResume() {
        super.onResume()

        goalsAdapter.updateItems(presenter.goals)
        showBackgroundHint(goalsAdapter.goalList.isEmpty())
    }

    override fun showCreationActivity() {

        val intent = Intent(context, CreationActivity::class.java)
        intent.putExtra(CreationActivity.TYPE_ID, CreationActivity.GOAL)
        startActivity(intent)
    }

    override fun showAssistantActivity(animate: Boolean) {

        val intent = Intent(context, AssistantActivity::class.java)

        val navPair = Pair.create<View, String>(activity?.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME)

        if (animate) {
            val transitionActivityOptions: ActivityOptions =
                ActivityOptions.makeSceneTransitionAnimation(
                    activity,
//                    Pair.create<View, String>
//                        (activity?.findViewById(android.R.id.navigationBarBackground), Window.NAVIGATION_BAR_BACKGROUND_TRANSITION_NAME), //TODO: check for null
                    Pair.create<View, String>
                        (activity?.appbar, "top"),
                    Pair.create<View, String>
                        (goalsAdapter.clickedPokemonView, "profile"),
                    Pair.create<View, String>
                        (goalsAdapter.clickedBackgroundView, "pokemon_background_transition"))

            startActivity(intent, transitionActivityOptions.toBundle())
        } else
            startActivity(intent)
    }

    override fun updateGoalsList() {
        goalsAdapter.updateItems(presenter.goals)
        goals_list.smoothScrollToPosition(0)
    }

    fun fastUpdateGoals() {
        goalsAdapter.notifyDataSetChanged()
        showBackgroundHint(goalsAdapter.goalList.isEmpty())
    }

    override fun showBackgroundHint(show: Boolean) {

        if (show) {
            goals_list.visibility = View.GONE
            hint_no_goals.visibility = View.VISIBLE
            val spannable = hint_no_goals.findViewById(R.id.spannable) as TextView
            spannable.movementMethod = LinkMovementMethod.getInstance()

            val text = getString(R.string.hint_no_goals)

            // Initialize a new SpannableStringBuilder instance
            val ssBuilder = SpannableStringBuilder(text)

            val clickableSpan = object : ClickableSpan() {
                override fun onClick(widget: View) {
                    presenter.addNewGoal()
                }
            }
            ssBuilder.setSpan(
                clickableSpan, // Span to add
                text.indexOf(getString(R.string.hint_no_goals_link)), // Start of the span (inclusive)
                text.indexOf(getString(R.string.hint_no_goals_link)) + getString(R.string.hint_no_goals_link).length, // End of the span (exclusive)
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE // Do not extend the span when text add later
            )
            spannable.text = ssBuilder
        } else {
            hint_no_goals.visibility = View.GONE
            goals_list.visibility = View.VISIBLE
        }
    }

    override fun showUndoAction() {

        val snackbar = activity?.add_goal_fab?.let {
            Snackbar.make(it, R.string.snack_goals_removed, 10000).setAction(R.string.label_undo) {
                presenter.restoreRemovedGoals()
            }
        }

        val text = snackbar?.view?.findViewById<TextView>(android.support.design.R.id.snackbar_text)

        text?.gravity = Gravity.CENTER_HORIZONTAL

        snackbar?.show()
    }

    fun startSelectionMode() {
        selectionMode = (activity as AppCompatActivity).startSupportActionMode(selectionModeCallback)
    }

    companion object {

        val TAG: String = GoalsFragment::class.java.simpleName

        fun newInstance(): GoalsFragment = GoalsFragment()

        const val SORT_DATE = 0
        const val SORT_IVS = 1
    }

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(GoalsFragment.SORT_DATE.toLong(), GoalsFragment.SORT_IVS.toLong())
    internal annotation class SortFlag


    private inner class GoalsAdapter(val goalList: ArrayList<InternalPokemon>) : RecyclerView.Adapter<GoalsAdapter.ViewHolder>() {

        var clickedPokemonView: View? = null
        var clickedBackgroundView: View? = null

        fun itemAt(position: Int): InternalPokemon = goalList[position]

        fun saveLastClicked(internalPokemon: InternalPokemon, clickedView: View, backgroundView: View) {
            clickedPokemonView = clickedView
            clickedBackgroundView = backgroundView

            presenter.processGoalSelection(internalPokemon)
        }

        private val selectionsList = HashSet<Int>()

        private val sortFlag = SORT_DATE

        private fun sortList(list: List<InternalPokemon>, @SortFlag flag: Int) {
            if (flag == SORT_DATE)
                Collections.sort(list, DateComparator())
            else if (flag == SORT_IVS)
                Collections.sort(list, IVsComparator())
        }

        fun updateItems(newGoals: List<InternalPokemon>) {

            /*Sorts the newly received list in the same way that the current list is sorted*/
            sortList(newGoals, sortFlag)


            /*Checks the differences between the current list and the newly received list*/
            val diffCallback = GoalDiffCallback(goalList, newGoals)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            /*Substitutes the values of the old list with the new received values, and then notifies the current
            * adapter of the changes for the recyclerView to animate accordingly*/
            goalList.clear()
            goalList.addAll(newGoals)

            diffResult.dispatchUpdatesTo(this)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

            val context = parent.context
            val inflater = LayoutInflater.from(context)

            val inflatedView = inflater.inflate(R.layout.goals_item, parent, false)


            return ViewHolder(inflatedView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            val currentGoal: InternalPokemon = try {
                goalList[position]
            } catch (e: IndexOutOfBoundsException) {
                InternalPokemon()
            }

            holder.bindGoal(currentGoal)

            if (selectionsList.contains(position)) {
                var drawable = holder.goalBackground.background
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable.mutate(), context?.let { ContextCompat.getColor(it, R.color.colorPrimaryLight) } ?: 0)
            } else {
                var drawable = holder.goalBackground.background
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable.mutate(), context?.let { ContextCompat.getColor(it, R.color.white) } ?: 0)
            }

        }

        override fun getItemCount(): Int {
            return goalList.size
        }

        override fun getItemId(position: Int): Long {
            return goalList[position].dateCreated
        }

        val selectedItemsCount: Int
            get() = selectionsList.size

        fun deselectAll() {
            if (!selectionsList.isEmpty()) {

                val tempList = HashSet(selectionsList)

                selectionsList.clear()

                if (!tempList.isEmpty()) {
                    for (i in tempList) {
                        notifyItemChanged(i)
                    }
                }

            }
        }

        fun removeSelected() {

            val goalsToBeRemoved = ArrayList<InternalPokemon>(selectionsList.size)
            selectionsList.mapTo(goalsToBeRemoved) { goalList[it] }
            presenter.removeGoals(goalsToBeRemoved)

            if (selectionMode != null)
                selectionMode!!.finish()

        }

        fun setSelection(position: Int) {
            if (selectionsList.contains(position)) {
                selectionsList.remove(position)
                if (selectionsList.isEmpty() && selectionMode != null)
                    selectionMode!!.finish()
            } else {
                selectionsList.add(position)
            }

            selectionMode?.invalidate()

            goalsAdapter.notifyItemChanged(position)
        }

        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

            private val goalIVs = arrayOf(itemView.pokemon_iv_hp,
                itemView.pokemon_iv_atk,
                itemView.pokemon_iv_def,
                itemView.pokemon_iv_satk,
                itemView.pokemon_iv_sdef,
                itemView.pokemon_iv_spd)

            private val goalIcon: ImageView = itemView.pokemon_icon

            val goalBackground: View = itemView.pokemon_background


            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            fun bindGoal(goal: InternalPokemon) {
                itemView.pokemon_name.text = presenter.getExternalPokemon(goal.externalId)?.name ?: getString(R.string.invalid_pokemon)

                val iconId = presenter.getPokemonIconId(goal.externalId)
                val icon = context?.let { ContextCompat.getDrawable(it, iconId) }
                itemView.pokemon_icon.setImageDrawable(icon)

                val nature = presenter.getNature(goal.natureId)?.name?.capitalize() ?: getString(R.string.invalid_nature)
                val ability = presenter.getAbilityName(goal.externalId, goal.abilitySlot) ?: getString(R.string.invalid_ability)

                itemView.pokemon_extra_info.text = getString(R.string.goal_extra_info, nature, ability)

                //Tints the IV drawable according to if the selected goal has it
                for (i in goalIVs.indices) {
                    val drawable = goalIVs[i].drawable
                    val wrappedDrawable = DrawableCompat.wrap(drawable).mutate()

                    val ivColor: Int
                    if (goal.IVs[i] > 0) {
                        ivColor = context?.let { ContextCompat.getColor(it, R.color.medium_grey) } ?: 0
                        DrawableCompat.setTint(wrappedDrawable, ivColor)
                    } else {
                        ivColor = context?.let { ContextCompat.getColor(it, R.color.iv_disabled_light) } ?: 0
                        DrawableCompat.setTint(wrappedDrawable, ivColor)
                    }
                }
            }

            override fun onClick(v: View) {

                if (selectionMode == null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        goalIcon.transitionName = "profile"
                        goalBackground.transitionName = "pokemon_background_transition"
                    }
                    saveLastClicked(itemAt(adapterPosition), goalIcon, goalBackground)
                } else {
                    setSelection(adapterPosition)
                }
            }

            override fun onLongClick(v: View): Boolean {
                if (selectionMode == null)
                    startSelectionMode()
                setSelection(adapterPosition)

                return true
            }
        }

        internal inner class GoalDiffCallback(private val oldList: List<InternalPokemon>, private val newList: List<InternalPokemon>) :
            DiffUtil.Callback() {

            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // add a unique ID property on Contact and expose a getId() method
                return oldList[oldItemPosition].internalId == newList[newItemPosition].internalId && oldList[oldItemPosition].externalId == newList[newItemPosition].externalId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldGoal = oldList[oldItemPosition]
                val newGoal = newList[newItemPosition]

                return oldGoal.externalId == newGoal.externalId && Arrays.equals(oldGoal.IVs, newGoal.IVs)
            }
        }

        internal inner class DateComparator : Comparator<InternalPokemon> {

            override fun compare(o1: InternalPokemon, o2: InternalPokemon): Int {
                return (o1.dateCreated - o2.dateCreated).toInt()
            }
        }

        internal inner class IVsComparator : Comparator<InternalPokemon> {

            override fun compare(o1: InternalPokemon, o2: InternalPokemon): Int {
                val ivs1 = o1.IVs.count { it > 0 }
                val ivs2 = o2.IVs.count { it > 0 }

                return if (ivs1 == ivs2)
                    (o1.dateCreated - o2.dateCreated).toInt()
                else
                    ivs2 - ivs1
            }
        }
    }
}
