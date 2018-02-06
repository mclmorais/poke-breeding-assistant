package marcelo.breguenait.breedingassistant.screens.assistant

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.drawable.DrawableCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.view.ActionMode
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.assistant_activity.*
import kotlinx.android.synthetic.main.assistant_stored_fragment.view.*
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity
import marcelo.breguenait.breedingassistant.utils.Genders
import marcelo.breguenait.breedingassistant.utils.Utility
import java.util.*
import javax.inject.Inject


class StoredPokemonFragment : Fragment(), AssistantContract.StorageView {

    @Inject
    lateinit var presenter: AssistantContract.Presenter

    private var gridLayoutManager: GridLayoutManager? = null

    private var linearLayoutManager: LinearLayoutManager? = null

    val viewTypeId = 1

    private var storedPokemonsAdapter: StoredPokemonsAdapter? = null

    private var selectionMode: ActionMode? = null

    internal var selectionCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            activity!!.menuInflater.inflate(R.menu.menu_assistant_storage_contextual, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            val counter = storedPokemonsAdapter!!.selectedItemsCount
            val selectedText: String
            if (counter == 1)
                selectedText = getString(R.string.selected_title_singular)
            else
                selectedText = getString(R.string.selected_title_plural)


            val title = String.format(selectedText, counter)

            mode.title = title
            return true
        }

        override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
            when (item.itemId) {
                android.R.id.home         -> {
                    mode.finish()
                    return true
                }
                R.id.menu_delete_selected -> {
                    storedPokemonsAdapter!!.removeSelected()
                    return true
                }
                else                      -> return false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            storedPokemonsAdapter!!.deselectAll()
            selectionMode = null
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.assistant_stored_fragment, container, false)

        presenter = (activity as AssistantActivity).assistantPresenter
        presenter.setStorageView(this)

        storedPokemonsAdapter = StoredPokemonsAdapter()


        val numberOfColumns = Utility.calculateNumberOfColumns(context, resources.getDimension(R.dimen.item_stored_width).toInt())
        gridLayoutManager = GridLayoutManager(context, numberOfColumns)
        linearLayoutManager = LinearLayoutManager(context)

        view.stored_pokemon_list.adapter = storedPokemonsAdapter
        view.stored_pokemon_list.layoutManager = gridLayoutManager

