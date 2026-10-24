package marcelo.breguenait.breedingassistant.screens.assistant

import android.os.Handler
import android.os.Looper
import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.data.internal.InternalRepository
import marcelo.breguenait.breedingassistant.logic.AssistantAi
import marcelo.breguenait.breedingassistant.logic.CompatibilityChecker
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity
import marcelo.breguenait.breedingassistant.utils.CachedPokemonIcons
import java.util.concurrent.Executors
import javax.inject.Inject


class AssistantPresenter @Inject
constructor(private val internalRepository: InternalRepository,
            private val externalRepository: ExternalRepository,
            private val cachedPokemonIcons: CachedPokemonIcons,
            private val assistantAi: AssistantAi)
    : AssistantContract.Presenter {


    private val compatibilityChecker: CompatibilityChecker = CompatibilityChecker(externalRepository) //TODO: inject?

    private lateinit var assistantView: AssistantContract.AssistantView
    private lateinit var boxView: BoxContract.BoxView

    override fun setAssistantView(assistantView: AssistantContract.AssistantView) {
        this.assistantView = assistantView
    }

    override fun setStorageView(boxView: BoxContract.BoxView) {
        this.boxView = boxView
    }


    private val compatiblePokemons: List<InternalPokemon>
        get() = compatibilityChecker.getCompatibleStoredPokemon(internalRepository.getStoredPokemons(), internalRepository.currentGoal!!)


    override fun startAssistant() {
        /*Requests which goal will be handled by the assistant*/
        val currentGoal = internalRepository.currentGoal

        /*Updates the View with the selected goal information*/
        val externalId = currentGoal!!.externalId
        assistantView.updateSelectedPokemonIcon(cachedPokemonIcons.getIconId(externalId))
        assistantView.updateSelectedPokemonName(externalRepository.getExternalPokemon(externalId)!!.name)
        assistantView.updateSelectedPokemonIVs(currentGoal.IVs)

        val natureName = externalRepository.getNature(currentGoal.natureId).getName(9)
        val abilityName = externalRepository.getAbility(
            currentGoal.externalId, currentGoal.abilitySlot)!!
            .name

        assistantView.updateSelectedPokemonExtraInfo(natureName, abilityName)


        requestChancesUpdate()
    }

    override fun initBox() {
        if (!boxView.initialized()) {
            boxView.updateStoredPokemons(compatiblePokemons)
        }
    }

    override fun result(requestCode: Int, resultCode: Int) {
        if (CreationActivity.REQUEST_CREATE_STORED == requestCode && CreationActivity.SUCCESSFUL == resultCode) {
            boxView.updateStoredPokemons(compatiblePokemons)
            boxView.showSuccessfulStorage()
        } else if (requestCode == CreationActivity.REQUEST_EDIT_STORED && resultCode == CreationActivity.SUCCESSFUL) {
            boxView.updateStoredPokemons(compatiblePokemons)
        } else if (CreationActivity.REQUEST_EDIT_GOAL == requestCode && CreationActivity.SUCCESSFUL == resultCode) {
            boxView.updateStoredPokemons(compatiblePokemons)
        }
    }

    override fun storeNewPokemon() {
        assistantView.showCreatePokemon()
        requestChancesUpdate()
    }

    override fun editGoal() {
        assistantView.showEditGoal()
        requestChancesUpdate()
    }

    override fun getPokemonName(externalId: Int): String {
        return externalRepository.getExternalPokemon(externalId)!!.name
    }

    override fun getPokemonIconId(externalId: Int): Int {
        return cachedPokemonIcons.getIconId(externalId)
    }

    override fun requestChancesUpdate() {
        assistantView.showLoading()
        calculateBestMatches()
    }

    override fun getNatureName(natureId: Int): String {
        return externalRepository.getNature(natureId).getName(9)
    }

    override fun getAbilityName(pokemonId: Int, abilitySlot: Int): String {
        return externalRepository.getAbility(pokemonId, abilitySlot)?.name ?: ""
    }

    override val currentGoal = internalRepository.currentGoal


    override fun removeStoredPokemons(stored: List<InternalPokemon>) {
        internalRepository.removeStoredPokemons(stored)
        boxView.updateStoredPokemons(compatiblePokemons)
        requestChancesUpdate()
    }

    private fun calculateBestMatches() {
        val view = assistantView
        calculations.execute {
            internalRepository.currentGoal?.let {
                assistantAi.calculateBestMatches(it)
                Thread.sleep(500) //TODO: Switch to a more elegant solution to always delay at least 500ms before giving results
            }
            mainThread.post {
                view.provideDirectItems(assistantAi.directCombinations, assistantAi.directFlag)
                view.provideImprovementItems(assistantAi.improvementCombinations)
                view.runLayoutAnimation()
            }
        }
    }

    companion object {
        private val calculations = Executors.newSingleThreadExecutor()
        private val mainThread = Handler(Looper.getMainLooper())
    }

}
