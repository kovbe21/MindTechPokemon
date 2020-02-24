package com.encosoft.fuger.ui.common

import android.util.Log
import android.view.View
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.fragment.FragmentNavigator
import androidx.transition.TransitionInflater
import hu.ppke.itk.mindtechpokemon.R

interface SharedTransition {
    var transitionExtras: FragmentNavigator.Extras?

    fun Fragment.setupTransition(sharedElements: List<Pair<View, String>>) {
        if (sharedElementEnterTransition == null || sharedElementReturnTransition == null) {
            val transition = TransitionInflater.from(requireContext()).inflateTransition(R.transition.change_transition)
            sharedElementEnterTransition = transition
            sharedElementReturnTransition = transition
        }
        val extrasBuilder = FragmentNavigator.Extras.Builder()
        sharedElements.forEach { element ->
            ViewCompat.setTransitionName(element.first, element.second)
            extrasBuilder.addSharedElement(element.first, element.second)
        }
        lifecycle.addObserver(TransitionObserver())
        transitionExtras = extrasBuilder.build()
    }

    class TransitionObserver : DefaultLifecycleObserver {
        override fun onStop(owner: LifecycleOwner) {
            (owner as? SharedTransition)?.transitionExtras = null
            Log.v("Transition", "clearTransition called for: $owner")
            owner.lifecycle.removeObserver(this)
        }
    }

    companion object {
        // Constants to match transitions
        const val APP_LOGO = "APP_LOGO"
    }
}