        return view
    }

    override fun onResume() {
        super.onResume()
        presenter.startStored()
    }


    override fun initialized(): Boolean {
        return !storedPokemonsAdapter!!.storedPokemons.isEmpty()
    }

    override fun updateStoredPokemons(storedPokemons: List<InternalPokemon>) {
        storedPokemonsAdapter!!.updateItems(storedPokemons)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        presenter.result(requestCode, resultCode)
    }

    override fun showSuccessfulStorage() {
        activity?.let { Snackbar.make(it.fab_add, "Pokémon Stored!", Snackbar.LENGTH_LONG).show() }
    }


    internal fun showEditStored(internalPokemon: InternalPokemon) {
        val intent = Intent(context, CreationActivity::class.java)
        intent.putExtra(CreationActivity.TYPE_ID, CreationActivity.STORED)
        intent.putExtra(CreationActivity.FILTER_ID, internalPokemon.externalId)
        intent.putExtra(CreationActivity.EXISTENT_ID, internalPokemon.internalId)
        startActivityForResult(intent, CreationActivity.REQUEST_EDIT_STORED)
    }

    internal inner class StoredPokemonsAdapter : RecyclerView.Adapter<StoredPokemonsAdapter.ViewHolder>() {

        var storedPokemons: MutableList<InternalPokemon> = ArrayList()

        private val selectionsList = HashSet<Int>()

        val selectedItemsCount: Int
            get() = selectionsList.size

        fun updateItems(newPokemons: List<InternalPokemon>) {

            val diff = CompatibleDiff(storedPokemons, newPokemons)
            val diffResult = DiffUtil.calculateDiff(diff)

            storedPokemons.clear()
            storedPokemons.addAll(newPokemons)

            diffResult.dispatchUpdatesTo(this)

        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)

            val inflatedView = inflater.inflate(R.layout.assistant_item_stored_small, parent, false)

            return ViewHolder(inflatedView)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val externalId = storedPokemons[position].externalId

            val goal = presenter.currentGoal

            val icon = Utility.getIcon(externalId, context)
            holder.viewIcon.setImageDrawable(icon)

            @Genders.GendersFlag
            val gender = storedPokemons[position].gender


            if (selectionsList.contains(position)) {
                var drawable = holder.viewIcon.background
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(context!!, R.color
                    .colorPrimaryLight))
            } else {
                var drawable = holder.viewIcon.background
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(context!!, R.color.colorPrimary))
            }

            val genderColor: Int

            when (gender) {
                Genders.MALE       -> genderColor = ContextCompat.getColor(context!!, R.color.male)
                Genders.FEMALE     -> genderColor = ContextCompat.getColor(context!!, R.color.female)
                Genders.GENDERLESS -> genderColor = ContextCompat.getColor(context!!, R.color.genderless)
                Genders.DITTO      -> genderColor = ContextCompat.getColor(context!!, R.color.ditto)
                else               -> genderColor = ContextCompat.getColor(context!!, android.R.color.black)
            }

            var genderDrawable = holder.viewGender.drawable
            genderDrawable = DrawableCompat.wrap(genderDrawable)
            DrawableCompat.setTint(genderDrawable.mutate(), genderColor)


            val activeColor = ContextCompat.getColor(context!!, R.color.iv_enabled_light)
            val inactiveColor = ContextCompat.getColor(context!!, R.color.iv_disabled_light)

            val activeIndicatorColor = ContextCompat.getColor(context!!, R.color.colorPrimaryLight)
            val inactiveIndicatorColor = ContextCompat.getColor(context!!, R.color.colorDisabled)


            val abilityBackground = DrawableCompat.wrap(holder.abilityIndicator.background).mutate()
            if (storedPokemons[position].gender != Genders.DITTO) {
                if (storedPokemons[position].abilitySlot == goal!!.abilitySlot)
                    DrawableCompat.setTint(abilityBackground, activeIndicatorColor)
                else
                    DrawableCompat.setTint(abilityBackground, inactiveIndicatorColor)
            } else
                DrawableCompat.setTint(abilityBackground, inactiveIndicatorColor)

            val natureBackground = DrawableCompat.wrap(holder.natureIndicator.background).mutate()
            if (storedPokemons[position].natureId == goal!!.natureId)
                DrawableCompat.setTint(natureBackground, activeIndicatorColor)
            else
                DrawableCompat.setTint(natureBackground, inactiveIndicatorColor)


            for (i in holder.viewIVs.indices) {
                //TODO: remove from here! Colors should be decided elsewhere

                val drawable = holder.viewIVs[i]?.drawable
                var wrappedDrawable = DrawableCompat.wrap(drawable!!)
                wrappedDrawable = wrappedDrawable.mutate()
                if (storedPokemons[position].IVs[i] > 0)
                    DrawableCompat.setTint(wrappedDrawable, activeColor)
                else
                    DrawableCompat.setTint(wrappedDrawable, inactiveColor)
            }
        }

        override fun getItemViewType(position: Int): Int {
            return viewTypeId
        }

        override fun getItemCount(): Int {
            return storedPokemons.size
        }

        fun setSelection(position: Int) {
            if (selectionsList.contains(position)) {
                selectionsList.remove(position)
                if (selectionsList.isEmpty() && selectionMode != null)
                    selectionMode!!.finish()
            } else {
                selectionsList.add(position)
            }

            if (selectionMode != null)
                selectionMode!!.invalidate()

            notifyItemChanged(position)
        }

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
            for (selection in selectionsList) {
                goalsToBeRemoved.add(storedPokemons[selection])
            }
            presenter.removeStoredPokemons(goalsToBeRemoved)

            if (selectionMode != null)
                selectionMode!!.finish()
        }


        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

            val viewIVs = arrayOfNulls<ImageView>(6)

            val viewIcon: ImageView

            val viewGender: ImageView

            val abilityIndicator: TextView
            val natureIndicator: TextView

            init {
                this.viewIcon = itemView.findViewById(R.id.stored_icon)
                viewIVs[0] = itemView.findViewById(R.id.stored_iv_hp)
                viewIVs[1] = itemView.findViewById(R.id.stored_iv_atk)
                viewIVs[2] = itemView.findViewById(R.id.stored_iv_def)
                viewIVs[3] = itemView.findViewById(R.id.stored_iv_satk)
                viewIVs[4] = itemView.findViewById(R.id.stored_iv_sdef)
                viewIVs[5] = itemView.findViewById(R.id.stored_iv_spd)
                viewGender = itemView.findViewById(R.id.stored_gender)
                abilityIndicator = itemView.findViewById(R.id.stored_ability_indicator)
                natureIndicator = itemView.findViewById(R.id.stored_nature_indicator)

                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun onClick(v: View) {
                if (selectionMode == null) {
                    showEditStored(storedPokemons[adapterPosition])
                } else {
                    setSelection(adapterPosition)
                }
            }

            override fun onLongClick(v: View): Boolean {
                if (selectionMode == null)
                    selectionMode = (activity as AppCompatActivity).startSupportActionMode(selectionCallback)

                setSelection(adapterPosition)

                return true
            }


        }

        internal inner class CompatibleDiff(private val oldList: List<InternalPokemon>, private val newList: List<InternalPokemon>) :
            DiffUtil.Callback() {

            override fun getOldListSize(): Int {
                return oldList.size
            }

            override fun getNewListSize(): Int {
                return newList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldItem = oldList[oldItemPosition]
                val newItem = newList[newItemPosition]

                return oldItem.internalId == newItem.internalId
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {

                return false

                //                InternalPokemon oldItem = oldList.get(oldItemPosition);
                //                InternalPokemon newItem = newList.get(newItemPosition);
                //
                //                return oldItem.getExternalId() == newItem.getExternalId() &&
                //                        oldItem.getGender() == newItem.getGender() &&
                //                        oldItem.getIVs() == newItem.getIVs() &&
                //                        oldItem.getNatureId() == newItem.getNatureId() &&
                //                        oldItem.getAbilitySlot() == newItem.getAbilitySlot();

            }
        }

    }

    companion object {

        fun newInstance(): StoredPokemonFragment {
            return StoredPokemonFragment()
        }
    }


}
