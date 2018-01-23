package marcelo.breguenait.breedingassistant.screens.creation

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.logic.StatsCalculation
import marcelo.breguenait.breedingassistant.utils.CachedPokemonIcons
import javax.inject.Inject

/**
 * Created by Marcelo on 17/01/2018.
 */
class CreationPresenter @Inject constructor(val externalRepository: ExternalRepository,
                                            val cachedPokemonIcons: CachedPokemonIcons,
                                            val statsCalculation: StatsCalculation) : CreationContract.Presenter {


    override var currentSelectionId = -1

    private var nextStats = intArrayOf(0, 0, 0, 0, 0, 0)

    private lateinit var createPokemonView: CreationContract.View

    override fun selectPokemon() {
        createPokemonView.showSelectPokemonFragment()
    }

    override fun updateSelection(id: Int) {
        currentSelectionId = id
        populateViewWithSelectedData()
    }

    private fun populateViewWithSelectedData() {

        val pokemon = externalRepository.getExternalPokemon(currentSelectionId) ?: return //TODO: RECOVER
        createPokemonView.updateSelectedPokemonName(pokemon.name)
        val iconId = cachedPokemonIcons.getIconId(currentSelectionId)
        createPokemonView.updateSelectedPokemonIcon(iconId)

        nextStats = IntArray(6)
        for (i in 0..5) {
            nextStats[i] = statsCalculation.getStat(
                currentSelectionId,
                i,
                100,
                createPokemonView.selectedNatureId,
                createPokemonView.selectedIVs[i] * 31,
                0)
        }
        createPokemonView.updateSelectedPokemonStats()


        createPokemonView.updateSelectedPokemonAbilities(externalRepository.getAbilitiesNames(currentSelectionId))

        createPokemonView.updateGenderRestrictions(pokemon.genderRestriction)


    }

    override fun setView(view: CreationContract.View) {
        createPokemonView = view
    }

    override fun getNextStat(statId: Int): Int {
        return nextStats[statId]
    }
}