package tgs.app.githubuser.ui

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import tgs.app.githubuser.R
import tgs.app.githubuser.adapter.SectionsPagerAdapter
import tgs.app.githubuser.database.FavoriteUser
import tgs.app.githubuser.databinding.ActivityDetailBinding
import tgs.app.githubuser.model.DetailUserResponse
import tgs.app.githubuser.model.ItemsItem
import tgs.app.githubuser.ui.insert.FavoriteUserAddUpdateViewModel
import tgs.app.githubuser.viewmodel.FavoriteViewModel
import tgs.app.githubuser.viewmodel.MainViewModel
import tgs.app.githubuser.viewmodel.ViewModelFactory

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sectionsPagerAdapter: SectionsPagerAdapter
    private lateinit var favoriteUserAddUpdateViewModel: FavoriteUserAddUpdateViewModel
    private lateinit var favoriteViewModel: FavoriteViewModel
    private lateinit var favoriteUser: FavoriteUser

    private var data: ItemsItem? = null
    private var isFavorite = false

    private var menuFavorite: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sectionsPagerAdapter = SectionsPagerAdapter(this)
        val viewPager: ViewPager2 = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter
        val tabs: TabLayout = findViewById(R.id.tabs)
        TabLayoutMediator(tabs, viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        data = intent.parcelable(DETAIL_USER)
        favoriteUser = FavoriteUser()

        favoriteUserAddUpdateViewModel = obtainViewModel(this@DetailActivity)
        favoriteViewModel = obtainFavoriteViewModel(this@DetailActivity)

        if (data != null) {
            data.let { data ->
                favoriteUser.username = data?.login.toString()
                favoriteUser.avatarUrl = data?.avatarUrl
            }

            favoriteViewModel.getFavoriteUserByUsername(data?.login.toString()).observe(this) {favoriteList ->
                if (favoriteList != null){
                    isFavorite = true
                } else {
                    isFavorite = false
                }
            }
        }

        sectionsPagerAdapter.username = data?.login
        mainViewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory())[MainViewModel::class.java]
        mainViewModel.detailUser(data?.login)
        mainViewModel.detailUserResponse.observe(this) { detailUser ->
            setItemsData(detailUser)
        }
        mainViewModel.isLoading.observe(this) {
            showLoading(it)
        }

        supportActionBar?.title = data?.login
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private inline fun <reified T : Parcelable> Intent.parcelable(key: String): T? = when {
        SDK_INT >= 33 -> getParcelableExtra(key, T::class.java)
        else -> @Suppress("DEPRECATION") getParcelableExtra(key) as? T
    }

    private fun obtainViewModel(activity: AppCompatActivity): FavoriteUserAddUpdateViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavoriteUserAddUpdateViewModel::class.java]
    }

    private fun obtainFavoriteViewModel(activity: AppCompatActivity): FavoriteViewModel {
        val factory = ViewModelFactory.getInstance(activity.application)
        return ViewModelProvider(activity, factory)[FavoriteViewModel::class.java]
    }

    private fun setItemsData(detailUser: DetailUserResponse?) {
        binding.detailName.text = detailUser?.name
        binding.detailLogin.text = detailUser?.login
        binding.detailFollowers.text = getString(R.string.followers, detailUser?.followers)
        binding.detailFollowing.text = getString(R.string.following, detailUser?.following)
        Glide.with(this).load(detailUser?.avatarUrl).into(binding.detailImgProfile)
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.detailProgressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        menuFavorite = menu
        if (isFavorite) {
            menuFavorite?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite)
        } else {
            menuFavorite?.getItem(0)?.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_border)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.favorite_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.favorite -> {
                if (!isFavorite) {
                    favoriteUserAddUpdateViewModel.insert(favoriteUser)
                    showToast(getString(R.string.added_to_favorite))

                    item.setIcon(R.drawable.ic_favorite)
                    isFavorite = true
                } else {
                    favoriteUserAddUpdateViewModel.delete(favoriteUser)
                    showToast(getString(R.string.deleted_from_favorite))

                    item.setIcon(R.drawable.ic_favorite_border)
                    isFavorite = false
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        const val DETAIL_USER = "detail_user"

        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.content_tab_followers,
            R.string.content_tab_following
        )
    }
}