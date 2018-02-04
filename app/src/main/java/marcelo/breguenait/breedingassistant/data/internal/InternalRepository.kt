package marcelo.breguenait.breedingassistant.data.internal

import android.support.annotation.IntDef
import java.util.*

class InternalRepository(private val mDataSource: InternalPokemonDataSource) {

    private var currentGoalPokemon: String? = null

    private val goalPokemons = LinkedHashMap<String, InternalPokemon>()

    private val storedPokemons = LinkedHashMap<String, InternalPokemon>()

    init {
        val goalList = mDataSource.loadGoalsFromDisk()
        for (internalPokemon in goalList) {
            goalPokemons.put(internalPokemon.internalId, internalPokemon)
        }
        val storedList = mDataSource.loadStoredPokemonsFromDisk()
        for (internalPokemon in storedList) {
            storedPokemons.put(internalPokemon.internalId, internalPokemon)
        }

        currentGoalPokemon = mDataSource.loadCurrentGoal()
    }

    fun addInternalPokemon(internalPokemon: InternalPokemon, @InternalPokemonFlag flag: Int) {
        if (flag == INTERNAL_GOAL)
            goalPokemons.put(internalPokemon.internalId, internalPokemon)
        else if (flag == INTERNAL_STORED)
            storedPokemons.put(internalPokemon.internalId, internalPokemon)

        saveInternalPokemons() //TODO: is side effect here desirable?
    }


    fun saveInternalPokemons() {
        mDataSource.saveGoalsToDisk(ArrayList(goalPokemons.values))
        mDataSource.saveStoredPokemonsToDisk(ArrayList(storedPokemons.values))
    }

    fun saveInternalPokemonsWithDebugInfo(): String {
        val goalString = mDataSource.saveGoalsToDisk(ArrayList(goalPokemons.values))
        val storedString = mDataSource.saveStoredPokemonsToDisk(ArrayList(storedPokemons.values))

        return "GOALS-----------------------\n$goalString \n\n STORED----------------------\n$storedString"
    }

    fun getStoredPokemon(id: String): InternalPokemon? {
        return storedPokemons[id]
    }

    val goals: ArrayList<InternalPokemon>
        get() = ArrayList(goalPokemons.values)

    fun removeGoalPokemons(goals: List<InternalPokemon>) {
        for (goalId in goals) {
            goalPokemons.remove(goalId.internalId)
        }
        saveInternalPokemons() //TODO: is side effect here desirable?
    }

    fun removeStoredPokemons(stored: List<InternalPokemon>) {
        for (storedId in stored) {
            storedPokemons.remove(storedId.internalId)
        }
        saveInternalPokemons()
    }

    fun getStoredPokemons(): ArrayList<InternalPokemon> {
        return ArrayList(storedPokemons.values)
    }

    val currentGoal: InternalPokemon?
        get() {
            if (goalPokemons[currentGoalPokemon] != null)
                return goalPokemons[currentGoalPokemon]
            else
                return ArrayList(goalPokemons.values)[0]
        }

    fun setCurrentGoalPokemon(currentGoalPokemon: String) {
        this.currentGoalPokemon = currentGoalPokemon
        mDataSource.saveCurrentGoal(currentGoal?.internalId)
    }


    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    @IntDef(INTERNAL_GOAL.toLong(), INTERNAL_STORED.toLong())
    internal annotation class InternalPokemonFlag
    companion object {

        const val INTERNAL_GOAL = 1
        const val INTERNAL_STORED = 2
    }


}
