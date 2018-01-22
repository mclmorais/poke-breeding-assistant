package marcelo.breguenait.breedingassistant.screens.creation

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import javax.inject.Inject

/**
 * Created by Marcelo on 17/01/2018.
 */
class CreationPresenter @Inject constructor(val externalRepository: ExternalRepository): CreationContract.Presenter {

    private lateinit var createPokemonView: CreationContract.View

    override fun selectPokemon() {
        createPokemonView.showSelectPokemonFragment()
    }

    override fun setView(view: CreationContract.View) {
        createPokemonView = view
    }
}