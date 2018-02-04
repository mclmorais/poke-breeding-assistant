package marcelo.breguenait.breedingassistant.screens.selection

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.util.DiffUtil
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.selection_dialog_fragment.view.*
import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity
import marcelo.breguenait.breedingassistant.utils.Utility


/**
 * Created by Marcelo on 22/01/2018.
 */
class SelectionDialogFragment : DialogFragment(), SelectionContract.View {

    /**
     * This interface is used to that this selection fragment can provide data about the selection
     * to the fragment or activity that invoked it.
     *
     * [onPokemonSelected] should be used for data update on the base fragment/activity.
     *
     * [onSelectorDismissed] should treat the edge case when the user doesn't select a Pokémon and
     * the base fragment/activity doesn't already have a selection (i.e. it should also close).
     */
    interface PokemonSelectionListener {
        fun onPokemonSelected(id: Int)
        fun onSelectorDismissed()
    }

    private lateinit var presenter: SelectionContract.Presenter

    private lateinit var adapter: SelectablePokemonsAdapter

    private fun initList(recyclerView: RecyclerView) {
        adapter = SelectablePokemonsAdapter(presenter.externalPokemons)

        val numberOfColumns = Utility.calculateNumberOfColumns(context, resources.getDimension(R.dimen.item_selectable_width).toInt())

        val gridLayoutManager = GridLayoutManager(context, numberOfColumns)

        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (adapter.getItemViewType(position)) {
                    -1   -> numberOfColumns
                    else -> 1
                }
            }
        }

        recyclerView.adapter = adapter
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
    }

    private fun initToolbar(toolbar: Toolbar) {
        toolbar.inflateMenu(R.menu.menu_select_pokemon)
    }

    private fun initSearch(searchView: MaterialSearchView, toolbar: Toolbar) {

        searchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter.filter(newText)
                return true
            }

        })

        searchView.setMenuItem(toolbar.menu.findItem(R.id.menu_search))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = (activity as CreationActivity).selectionPresenter
        presenter.setView(this)

        if(targetFragment !is PokemonSelectionListener)
            throw Exception("the base fragment must implement PokemonSelectionListener!")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        val root = inflater.inflate(R.layout.selection_dialog_fragment, container, false)

        initList(root.list_selectable_pokemon)
        initToolbar(root.toolbar)
        initSearch(root.search_view, root.toolbar)

        return root
    }


    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    private inner class SelectablePokemonsAdapter(selectablePokemonList: List<ExternalPokemon>)
        : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

        internal var selectablePokemonList: List<ExternalPokemon> = ArrayList()

        internal var filteredSelectableList: MutableList<ExternalPokemon> = ArrayList()

        internal var showIncompatible = false

        internal var fakePokemons: MutableList<ExternalPokemon> = ArrayList(3)

        init {

            if (presenter.pokemonFilterId != -1 && !showIncompatible) {
                this.selectablePokemonList = selectablePokemonList.subList(0, presenter.familySize + presenter.eggGroupSize)
            } else {
                this.selectablePokemonList = selectablePokemonList
            }
            this.filteredSelectableList = ArrayList(this.selectablePokemonList)

            if (presenter.pokemonFilterId != -1) {

                fakePokemons.add(ExternalPokemon(-1))
                fakePokemons.add(ExternalPokemon(-2))
                fakePokemons.add(ExternalPokemon(-3))

                filteredSelectableList.add(0, fakePokemons[0])
                filteredSelectableList.add(1 + presenter.familySize, fakePokemons[1])
                if (showIncompatible)
                    filteredSelectableList.add(2 + presenter.eggGroupSize + presenter.familySize, fakePokemons[2])

            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

            val view: View
            val context = parent.context
            val inflater = LayoutInflater.from(context)

            if (viewType > 0)
                view = inflater.inflate(R.layout.selection_item_small, parent, false)
            else
                view = inflater.inflate(R.layout.selection_item_header, parent, false)

            if (viewType > 0)
                return ItemViewHolder(view)
            else
                return HeaderViewHolder(view)
        }


        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {


            if (getItemViewType(position) >= 0) {
                val itemViewHolder = holder as ItemViewHolder

                val number = filteredSelectableList[position].number
                itemViewHolder.selectablePokemonNumber.text = String.format("%d",number)

                val iconId = presenter.getPokemonIconId(filteredSelectableList[position].id)
                Glide.with(itemViewHolder.selectablePokemonIcon.context).load(iconId).into(itemViewHolder
                    .selectablePokemonIcon)
            } else {
                val headerViewHolder = holder as HeaderViewHolder

                if (filteredSelectableList[position].id == -1)
                    headerViewHolder.headerTitle.setText(R.string.label_same_family)
                if (filteredSelectableList[position].id == -2)
                    headerViewHolder.headerTitle.setText(R.string.label_same_egg_group)
                if (filteredSelectableList[position].id == -3)
                    headerViewHolder.headerTitle.setText(R.string.label_incompatible)
            }
        }

        override fun getItemCount(): Int {
            return filteredSelectableList.size
        }

        override fun getItemViewType(position: Int): Int {
            if (filteredSelectableList[position].id < 0)
                return -1
            else
                return 1
        }

        internal fun itemAt(i: Int): ExternalPokemon {
            return filteredSelectableList[i]
        }

        internal fun updateItems(newFiltered: ArrayList<ExternalPokemon>) {

            /*Sorts the newly received list in the same way that the current list is sorted*/
            //sortList(newGoals, sortFlag);

            /*Checks the differences between the current list and the newly received list*/
            val diffCallback = GoalDiffCallback(filteredSelectableList, newFiltered)
            val diffResult = DiffUtil.calculateDiff(diffCallback)

            /*Substitutes the values of the old list with the new received values, and then notifies the current
            * adapter of the changes for the recyclerView to animate accordingly*/
            filteredSelectableList.clear()
            filteredSelectableList.addAll(newFiltered)

            diffResult.dispatchUpdatesTo(this)
            view?.list_selectable_pokemon?.scrollToPosition(0)
        }

        override fun getFilter(): Filter {
            return object : Filter() {
                override fun performFiltering(constraint: CharSequence?): Filter.FilterResults {
                    var superConstraint = constraint

                    if (superConstraint != null)
                        superConstraint = superConstraint.toString().toLowerCase()

                    val filteredSelectablePokemons = ArrayList<ExternalPokemon>()

                    if (presenter.pokemonFilterId != -1) {

                        val familyList = selectablePokemonList.subList(0, presenter.familySize)
                        val eggGroupList = selectablePokemonList.subList(presenter.familySize, presenter.familySize + presenter.eggGroupSize)
                        val incompatibleList = selectablePokemonList.subList(presenter.familySize + presenter.eggGroupSize, selectablePokemonList.size)


                        /*Adds Related Header*/
                        filteredSelectablePokemons.add(fakePokemons[0])

                        /*Adds family compatible pokemons*/
                        for (pokemon in familyList) {
                            if (superConstraint == null) superConstraint = ""

                            if (pokemon.name.toLowerCase().contains(superConstraint))
                                filteredSelectablePokemons.add(pokemon)
                            else if (Integer.toString(pokemon.id).contains(superConstraint))
                                filteredSelectablePokemons.add(pokemon)
                        }

                        /*If no family compatible pokemons were added, removes the header*/
                        if (filteredSelectablePokemons[filteredSelectablePokemons.size - 1] === fakePokemons[0])
                            filteredSelectablePokemons.removeAt(filteredSelectablePokemons.size - 1)


                        /*Adds the Compatible Header*/
                        filteredSelectablePokemons.add(fakePokemons[1])

                        for (pokemon in eggGroupList) {

                            if (superConstraint == null) superConstraint = ""

                            if (pokemon.name.toLowerCase().contains(superConstraint))
                                filteredSelectablePokemons.add(pokemon)
                            else if (Integer.toString(pokemon.id).contains(superConstraint))
                                filteredSelectablePokemons.add(pokemon)
                        }

                        if (filteredSelectablePokemons[filteredSelectablePokemons.size - 1] === fakePokemons[1])
                            filteredSelectablePokemons.removeAt(filteredSelectablePokemons.size - 1)

                        if (showIncompatible) {
                            filteredSelectablePokemons.add(fakePokemons[2])

                            for (pokemon in incompatibleList) {

                                if (superConstraint == null) superConstraint = ""

                                if (pokemon.name.toLowerCase().contains(superConstraint))
                                    filteredSelectablePokemons.add(pokemon)
                                else if (Integer.toString(pokemon.id).contains(superConstraint))
                                    filteredSelectablePokemons.add(pokemon)

                            }

                            if (filteredSelectablePokemons[filteredSelectablePokemons.size - 1] === fakePokemons[2])
                                filteredSelectablePokemons.removeAt(filteredSelectablePokemons.size - 1)
                        }

                        val filterResults = Filter.FilterResults()
                        filterResults.count = filteredSelectablePokemons.size
                        filterResults.values = filteredSelectablePokemons
                        return filterResults
                    } else {
                        for (pokemon in selectablePokemonList) {

                            if (superConstraint == null) superConstraint = ""

                            if (pokemon.name.toLowerCase().contains(superConstraint))
                                filteredSelectablePokemons.add(pokemon)
                            else if (Integer.toString(pokemon.id).contains(superConstraint))
                                filteredSelectablePokemons.add(pokemon)
                        }

                        val filterResults = Filter.FilterResults()
                        filterResults.count = filteredSelectablePokemons.size
                        filterResults.values = filteredSelectablePokemons
                        return filterResults
                    }

                }

                override fun publishResults(constraint: CharSequence, results: Filter.FilterResults) {
                    @Suppress("UNCHECKED_CAST")
                    updateItems(results.values as ArrayList<ExternalPokemon>)
                }
            }
        }

        internal inner class HeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

            var headerTitle: TextView = itemView.findViewById(R.id.header_title) as TextView

        }

        internal inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

            var selectablePokemonNumber: TextView = itemView.findViewById(R.id.selectable_pokemon_number) as TextView

            var selectablePokemonIcon: ImageView = itemView.findViewById(R.id.selectable_pokemon_icon) as ImageView

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(v: View) {
                presenter.finishSelection(itemAt(adapterPosition).id)
            }
        }

        internal inner class GoalDiffCallback(private val mOldList: List<ExternalPokemon>, private val mNewList: List<ExternalPokemon>) :
            DiffUtil.Callback() {

            override fun getOldListSize(): Int {
                return mOldList.size
            }

            override fun getNewListSize(): Int {
                return mNewList.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // add a unique ID property on Contact and expose a getId() method
                return mOldList[oldItemPosition].id == mNewList[newItemPosition].id
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                val oldGoal = mOldList[oldItemPosition]
                val newGoal = mNewList[newItemPosition]

                return oldGoal.id == newGoal.id
            }
        }
    }


    override fun sendSelectedPokemonId(id: Int) {
        (targetFragment as PokemonSelectionListener).onPokemonSelected(id)
        dismiss()
    }
}