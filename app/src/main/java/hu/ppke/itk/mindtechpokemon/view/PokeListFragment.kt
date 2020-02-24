package hu.ppke.itk.mindtechpokemon.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import hu.ppke.itk.mindtechpokemon.PokeListAdapter
import hu.ppke.itk.mindtechpokemon.R
import hu.ppke.itk.mindtechpokemon.basic.Status
import hu.ppke.itk.mindtechpokemon.viewmodels.ListViewModel
import kotlinx.android.synthetic.main.fragment_list.*
import org.koin.android.viewmodel.ext.android.viewModel

class PokeListFragment : Fragment(R.layout.fragment_list) {


    private val model: ListViewModel by viewModel()

    private val TAG = "PokeListFragment"

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.setupNavigation(this)

        val adapter = PokeListAdapter(
            model::onClick,
            model::retry,
            model::callback
        )

        model.data.observe(viewLifecycleOwner, Observer {
            Log.v(TAG, "items: ${it.size}")
            adapter.submitList(it)
            if (adapter.itemCount > 0) {
                fragment_list_progress_bar?.visibility = View.GONE
            }

        })


        model.networkState.observe(viewLifecycleOwner, Observer {


            if (it != null && it.status.isCompleted) {
                fragment_list_swipe_refresh?.isRefreshing = false
                fragment_list_progress_bar?.visibility = View.GONE
            }

            if (it.status == Status.FAILED) {
                Toast.makeText(context,context?.getString(R.string.network_error),Toast.LENGTH_SHORT).show()
            }
        })

        fragment_list_swipe_refresh.setOnRefreshListener(model::refresh)


        fragment_list_recycler_view.adapter = adapter

    }

}
