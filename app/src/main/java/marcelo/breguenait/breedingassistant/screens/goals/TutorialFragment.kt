package marcelo.breguenait.breedingassistant.screens.goals


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import marcelo.breguenait.breedingassistant.R


class TutorialFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val root: View =  inflater.inflate(R.layout.tutorial_fragment, container, false)

        return root
    }



    companion object {
        val TAG: String = TutorialFragment::class.java.simpleName

        fun newInstance(): TutorialFragment {
            return TutorialFragment()
        }
    }

}
