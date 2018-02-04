package marcelo.breguenait.breedingassistant.screens.creation

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemonValidator
import marcelo.breguenait.breedingassistant.data.internal.InternalRepository
import marcelo.breguenait.breedingassistant.logic.StatsCalculation
import marcelo.breguenait.breedingassistant.utils.CachedPokemonIcons
import marcelo.breguenait.breedingassistant.utils.Genders
import java.util.*
import javax.inject.Inject

/**
 * Created by Marcelo on 17/01/2018.
 */
class CreationPresenter @Inject constructor(val internalRepository: InternalRepository,
                                            val externalRepository: ExternalRepository,
                                            val cachedPokemonIcons: CachedPokemonIcons,
                                            val statsCalculation: StatsCalculation) : CreationContract.Presenter {

    var transactionType = -1

    var internalPokemonId: String? = null

    override var currentSelectionId = -1

    private var nextStats = intArrayOf(0, 0, 0, 0, 0, 0)

    private val validatedGender: Int
        @Genders.GendersFlag get() = InternalPokemonValidator.getValidatedGender(currentSelectionId, createPokemonView.selectedGender, externalRepository)

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

    private fun buildFromCurrentViewData(internalPokemonId: String?): InternalPokemon {

        val createdPokemon: InternalPokemon

        if (internalPokemonId == null)
            createdPokemon = InternalPokemon(
                currentSelectionId, Date().time,
                UUID.randomUUID().toString())
        else
            createdPokemon = getExistentPokemon(internalPokemonId)


        createdPokemon.externalId = currentSelectionId
        createdPokemon.IVs = createPokemonView.selectedIVs

        createdPokemon.gender = validatedGender
        createdPokemon.natureId = createPokemonView.selectedNatureId
        createdPokemon.abilitySlot = createPokemonView.selectedAbilitySlot

        createdPokemon.valid = InternalPokemonValidator.validate(createdPokemon, externalRepository)

        return createdPokemon
    }

    private fun getExistentPokemon(id: String): InternalPokemon {

        if (transactionType == CreationActivity.GOAL)
            return internalRepository.currentGoal!!
        else
            return internalRepository.getStoredPokemon(id)!!
    }

    override fun finishCreation() {
        val builtPokemon: InternalPokemon

        when (transactionType) {
            CreationActivity.GOAL   -> {
                builtPokemon = buildFromCurrentViewData(internalPokemonId)
                internalRepository.addInternalPokemon(builtPokemon, InternalRepository.INTERNAL_GOAL)
            }
            CreationActivity.STORED -> {
                builtPokemon = buildFromCurrentViewData(internalPokemonId)
                internalRepository.addInternalPokemon(builtPokemon, InternalRepository.INTERNAL_STORED)
            }
            else                    ->
                throw Exception(transactionType.toString()) //TODO: do something with it (warn the user that something's wrong)
        }

        createPokemonView.exitActivity(builtPokemon.internalId)
    }

}