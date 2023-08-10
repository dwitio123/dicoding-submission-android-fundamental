package tgs.app.githubuser.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import tgs.app.githubuser.adapter.ListAdapter
import tgs.app.githubuser.databinding.ActivityFavoriteBinding
import tgs.app.githubuser.model.ItemsItem
import tgs.app.githubuser.viewmodel.FavoriteViewModel
import tgs.app.githubuser.viewmodel.ViewModelFactory

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding
    private lateinit var favoriteViewModel: FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.favoriteRecyclerView.layoutManager = LinearLayoutManager(this)

        favoriteViewModel = obtainFavoriteViewModel(this@FavoriteActivity)
        favoriteViewModel.isLoading.observe(this) {
            showLoading(it)
        }
        favoriteViewModel.getAllFavoriteUser().observe(this) { users ->
            val items = arrayListOf<ItemsItem>()
            users.map {
                val item = ItemsItem(login = it.username, avatarUrl = it.avatarUrl)
                items.add(item)
            }
            val adapter = ListAdapter(items)
            binding.favoriteRecyclerView.adapter = adapter
            adapter.setOnItemClickCallback(object : ListAdapter.OnItemClickCallback {
                override fun onItemClicked(data: ItemsItem?) {
                    val intent = Intent(applicationContext, DetailActivity::class.java)
                    intent.putExtra(DetailActivity.DETAIL_USER, data)
                    startActivity(intent)
                }
            })
            showLoading(false)
        }

        supportActionBar?.title = "Favorites"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun obtainFavoriteViewModel(activity: AppCompatActivity): FavoriteViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavoriteViewModel::class.java]
    }

    private fun showLoading(isLoading: Boolean) {
        binding.favoriteProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}