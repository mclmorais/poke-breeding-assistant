package marcelo.breguenait.breedingassistant.screens.creation

import marcelo.breguenait.breedingassistant.data.external.datablocks.ExternalNature
import marcelo.breguenait.breedingassistant.utils.Genders
import java.util.ArrayList

/**
 * Created by Marcelo on 17/01/2018.
 */
interface CreationContract {

    interface View {
        fun showSelectPokemonFragment()

        fun updateSelectedPokemonName(name: String)

        fun updateSelectedPokemonIcon(iconId: Int)

        fun updateSelectedPokemonStats()

        fun updateSelectedPokemonStat(statId: Int)

        fun updateSelectedPokemonAbilities(abilities: Array<String?>)

        fun updateSelectedPokemonChosenAbilitySlot(slot: Int)

        fun updateSelectedPokemonChosenNature(natureId: Int)

        fun updateSelectedPokemonChosenIVs(IVs: IntArray)

        fun updateGenderRestrictions(restriction: Int)

        fun updateSelectedPokemonChosenGender(@Genders.GendersFlag gender: Int)

       // val editIdRequest: String

        val selectedIVs: IntArray

        @get:Genders.GendersFlag val selectedGender: Int

        val selectedNatureId: Int

        val selectedAbilitySlot: Int

        fun exitActivity(createdId: String?)

    }

    interface Presenter {

        fun start()

        val currentSelectionId: Int

        val natures: ArrayList<ExternalNature>

        fun getStatName(statId: Int): String

        fun selectPokemon()

        fun updateSelection(id: Int)

        fun setView(view: View)

        fun getNextStat(statId: Int): Int

        fun notifyStatChanged(statId: Int)

        fun finishCreation()
    }
}