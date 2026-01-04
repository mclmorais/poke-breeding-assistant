package marcelo.breguenait.breedingassistant.extensions

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction

fun FragmentManager.inTransaction(body: FragmentTransaction.() -> Unit) {
    beginTransaction().apply {
        body()
        commit()
    }

}
    fun FragmentManager.inAnimatedTransaction(body: FragmentTransaction.() -> Unit) {
        beginTransaction().apply {
            setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
            body()
            commit()
        }
    }
