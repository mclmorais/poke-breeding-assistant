package marcelo.breguenait.breedingassistant.screens.selection

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon
import marcelo.breguenait.breedingassistant.utils.CachedPokemonIcons
import java.util.ArrayList
import javax.inject.Inject

/**
 * Created by Marcelo on 22/01/2018.
 */
class SelectionPresenter @Inject constructor(
    val cachedPokemonIcons: CachedPokemonIcons, val externalRepository: ExternalRepository
) :
    SelectionContract.Presenter {

    private lateinit var selectPokemonView: SelectionContract.View

    override var pokemonFilterId: Int = -1

    override var familySize = 0

    override var eggGroupSize = 0

    override fun getPokemonIconId(id: Int): Int = cachedPokemonIcons.getIconId(id)

    override fun finishSelection(id: Int) {
        selectPokemonView.sendSelectedPokemonId(id)
    }

    override fun setView(selectionView: SelectionContract.View) {
        this.selectPokemonView = selectionView
    }

    override val externalPokemons: ArrayList<ExternalPokemon>
        get() {

            val allPokemons = externalRepository.externalPokemons

            if (pokemonFilterId != -1) {

                val familyPokemons = externalRepository.getAllFromSameFamily(pokemonFilterId)

                familySize = familyPokemons.size

                val eggGroupPokemons: ArrayList<ExternalPokemon> = externalRepository.getAllFromEggGroups(pokemonFilterId) as ArrayList<ExternalPokemon>

                allPokemons.removeAll(eggGroupPokemons)

                eggGroupPokemons.removeAll(familyPokemons)

                eggGroupSize = eggGroupPokemons.size

                allPokemons.addAll(0, familyPokemons)

                allPokemons.addAll(familySize, eggGroupPokemons)
            }

            return allPokemons
        }

}