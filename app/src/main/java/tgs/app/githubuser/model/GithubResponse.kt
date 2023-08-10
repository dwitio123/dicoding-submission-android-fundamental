package tgs.app.githubuser.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class GithubResponse(
	@field:SerializedName("items")
	val items: List<ItemsItem?>
)

@Parcelize
data class ItemsItem(
	@field:SerializedName("login")
	val login: String? = null,

	@field:SerializedName("avatar_url")
	val avatarUrl: String? = null
) : Parcelable
