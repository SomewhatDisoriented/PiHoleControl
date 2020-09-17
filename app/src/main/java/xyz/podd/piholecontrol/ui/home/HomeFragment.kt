package xyz.podd.piholecontrol.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import xyz.podd.piholecontrol.R

class HomeFragment : Fragment() {

    private lateinit var viewModel: HomeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this, HomeViewModelFactory(requireContext()))
            .get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        viewModel.devices.observe(viewLifecycleOwner) {
            val transaction = parentFragmentManager.beginTransaction()

            // Remove old
            for (fragment in parentFragmentManager.fragments) {
                if (fragment is DeviceFragment) {
                    transaction.remove(fragment)
                }
            }

            // Add new
            for (device in it) {
                transaction.add(R.id.layout_devices, DeviceFragment.newInstance(device))
            }

            transaction.commit()
        }

        return root
    }

    override fun onResume() {
        super.onResume()

        viewModel.refresh()
    }
}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory(private val context: Context): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T = HomeViewModel(context) as T
}