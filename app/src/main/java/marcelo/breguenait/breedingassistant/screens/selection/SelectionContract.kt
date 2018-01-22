package marcelo.breguenait.breedingassistant.screens.selection

/**
 * Created by Marcelo on 22/01/2018.
 */
interface SelectionContract {

    interface View {
        fun sendSelectedPokemonId(id: Int)

    }

    interface Presenter {

        fun setView(selectionView: SelectionContract.View)

        val pokemonFilterId: Int

        val familySize: Int

        val eggGroupSize: Int

        fun getPokemonIconId(id: Int): Int

        fun finishSelection(id: Int)

    }
}