package marcelo.breguenait.breedingassistant.screens.assistant;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.util.DiffUtil;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import marcelo.breguenait.breedingassistant.R;
import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon;
import marcelo.breguenait.breedingassistant.screens.creation.CreationActivity;
import marcelo.breguenait.breedingassistant.utils.Genders;
import marcelo.breguenait.breedingassistant.utils.Utility;

public class StoredPokemonFragment extends Fragment implements AssistantContract.StorageView {

    @Inject
    AssistantContract.Presenter presenter;

    FloatingActionButton addPokemonFab;

    RecyclerView recyclerView;

    private GridLayoutManager gridLayoutManager;

    private LinearLayoutManager linearLayoutManager;

    private int viewTypeId = 1;

    private StoredPokemonsAdapter storedPokemonsAdapter;

    @Nullable
    private ActionMode selectionMode;

    ActionMode.Callback selectionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            getActivity().getMenuInflater().inflate(R.menu.menu_assistant_storage_contextual, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            int counter = storedPokemonsAdapter.getSelectedItemsCount();
            String selectedText;
            if (counter == 1)
                selectedText = getString(R.string.selected_title_singular);
            else
                selectedText = getString(R.string.selected_title_plural);


            String title = String.format(selectedText, counter);

            mode.setTitle(title);
            return true;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case android.R.id.home:
                    mode.finish();
                    return true;
                case R.id.menu_delete_selected:
                    storedPokemonsAdapter.removeSelected();
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            storedPokemonsAdapter.deselectAll();
            selectionMode = null;
        }
    };


    public StoredPokemonFragment() {
    }

    @SuppressWarnings("unused")
    public static StoredPokemonFragment newInstance(int columnCount) {
        StoredPokemonFragment fragment = new StoredPokemonFragment();
        return fragment;
    }

    public void setPresenter(AssistantContract.Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.assistant_stored_fragment, container, false);

        presenter = ((AssistantActivity) getActivity()).getAssistantPresenter();
        presenter.setStorageView(this);

        storedPokemonsAdapter = new StoredPokemonsAdapter();


        int numberOfColumns = Utility.calculateNumberOfColumns(getContext(), (int) getResources().getDimension(R.dimen.item_stored_width));
        gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView = (RecyclerView) view.findViewById(R.id.stored_pokemon_list);
        recyclerView.setAdapter(storedPokemonsAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);

        addPokemonFab = (FloatingActionButton) getActivity().findViewById(R.id.fab_add);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.startStored();
    }

    public int getViewTypeId() {
        return viewTypeId;
    }


    @Override
    public boolean initialized() {
        return !storedPokemonsAdapter.storedPokemons.isEmpty();
    }

    @Override
    public void updateStoredPokemons(List<InternalPokemon> compatiblePokemons) {
        storedPokemonsAdapter.updateItems(compatiblePokemons);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        presenter.result(requestCode, resultCode);
    }

    @Override
    public void showSuccessfulStorage() {
        Snackbar.make(addPokemonFab, "Pokémon Stored!",
                Snackbar.LENGTH_LONG).show();
    }


    void showEditStored(InternalPokemon internalPokemon) {
        Intent intent = new Intent(getContext(), CreationActivity.class);
        intent.putExtra(CreationActivity.TYPE_ID, CreationActivity.STORED);
        intent.putExtra(CreationActivity.FILTER_ID, internalPokemon.getExternalId());
        intent.putExtra(CreationActivity.EXISTANT_ID, internalPokemon.getInternalId());
        startActivityForResult(intent, CreationActivity.REQUEST_EDIT_STORED);
    }

    class StoredPokemonsAdapter extends RecyclerView.Adapter<StoredPokemonsAdapter.ViewHolder> {

        List<InternalPokemon> storedPokemons = new ArrayList<>();

        private final HashSet<Integer> selectionsList = new HashSet<>();

        void updateItems(List<InternalPokemon> newPokemons) {

            final CompatibleDiff diff = new CompatibleDiff(storedPokemons, newPokemons);
            final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diff);

            storedPokemons.clear();
            storedPokemons.addAll(newPokemons);

            diffResult.dispatchUpdatesTo(this);

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);

            View inflatedView = inflater.inflate(R.layout.assistant_item_stored_small, parent, false);

            return new ViewHolder(inflatedView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            int externalId = storedPokemons.get(position).getExternalId();

            InternalPokemon goal = presenter.getCurrentGoal();

            Drawable icon = Utility.getIcon(externalId, getContext());
            holder.viewIcon.setImageDrawable(icon);

            @Genders.GendersFlag
            int gender = storedPokemons.get(position).getGender();


            if (selectionsList.contains(position)) {
                Drawable drawable = holder.viewIcon.getBackground();
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(getContext(), R.color
                        .colorPrimaryLight));
            } else {
                Drawable drawable = holder.viewIcon.getBackground();
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable.mutate(), ContextCompat.getColor(getContext(), R.color.colorPrimary));
            }

            int genderColor;

            switch (gender) {
                case Genders.MALE:
                    genderColor = ContextCompat.getColor(getContext(), R.color.male);

                    break;
                case Genders.FEMALE:
                    genderColor = ContextCompat.getColor(getContext(), R.color.female);

                    break;
                case Genders.GENDERLESS:
                    genderColor = ContextCompat.getColor(getContext(), R.color.genderless);

                    break;
                case Genders.DITTO:
                    genderColor = ContextCompat.getColor(getContext(), R.color.ditto);

                    break;
                default:
                    genderColor = ContextCompat.getColor(getContext(), android.R.color.black);

                    break;
            }

            Drawable genderDrawable = holder.viewGender.getDrawable();
            genderDrawable = DrawableCompat.wrap(genderDrawable);
            DrawableCompat.setTint(genderDrawable.mutate(), genderColor);


            int activeColor = ContextCompat.getColor(getContext(), R.color.iv_enabled_light);
            int inactiveColor = ContextCompat.getColor(getContext(), R.color.iv_disabled_light);

            int activeIndicatorColor = ContextCompat.getColor(getContext(), R.color.colorPrimaryLight);
            int inactiveIndicatorColor = ContextCompat.getColor(getContext(), R.color.colorDisabled);


            Drawable abilityBackground = DrawableCompat.wrap(holder.abilityIndicator.getBackground()).mutate();
            if (storedPokemons.get(position).getGender() != Genders.DITTO) {
                if (storedPokemons.get(position).getAbilitySlot() == goal.getAbilitySlot())
                    DrawableCompat.setTint(abilityBackground, activeIndicatorColor);
                else
                    DrawableCompat.setTint(abilityBackground, inactiveIndicatorColor);
            } else
                DrawableCompat.setTint(abilityBackground, inactiveIndicatorColor);

            Drawable natureBackground = DrawableCompat.wrap(holder.natureIndicator.getBackground()).mutate();
            if (storedPokemons.get(position).getNatureId() == goal.getNatureId())
                DrawableCompat.setTint(natureBackground, activeIndicatorColor);
            else
                DrawableCompat.setTint(natureBackground, inactiveIndicatorColor);


            for (int i = 0; i < holder.viewIVs.length; i++) {
                //TODO: remove from here! Colors should be decided elsewhere

                Drawable drawable = holder.viewIVs[i].getDrawable();
                Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
                wrappedDrawable = wrappedDrawable.mutate();
                if (storedPokemons.get(position).getIVs()[i] > 0)
                    DrawableCompat.setTint(wrappedDrawable, activeColor);
                else
                    DrawableCompat.setTint(wrappedDrawable, inactiveColor);
            }
        }

        @Override
        public int getItemViewType(int position) {
            return getViewTypeId();
        }

        @Override
        public int getItemCount() {
            return storedPokemons.size();
        }

        int getSelectedItemsCount() {
            return selectionsList.size();
        }

        void setSelection(int position) {
            if (selectionsList.contains(position)) {
                selectionsList.remove(position);
                if (selectionsList.isEmpty() && selectionMode != null)
                    selectionMode.finish();
            } else {
                selectionsList.add(position);
            }

            if (selectionMode != null)
                selectionMode.invalidate();

            notifyItemChanged(position);
        }

        void deselectAll() {
            if (!selectionsList.isEmpty()) {

                HashSet<Integer> tempList = new HashSet<>(selectionsList);

                selectionsList.clear();

                if (!tempList.isEmpty()) {
                    for (int i : tempList) {
                        notifyItemChanged(i);
                    }
                }

            }
        }

        void removeSelected() {
            List<InternalPokemon> goalsToBeRemoved = new ArrayList<>(selectionsList.size());
            for (int selection : selectionsList) {
                goalsToBeRemoved.add(storedPokemons.get(selection));
            }
            presenter.removeStoredPokemons(goalsToBeRemoved);

            if (selectionMode != null)
                selectionMode.finish();
        }


        class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {

            final ImageView[] viewIVs = new ImageView[6];

            final ImageView viewIcon;

            final ImageView viewGender;

            final TextView abilityIndicator, natureIndicator;

            ViewHolder(View itemView) {
                super(itemView);
                this.viewIcon = (ImageView) itemView.findViewById(R.id.stored_icon);
                viewIVs[0] = (ImageView) itemView.findViewById(R.id.stored_iv_hp);
                viewIVs[1] = (ImageView) itemView.findViewById(R.id.stored_iv_atk);
                viewIVs[2] = (ImageView) itemView.findViewById(R.id.stored_iv_def);
                viewIVs[3] = (ImageView) itemView.findViewById(R.id.stored_iv_satk);
                viewIVs[4] = (ImageView) itemView.findViewById(R.id.stored_iv_sdef);
                viewIVs[5] = (ImageView) itemView.findViewById(R.id.stored_iv_spd);
                viewGender = (ImageView) itemView.findViewById(R.id.stored_gender);
                abilityIndicator = (TextView) itemView.findViewById(R.id.stored_ability_indicator);
                natureIndicator = (TextView) itemView.findViewById(R.id.stored_nature_indicator);

                itemView.setOnClickListener(this);
                itemView.setOnLongClickListener(this);
            }

            @Override
            public void onClick(View v) {
                if (selectionMode == null) {
                    showEditStored(storedPokemons.get(getAdapterPosition()));
                } else {
                    setSelection(getAdapterPosition());
                }
            }

            @Override
            public boolean onLongClick(View v) {
                if (selectionMode == null)
                    selectionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(selectionCallback);

                setSelection(getAdapterPosition());

                return true;
            }


        }

        class CompatibleDiff extends DiffUtil.Callback {

            private final List<InternalPokemon> oldList, newList;

            CompatibleDiff(List<InternalPokemon> oldList, List<InternalPokemon> newList) {
                this.oldList = oldList;
                this.newList = newList;
            }

            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                InternalPokemon oldItem = oldList.get(oldItemPosition);
                InternalPokemon newItem = newList.get(newItemPosition);

                return oldItem.getInternalId().equals(newItem.getInternalId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {

                return false;

//                InternalPokemon oldItem = oldList.get(oldItemPosition);
//                InternalPokemon newItem = newList.get(newItemPosition);
//
//                return oldItem.getExternalId() == newItem.getExternalId() &&
//                        oldItem.getGender() == newItem.getGender() &&
//                        oldItem.getIVs() == newItem.getIVs() &&
//                        oldItem.getNatureId() == newItem.getNatureId() &&
//                        oldItem.getAbilitySlot() == newItem.getAbilitySlot();

            }
        }

    }


}
