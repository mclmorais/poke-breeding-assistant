package marcelo.breguenait.breedingassistant.screens.goals

import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.data.internal.InternalRepository
import marcelo.breguenait.breedingassistant.screens.assistant.BoxContract
import marcelo.breguenait.breedingassistant.utils.CachedPokemonIcons
import javax.inject.Inject


class GoalsPresenter @Inject
internal constructor(private val internalRepository: InternalRepository,
                     private val externalRepository: ExternalRepository,
                     private val cachedIcons: CachedPokemonIcons) : GoalsContract.Presenter {

    private lateinit var goalsView: GoalsContract.View

    private lateinit var boxView: BoxContract.BoxView

    override val goals: List<InternalPokemon>
        get() = internalRepository.goals

    private val temporaryRestorableGoals = ArrayList<InternalPokemon>()

    override fun setGoalsView(view: GoalsContract.View) {
        goalsView = view
    }

    override fun processGoalSelection(selectedPokemon: InternalPokemon) {
        internalRepository.setCurrentGoalPokemon(selectedPokemon.internalId)
        goalsView.showAssistantActivity(true)
    }

    override fun addNewGoal() {
        goalsView.showCreationActivity()
    }

    override fun removeGoals(goalsToBeRemoved: List<InternalPokemon>) {

        temporaryRestorableGoals.addAll(goalsToBeRemoved)

        internalRepository.removeGoalPokemons(goalsToBeRemoved)

        goalsView.updateGoalsList()

        goalsView.showUndoAction()

        goalsView.showBackgroundHint(goals.isEmpty())

    }

    override fun restoreRemovedGoals() {
        temporaryRestorableGoals.forEach { goal ->
            internalRepository.addInternalPokemon(goal, InternalRepository.INTERNAL_GOAL)
        }
        goalsView.updateGoalsList()
        temporaryRestorableGoals.clear()
        goalsView.showBackgroundHint(goals.isEmpty())
    }

    override fun getExternalPokemon(externalId: Int): ExternalPokemon? = externalRepository.getExternalPokemon(externalId)

    override fun getNature(natureId: Int): ExternalNature? = externalRepository.getNature(natureId)


    override fun getAbilityName(pokemonId: Int, abilitySlot: Int): String? = externalRepository.getAbility(pokemonId, abilitySlot)?.name


    override fun getPokemonIconId(id: Int): Int {
        return cachedIcons.getIconId(id)
    }

    override val currentGoal: InternalPokemon? = null

    override fun initBox() {
        if (!boxView.initialized()) {
            boxView.updateStoredPokemons(internalRepository.getStoredPokemons())
        }
    }

    override fun result(requestCode: Int, resultCode: Int) {
        boxView.updateStoredPokemons(internalRepository.getStoredPokemons())
    }

    override fun removeStoredPokemons(stored: List<InternalPokemon>) {
        TODO("not implemented")
    }

    override fun setStorageView(boxView: BoxContract.BoxView) {
        this.boxView = boxView
    }
}