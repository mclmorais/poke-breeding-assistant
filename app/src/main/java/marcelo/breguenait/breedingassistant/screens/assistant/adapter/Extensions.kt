package marcelo.breguenait.breedingassistant.screens.assistant.adapter

import android.support.annotation.LayoutRes
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * Created by Marcelo on 07/02/2018.
 */
fun ViewGroup.inflate(@LayoutRes layoutRes: Int, attachToRoot: Boolean = false): View {
    return LayoutInflater.from(context).inflate(layoutRes, this, attachToRoot)
}

fun <T> ArrayList<T>.removeInCase(condition: (T) -> Boolean) {

    this.removeAll(this.filter(condition))
}
