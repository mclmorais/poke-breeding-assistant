package marcelo.breguenait.breedingassistant.screens.goals

import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalPokemon
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon

interface GoalsContract {

    interface View {

        fun showCreationActivity()

        fun showAssistantActivity(animate: Boolean)

        fun updateGoalsList()

        fun showBackgroundHint(show: Boolean)

        fun showUndoAction()

    }

    interface Presenter {

        val goals: List<InternalPokemon>

        fun setGoalsView(view: View)

        fun processGoalSelection(selectedPokemon: InternalPokemon)

        fun addNewGoal()

        fun removeGoals(goalsToBeRemoved: List<InternalPokemon>)

        fun restoreRemovedGoals()

        fun getExternalPokemon(externalId: Int): ExternalPokemon?

        fun getNature(natureId: Int): ExternalNature?

        fun getAbilityName(pokemonId: Int, abilitySlot: Int): String?

        fun getPokemonIconId(id: Int): Int

    }

}
