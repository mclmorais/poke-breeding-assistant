package marcelo.breguenait.breedingassistant.screens.creation

import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.text.Selection
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.creation_activity.*

import marcelo.breguenait.breedingassistant.R
import marcelo.breguenait.breedingassistant.screens.selection.SelectionDialogFragment

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [CreationFragment.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [CreationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreationFragment : Fragment(), CreationContract.View,
    SelectionDialogFragment.PokemonSelectionListener {


    private lateinit var presenter: CreationContract.Presenter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.creation_fragment, container, false)


        presenter = (activity as CreationActivity).creationPresenter
        presenter.setView(this)

        //Anchors the floating action buttons to the main card on the fragment
        val fabParams =
            (activity as CreationActivity).finish_fab.layoutParams as CoordinatorLayout.LayoutParams
        fabParams.anchorId = R.id.main_card

        val params =
            (activity as CreationActivity).gender_fab.layoutParams as CoordinatorLayout.LayoutParams
        params.anchorId = R.id.main_card

        return root
    }

    override fun onResume() {
        super.onResume()
        presenter.selectPokemon()
    }

    override fun onPokemonSelected(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSelectorDismissed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun showSelectPokemonFragment() {

        val selectionFragment = SelectionDialogFragment()
        selectionFragment.setTargetFragment(this, 300)
        selectionFragment.show(fragmentManager, "fragment_edit_name")

    }

    companion object {
        fun newInstance(/*param1: String, param2: String*/): CreationFragment {
            return CreationFragment()
        }
    }
}
