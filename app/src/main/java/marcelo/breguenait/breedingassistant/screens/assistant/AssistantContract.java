package marcelo.breguenait.breedingassistant.screens.assistant;

import java.util.List;

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon;
import marcelo.breguenait.breedingassistant.logic.CombinationHolder;

/**
 * Created by Marcelo on 12/02/2017.
 */

public interface AssistantContract {

    interface AssistantView {

        /*Goal information update*/
        void updateSelectedPokemonName(String name);

        void updateSelectedPokemonIcon(int iconId);

        void updateSelectedPokemonIVs(int[] IVs);

        void updateSelectedPokemonExtraInfo(String nature, String ability);

        void showCreatePokemon();

        void showEditGoal();

        void showLoading();

        void provideDirectItems(List<CombinationHolder> chances, int flags);

        void provideImprovementItems(List<CombinationHolder> improvements);

    }

    interface StorageView {

        boolean initialized();

        void updateStoredPokemons(List<InternalPokemon> storedPokemons);

        void showSuccessfulStorage();
    }

    interface Presenter {

        void setAssistantView(AssistantView assistantView);

        void setStorageView(StorageView storageView);

        void startAssistant();

        void startStored();

        void result(int requestCode, int resultCode);

        void requestChancesUpdate();

        String getPokemonName(int externalId);

        int getPokemonIconId(int externalId);

        String getNatureName(int natureId);

        String getAbilityName(int pokemonId, int abilitySlot);

        void storeNewPokemon();

        void editGoal();

        InternalPokemon getCurrentGoal();

        void removeStoredPokemons(List<InternalPokemon> stored);

    }
}
