package tgs.app.githubuser.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import tgs.app.githubuser.ui.DetailActivity
import tgs.app.githubuser.adapter.ListAdapter
import tgs.app.githubuser.databinding.FragmentFollowBinding
import tgs.app.githubuser.model.ItemsItem
import tgs.app.githubuser.viewmodel.MainViewModel

class FollowFragment : Fragment() {
    private lateinit var mainViewModel: MainViewModel

    private lateinit var _binding: FragmentFollowBinding
    private val binding get() = _binding

    private var username: String? = ""
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.fragmentRecyclerView.layoutManager = LinearLayoutManager(requireActivity())

        mainViewModel = ViewModelProvider(requireActivity(), ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]

        mainViewModel.isLoading.observe(viewLifecycleOwner) {
            showLoading(it)
        }

        arguments?.let {
            position = it.getInt(ARG_POSITION)
            username = it.getString(ARG_USERNAME)
        }
        if (position == 1){
            mainViewModel.listFollowers(username)
            mainViewModel.itemsItemFollowers.observe(viewLifecycleOwner) { itemsItem ->
                setItemsData(itemsItem)
            }
        } else {
            mainViewModel.listFollowing(username)
            mainViewModel.itemsItemFollowing.observe(viewLifecycleOwner) { itemsItem ->
                setItemsData(itemsItem)
            }
        }
    }

    private fun setItemsData(itemsItem: List<ItemsItem?>) {
        val adapter = ListAdapter(itemsItem)
        binding.fragmentRecyclerView.adapter = adapter
        adapter.setOnItemClickCallback(object : ListAdapter.OnItemClickCallback{
            override fun onItemClicked(data: ItemsItem?) {
                val intent = Intent(requireActivity(), DetailActivity::class.java)
                intent.putExtra(DetailActivity.DETAIL_USER, data)
                startActivity(intent)
            }
        })
    }

    private fun showLoading(isLoading: Boolean) {
        binding.fragmentProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        const val ARG_POSITION = "arg_position"
        const val ARG_USERNAME = "arg_username"
    }
}