package marcelo.breguenait.breedingassistant.screens.assistant

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.databinding.AssistantItemStoredSmallBinding
import marcelo.breguenait.breedingassistant.databinding.AssistantStoredFragmentBinding
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity
import marcelo.breguenait.breedingassistant.utils.Genders
import marcelo.breguenait.breedingassistant.utils.Utility
import java.util.*
import javax.inject.Inject


class StoredPokemonFragment : Fragment(), BoxContract.BoxView {

    @Inject
    lateinit var presenter: BoxContract.Presenter

    private lateinit var presenterCallback: PresenterCallback

    private var gridLayoutManager: GridLayoutManager? = null

    private var linearLayoutManager: LinearLayoutManager? = null

    val viewTypeId = 1

    private var storedPokemonsAdapter: StoredPokemonsAdapter? = null

    private var selectionMode: ActionMode? = null

    private val editStoredLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        presenter.result(CreationActivity.REQUEST_EDIT_STORED, it.resultCode)
    }

    internal var selectionCallback: ActionMode.Callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
            activity!!.menuInflater.inflate(R.menu.menu_assistant_storage_contextual, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
            val counter = storedPokemonsAdapter!!.selectedItemsCount

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
                    storedPokemonsAdapter!!.removeSelected()
                    true
                }
                else                      -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode) {
            storedPokemonsAdapter!!.deselectAll()
            selectionMode = null
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        try {
            presenterCallback = activity as PresenterCallback
        } catch (e: ClassCastException) {
            throw  ClassCastException (activity.toString()
                    + " must implement " + PresenterCallback::class.java.simpleName)
        }


    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val binding = AssistantStoredFragmentBinding.inflate(inflater, container, false)

        presenter = presenterCallback.setPresenter()
        presenter.setStorageView(this)

        storedPokemonsAdapter = StoredPokemonsAdapter()


        val numberOfColumns = Utility.calculateNumberOfColumns(context, resources.getDimension(R.dimen.item_stored_width).toInt())
        gridLayoutManager = GridLayoutManager(context, numberOfColumns)
        linearLayoutManager = LinearLayoutManager(context)

        binding.storedPokemonList.adapter = storedPokemonsAdapter
        binding.storedPokemonList.layoutManager = gridLayoutManager

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        presenter.initBox()
    }


    override fun initialized(): Boolean {
        return !storedPokemonsAdapter!!.storedPokemons.isEmpty()
    }

    override fun updateStoredPokemons(storedPokemons: List<InternalPokemon>) {
        storedPokemonsAdapter!!.updateItems(storedPokemons)
    }

    override fun showSuccessfulStorage() {
        activity?.findViewById<View>(R.id.fab_add)?.let { Snackbar.make(it, "Pokémon Stored!", Snackbar.LENGTH_LONG).show() }
    }


    internal fun showEditStored(internalPokemon: InternalPokemon) {
        val intent = Intent(context, CreationActivity::class.java)
        intent.putExtra(CreationActivity.TYPE_ID, CreationActivity.STORED)
        intent.putExtra(CreationActivity.FILTER_ID, internalPokemon.externalId)
        intent.putExtra(CreationActivity.EXISTENT_ID, internalPokemon.internalId)
        editStoredLauncher.launch(intent)
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
                DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(requireContext(), R.color
                    .colorPrimaryLight))
            } else {
                var drawable = holder.viewIcon.background
                drawable = DrawableCompat.wrap(drawable)
                DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(requireContext(), R.color.colorPrimary))
            }

            val genderColor: Int

            genderColor = when (gender) {
                Genders.MALE       -> ContextCompat.getColor(requireContext(), R.color.male)
                Genders.FEMALE     -> ContextCompat.getColor(requireContext(), R.color.female)
                Genders.GENDERLESS -> ContextCompat.getColor(requireContext(), R.color.genderless)
                Genders.DITTO      -> ContextCompat.getColor(requireContext(), R.color.ditto)
                else               -> ContextCompat.getColor(requireContext(), android.R.color.black)
            }

            var genderDrawable = holder.viewGender.drawable
            genderDrawable = DrawableCompat.wrap(genderDrawable)
            DrawableCompat.setTint(genderDrawable.mutate(), genderColor)


            val activeColor = ContextCompat.getColor(requireContext(), R.color.iv_enabled_light)
            val inactiveColor = ContextCompat.getColor(requireContext(), R.color.iv_disabled_light)

            val activeIndicatorColor = ContextCompat.getColor(requireContext(), R.color.colorPrimaryLight)
            val inactiveIndicatorColor = ContextCompat.getColor(requireContext(), R.color.colorDisabled)


            val abilityBackground = DrawableCompat.wrap(holder.abilityIndicator.background).mutate()
            if (storedPokemons[position].gender != Genders.DITTO) {
                if (storedPokemons[position].abilitySlot == goal?.abilitySlot)
                    DrawableCompat.setTint(abilityBackground, activeIndicatorColor)
                else
                    DrawableCompat.setTint(abilityBackground, inactiveIndicatorColor)
            } else
                DrawableCompat.setTint(abilityBackground, inactiveIndicatorColor)

            val natureBackground = DrawableCompat.wrap(holder.natureIndicator.background).mutate()
            if (storedPokemons[position].natureId == goal?.natureId)
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
            val storedToBeRemoved = ArrayList<InternalPokemon>(selectionsList.size)
            selectionsList.mapTo(storedToBeRemoved) { storedPokemons[it] }
            presenter.removeStoredPokemons(storedToBeRemoved)

            selectionMode?.finish()
        }


        internal inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener, View.OnLongClickListener {

            private val itemBinding = AssistantItemStoredSmallBinding.bind(itemView)

            val viewIVs = arrayOf(
                itemBinding.storedIvHp,
                itemBinding.storedIvAtk,
                itemBinding.storedIvDef,
                itemBinding.storedIvSatk,
                itemBinding.storedIvSdef,
                itemBinding.storedIvSpd)

            val viewIcon: ImageView = itemView.findViewById(R.id.stored_icon)

            val viewGender: ImageView = itemView.findViewById(R.id.stored_gender)

            val abilityIndicator: TextView = itemView.findViewById(R.id.stored_ability_indicator)
            val natureIndicator: TextView = itemView.findViewById(R.id.stored_nature_indicator)

            init {
                itemView.setOnClickListener(this)
                itemView.setOnLongClickListener(this)
            }

            override fun onClick(v: View) {
                if (selectionMode == null) {
                    showEditStored(storedPokemons[bindingAdapterPosition])
                } else {
                    setSelection(bindingAdapterPosition)
                }
            }

            override fun onLongClick(v: View): Boolean {
                if (selectionMode == null)
                    selectionMode = (activity as AppCompatActivity).startSupportActionMode(selectionCallback)

                setSelection(bindingAdapterPosition)

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


    interface PresenterCallback {
        fun setPresenter(): BoxContract.Presenter
    }

    companion object {

        val TAG: String = StoredPokemonFragment::class.java.simpleName

        fun newInstance(): StoredPokemonFragment {
            return StoredPokemonFragment()
        }
    }


}
