package tgs.app.githubuser.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import tgs.app.githubuser.databinding.ItemListBinding
import tgs.app.githubuser.model.ItemsItem

class ListAdapter(private val listGithub: List<ItemsItem?>) :
    RecyclerView.Adapter<ListAdapter.ViewHolder>() {

    private lateinit var onItemClickCallback: OnItemClickCallback

    fun setOnItemClickCallback(onItemClickCallback: OnItemClickCallback) {
        this.onItemClickCallback = onItemClickCallback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.txtListUsername.text = listGithub[position]?.login
        Glide.with(holder.itemView.context).load(listGithub[position]?.avatarUrl)
            .into(holder.binding.imgListAvatar)
        holder.itemView.setOnClickListener {
            onItemClickCallback.onItemClicked(listGithub[holder.adapterPosition])
        }
    }

    override fun getItemCount() = listGithub.size

    class ViewHolder(val binding: ItemListBinding) : RecyclerView.ViewHolder(binding.root)

    interface OnItemClickCallback {
        fun onItemClicked(data: ItemsItem?)
    }
}