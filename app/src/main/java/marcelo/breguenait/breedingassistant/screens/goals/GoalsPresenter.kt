package marcelo.breguenait.breedingassistant.screens.goals

import javax.inject.Inject


class GoalsPresenter @Inject
internal constructor() : GoalsContract.Presenter {

    private lateinit var goalsView: GoalsContract.View

    override fun setGoalsView(view: GoalsContract.View) {
        goalsView = view
    }

    override fun addNewGoal() {
        goalsView.showCreateGoal()
    }
}