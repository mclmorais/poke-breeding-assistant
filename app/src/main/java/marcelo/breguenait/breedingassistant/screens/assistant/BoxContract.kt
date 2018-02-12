package marcelo.breguenait.breedingassistant.screens.assistant

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon

interface BoxContract {

    interface Presenter {

        val currentGoal: InternalPokemon?

        fun initBox()

        fun result(requestCode: Int, resultCode: Int)

        fun removeStoredPokemons(stored: List<InternalPokemon>)

        fun setStorageView(boxView: BoxView)
    }

    interface BoxView {

        fun initialized(): Boolean

        fun updateStoredPokemons(storedPokemons: List<InternalPokemon>)

        fun showSuccessfulStorage()
    }

}