package marcelo.breguenait.breedingassistant.logic;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import marcelo.breguenait.breedingassistant.data.internal.InternalPokemon;
import marcelo.breguenait.breedingassistant.utils.Sequence;


public class CombinationHolder {

    private final int id;

    private Couple couple;

    @Nullable
    private List<Child> children;

    private double combinedChildrenChance = 0;

    private CombinationProblems combinationProblems;

    CombinationHolder(@NonNull Couple couple) {
        this.couple = couple;
        this.id = Sequence.nextValue();
    }

    public CombinationHolder() {
        this.id = Sequence.nextValue();
    }

    @NonNull
    public Couple getCouple() {
        return couple;
    }

    @Nullable
    public List<Child> getChildren() {
        return children;
    }

    void setChildren(@Nullable List<Child> children, int multiplier) {
        this.children = children;

        if (children != null) {
            for (Child child : this.children) {
                combinedChildrenChance += child.getWeightedChance();
            }
        }
        combinedChildrenChance /= ((double) multiplier);
    }

    void setCompatibleParent(InternalPokemon compatible) {
        couple.setCompatible(compatible);
    }

    void setCombinedChildrenChance(double combinedChildrenChance) {
        this.combinedChildrenChance = combinedChildrenChance;
    }

    public double getChance() {
        return combinedChildrenChance;
    }

    public int getId() {
        return id;
    }

    public CombinationProblems getCombinationProblems() {
        return combinationProblems;
    }

    public void setCombinationProblems(CombinationProblems combinationProblems) {
        this.combinationProblems = combinationProblems;
    }


}
