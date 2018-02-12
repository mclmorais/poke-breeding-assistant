package marcelo.breguenait.breedingassistant.screens.assistant

import marcelo.breguenait.breedingassistant.logic.CombinationHolder

/**
 * Created by Marcelo on 12/02/2017.
 */

interface AssistantContract {

    interface AssistantView {

        /*Goal information update*/
        fun updateSelectedPokemonName(name: String)

        fun updateSelectedPokemonIcon(iconId: Int)

        fun updateSelectedPokemonIVs(IVs: IntArray)

        fun updateSelectedPokemonExtraInfo(nature: String, ability: String)

        fun showCreatePokemon()

        fun showEditGoal()

        fun showLoading()

        fun provideDirectItems(chances: List<CombinationHolder>, flags: Int)

        fun provideImprovementItems(improvements: List<CombinationHolder>)

        fun runLayoutAnimation()

    }



    interface Presenter: BoxContract.Presenter {

        fun setAssistantView(assistantView: AssistantView)

        fun startAssistant()

        fun requestChancesUpdate()

        fun getPokemonName(externalId: Int): String

        fun getPokemonIconId(externalId: Int): Int

        fun getNatureName(natureId: Int): String

        fun getAbilityName(pokemonId: Int, abilitySlot: Int): String

        fun storeNewPokemon()

        fun editGoal()
    }
}
