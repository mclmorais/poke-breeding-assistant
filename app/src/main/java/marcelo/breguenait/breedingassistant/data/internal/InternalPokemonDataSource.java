package marcelo.breguenait.breedingassistant.data.internal;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class InternalPokemonDataSource {


    private final static String STORED_KEY = "json_stored_list";
    private final static String GOAL_KEY = "json_goal_list";
    private final static String CURRENT_GOAL = "current_goal";

    private final static int INTERNAL_POKEMON_VERSION = 1;

    private SharedPreferences mSharedPreferences;
    private Gson mGson;

    public InternalPokemonDataSource(SharedPreferences sharedPreferences, Gson gson) {
        this.mSharedPreferences = sharedPreferences;
        this.mGson = gson;
    }


    public String saveGoalsToDisk(ArrayList<InternalPokemon> internalPokemons) {
        String jsonString;
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();

        prefEditor.putInt("internal_pokemon_version", INTERNAL_POKEMON_VERSION);

        jsonString = mGson.toJson(internalPokemons);
        prefEditor.putString(GOAL_KEY, jsonString);
        prefEditor.apply();

        return jsonString;
    }


    public String saveStoredPokemonsToDisk(ArrayList<InternalPokemon> internalPokemons) {
        String jsonString;
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();

        prefEditor.putInt("internal_pokemon_version", INTERNAL_POKEMON_VERSION);
        jsonString = mGson.toJson(internalPokemons);
        prefEditor.putString(STORED_KEY, jsonString);
        prefEditor.apply();

        return jsonString;
    }


    public ArrayList<InternalPokemon> loadGoalsFromDisk3() {

        int version = mSharedPreferences.getInt("internal_pokemon_version", 0);
        ArrayList<InternalPokemon> internalPokemons = new ArrayList<>();

        if (version >= 1) {
            String jsonString = mSharedPreferences.getString(GOAL_KEY, null);
            if (jsonString != null) {
                Type type = new TypeToken<ArrayList<InternalPokemon>>() {
                }.getType();
                internalPokemons = mGson.fromJson(jsonString, type);
            }
        }
        return internalPokemons;
    }

    @NonNull
    public ArrayList<InternalPokemon> loadGoalsFromDisk() {


        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson customGson;
        Type structureType = new TypeToken<ArrayList<InternalPokemon>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(structureType, new JsonInternalPokemonDeserializer());
        customGson = gsonBuilder.create();

        String jsonString = mSharedPreferences.getString(GOAL_KEY, null);

        ArrayList<InternalPokemon> loaded = customGson.fromJson(jsonString, structureType);

        if(loaded == null) loaded = new ArrayList<>();

        return loaded;
    }


    @NonNull
    public ArrayList<InternalPokemon> loadStoredPokemonsFromDisk() {

        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson customGson;
        Type structureType = new TypeToken<ArrayList<InternalPokemon>>() {
        }.getType();
        gsonBuilder.registerTypeAdapter(structureType, new JsonInternalPokemonDeserializer());
        customGson = gsonBuilder.create();

        String jsonString = mSharedPreferences.getString(STORED_KEY, null);

        ArrayList<InternalPokemon> loaded = customGson.fromJson(jsonString, structureType);

        if(loaded == null) loaded = new ArrayList<>();

        return loaded;
    }


    public void saveCurrentGoal(String goal) {
        SharedPreferences.Editor prefEditor = mSharedPreferences.edit();
        prefEditor.putString(CURRENT_GOAL, goal);
        prefEditor.apply();
    }


    public String loadCurrentGoal() {
        return mSharedPreferences.getString(CURRENT_GOAL, null);
    }
}
