package marcelo.breguenait.breedingassistant.screens.goals


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import marcelo.breguenait.breedingassistant.R


class GoalsFragment : Fragment(), GoalsContract.View {

    private lateinit var presenter: GoalsContract.Presenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.goals_fragment, container, false)

        //presenter = (activity as GoalsActivity).goalsPresenter

        return root
    }

}
