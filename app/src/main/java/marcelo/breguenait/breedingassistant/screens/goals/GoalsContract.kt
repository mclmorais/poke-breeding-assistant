package marcelo.breguenait.breedingassistant.screens.goals

interface GoalsContract {

    interface View {

        fun showCreateGoal()

    }

    interface Presenter {
        fun setGoalsView(view: View)

        fun addNewGoal()
    }

}
