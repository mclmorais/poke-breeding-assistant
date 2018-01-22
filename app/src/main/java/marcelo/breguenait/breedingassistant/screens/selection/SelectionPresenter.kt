package marcelo.breguenait.breedingassistant.screens.selection

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.utils.CachedPokemonIcons
import javax.inject.Inject

/**
 * Created by Marcelo on 22/01/2018.
 */
class SelectionPresenter @Inject constructor(
    val icons: CachedPokemonIcons, val externalRepository: ExternalRepository
) :
    SelectionContract.Presenter {

    private lateinit var selectPokemonView: SelectionContract.View

    override var pokemonFilterId: Int = -1

    override var familySize = 0

    override var eggGroupSize = 0

    override fun getPokemonIconId(id: Int): Int = 0//cachedPokemonIcons.getIconId(id)

    override fun finishSelection(id: Int) {
        selectPokemonView.sendSelectedPokemonId(id)
    }

    override fun setView(selectionView: SelectionContract.View) {
        this.selectPokemonView = selectionView
    }
}