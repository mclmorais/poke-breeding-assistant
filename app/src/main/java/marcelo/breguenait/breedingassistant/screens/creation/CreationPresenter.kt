package marcelo.breguenait.breedingassistant.screens.creation

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
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

    override val natures: ArrayList<ExternalNature>
        get() = ArrayList(externalRepository.natures.values)

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

    override fun notifyStatChanged(statId: Int) {
        if (currentSelectionId == -1) return

        nextStats[statId] = statsCalculation.getStat(currentSelectionId, statId, 100, createPokemonView
            .selectedNatureId, createPokemonView.selectedIVs[statId] * 31, 0) //TODO: getselectedIVS should look at what it already has when editing a pokemon!

        createPokemonView.updateSelectedPokemonStat(statId)
    }

    override fun getStatName(statId: Int): String {
        return externalRepository.getStatName(statId, 9)
    }

    override fun finishCreation() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

        /*    override fun finishCreation() {
        val builtPokemon: InternalPokemon

        when (transactionType) {
            CreatePokemonActivity.GOAL   -> {
                builtPokemon = buildFromCurrentViewData(internalPokemonId)
                internalRepository.addInternalPokemon(builtPokemon, InternalRepository.INTERNAL_GOAL)
            }
            CreatePokemonActivity.STORED -> {
                builtPokemon = buildFromCurrentViewData(internalPokemonId)
                internalRepository.addInternalPokemon(builtPokemon, InternalRepository.INTERNAL_STORED)
            }
            else ->
                    throw Exception(transactionType.toString())
        }

        createPokemonView.exitActivity(builtPokemon.internalId)
    }*/
    }
}