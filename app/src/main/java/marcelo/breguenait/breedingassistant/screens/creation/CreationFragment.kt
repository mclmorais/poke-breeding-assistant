package marcelo.breguenait.breedingassistant.screens.creation

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
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

    // TODO: Rename and change types of parameters
    private var mParam1: String? = null
    private var mParam2: String? = null

    private var listener: OnFragmentInteractionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            mParam1 = arguments!!.getString(ARG_PARAM1)
            mParam2 = arguments!!.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.creation_fragment, container, false)


        //Anchors the floating action buttons to the main card on the fragment
        val fabParams =
            (activity as CreationActivity).finish_fab.layoutParams as CoordinatorLayout.LayoutParams
        fabParams.anchorId = R.id.main_card

        val params =
            (activity as CreationActivity).gender_fab.layoutParams as CoordinatorLayout.LayoutParams
        params.anchorId = R.id.main_card

        return root
    }

    // TODO: Rename method, update argument and hook method into UI event
    fun onButtonPressed(uri: Uri) {

    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context!!.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        fun onFragmentInteraction(uri: Uri)
    }

    override fun onPokemonSelected(id: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onSelectorDismissed() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    companion object {
        // TODO: Rename parameter arguments, choose names that match
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private val ARG_PARAM1 = "param1"
        private val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
        //         * @param param1 Parameter 1.
        //         * @param param2 Parameter 2.
         * @return A new instance of fragment CreationFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(/*param1: String, param2: String*/): CreationFragment {
            val fragment = CreationFragment()
//            val args = Bundle()
//            args.putString(ARG_PARAM1, param1)
//            args.putString(ARG_PARAM2, param2)
//            fragment.arguments = args
            return fragment
        }
    }
}
