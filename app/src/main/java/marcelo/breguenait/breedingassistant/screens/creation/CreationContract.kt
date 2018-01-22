package marcelo.breguenait.breedingassistant.screens.creation

/**
 * Created by Marcelo on 17/01/2018.
 */
interface CreationContract {

    interface View {
        fun showSelectPokemonFragment()
    }

    interface Presenter {
        fun selectPokemon()

        fun setView(view: View)
    }
}