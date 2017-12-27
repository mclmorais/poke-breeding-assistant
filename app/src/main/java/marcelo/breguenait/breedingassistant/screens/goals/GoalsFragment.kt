package marcelo.breguenait.breedingassistant.screens.goals


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.goals_activity.*

import marcelo.breguenait.breedingassistant.R


class GoalsFragment : Fragment(), GoalsContract.View {

    private lateinit var presenter: GoalsContract.Presenter

    companion object {
        fun newInstance(): GoalsFragment = GoalsFragment()

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.goals_fragment, container, false)

        //Recovers a reference to the presenter injected in the activity
        presenter = (activity as GoalsActivity).goalsPresenter

        //Links this view to the presenter
        presenter.setGoalsView(this)


        (activity as GoalsActivity).add_goal_fab.setOnClickListener { presenter.addNewGoal() }

        return root
    }
}
