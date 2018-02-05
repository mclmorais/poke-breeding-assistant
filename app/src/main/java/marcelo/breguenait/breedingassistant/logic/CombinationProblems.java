package marcelo.breguenait.breedingassistant.logic;

public class CombinationProblems {

    private boolean noMatchingNature;
    private boolean noMatchingAbility;
    private int[] missingIVs;


    public boolean hasNoMatchingNature() {
        return noMatchingNature;
    }

    void setNoMatchingNature(boolean noMatchingNature) {
        this.noMatchingNature = noMatchingNature;
    }

    public boolean hasNoMatchingAbility() {
        return noMatchingAbility;
    }

    void setNoMatchingAbility(boolean noMatchingAbility) {
        this.noMatchingAbility = noMatchingAbility;
    }

    public int[] getMissingIVs() {
        return missingIVs;
    }

    void setMissingIVs(int[] missingIVs) {
        this.missingIVs = missingIVs;
    }
}
