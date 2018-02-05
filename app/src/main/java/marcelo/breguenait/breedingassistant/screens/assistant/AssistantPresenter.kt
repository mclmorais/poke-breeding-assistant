package marcelo.breguenait.breedingassistant.screens.assistant

import android.os.AsyncTask
import marcelo.breguenait.breedingassistant.data.external.ExternalRepository
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon
import marcelo.breguenait.breedingassistant.data.internal.InternalRepository
import marcelo.breguenait.breedingassistant.logic.AssistantAi
import marcelo.breguenait.breedingassistant.logic.CompatibilityChecker
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity
import marcelo.breguenait.breedingassistant.utils.CachedPokemonIcons
import javax.annotation.Resource
import javax.inject.Inject


class AssistantPresenter @Inject
constructor(private val internalRepository: InternalRepository,
            private val externalRepository: ExternalRepository,
            private val cachedPokemonIcons: CachedPokemonIcons,
            private val assistantAi: AssistantAi)
    : AssistantContract.Presenter {


    private val compatibilityChecker: CompatibilityChecker = CompatibilityChecker(externalRepository) //TODO: inject?

    @Resource
    private lateinit var assistantView: AssistantContract.AssistantView
    @Resource
    private lateinit var storageView: AssistantContract.StorageView

    override fun setAssistantView(assistantView: AssistantContract.AssistantView) {
        this.assistantView = assistantView
    }

    override fun setStorageView(storageView: AssistantContract.StorageView) {
        this.storageView = storageView
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

    override fun startStored() {

        if (!storageView.initialized()) {
            storageView.updateStoredPokemons(compatiblePokemons)
        }
    }

    override fun result(requestCode: Int, resultCode: Int) {
        if (CreationActivity.REQUEST_CREATE_STORED == requestCode && CreationActivity.SUCCESSFUL == resultCode) {
            storageView.updateStoredPokemons(compatiblePokemons)
            storageView.showSuccessfulStorage()
        } else if (requestCode == CreationActivity.REQUEST_EDIT_STORED && resultCode == CreationActivity.SUCCESSFUL) {
            storageView.updateStoredPokemons(compatiblePokemons)
        } else if (CreationActivity.REQUEST_EDIT_GOAL == requestCode && CreationActivity.SUCCESSFUL == resultCode) {
            storageView.updateStoredPokemons(compatiblePokemons)
        }
    }

    override fun storeNewPokemon() {
        assistantView.showCreatePokemon()
    }

    override fun editGoal() {
        assistantView.showEditGoal()
    }

    override fun getPokemonName(externalId: Int): String {
        return externalRepository.getExternalPokemon(externalId)!!.name
    }

    override fun getPokemonIconId(externalId: Int): Int {
        return cachedPokemonIcons.getIconId(externalId)
    }

    override fun requestChancesUpdate() {

        assistantView.showLoading()

        object : AsyncTask<Int, Int, Int>() {


            override fun onPreExecute() {
                super.onPreExecute()

            }

            override fun doInBackground(vararg params: Int?): Int? {

                assistantAi.calculateBestMatches(internalRepository.currentGoal ?: return 0)

                return 0
            }

            override fun onPostExecute(integer: Int) {
                super.onPostExecute(integer)

                assistantView.provideDirectItems(assistantAi.directCombinations, assistantAi.directFlag)

                assistantView.provideImprovementItems(assistantAi.improvementCombinations)
            }
        }.execute()
    }

    override fun getNatureName(natureId: Int): String {
        return externalRepository.getNature(natureId)!!.getName(9)
    }

    override fun getAbilityName(pokemonId: Int, abilitySlot: Int): String? {
        return externalRepository.getAbility(pokemonId, abilitySlot)?.name
    }

    override fun getCurrentGoal(): InternalPokemon? {
        return internalRepository.currentGoal
    }

    override fun removeStoredPokemons(stored: List<InternalPokemon>) {
        internalRepository.removeStoredPokemons(stored)
        storageView.updateStoredPokemons(compatiblePokemons)
        requestChancesUpdate()
    }
}
