package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

class ReposViewHolder(
    private val binding: FragmentRepoItemBinding,
    private val onEdit: (Repo) -> Unit, // nueva línea implementada
    private val onDelete: (Repo) -> Unit // nueva línea implementada
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(repo: Repo) {
        binding.repoName.text = repo.name
        binding.repoDescription.text = repo.description
        binding.repoLang.text = repo.language

        Glide.with(binding.root.context)
            .load(repo.owner.avatarUrl)
            .placeholder(R.mipmap.ic_launcher)
            .error(R.mipmap.ic_launcher)
            .circleCrop()
            .into(binding.repoOwnerImage)

        binding.repoItemEditButton.setOnClickListener { // nueva línea implementada
            onEdit(repo) // nueva línea implementada
        }

        binding.repoItemDeleteButton.setOnClickListener { // nueva línea implementada
            onDelete(repo) // nueva línea implementada
        }
    }
}

class ReposAdapter(
    private val onEditClicked: (Repo) -> Unit, // nueva línea implementada
    private val onDeleteClicked: (Repo) -> Unit // nueva línea implementada
) : RecyclerView.Adapter<ReposViewHolder>() {

    private var repositories: List<Repo> = emptyList()

    override fun getItemCount(): Int = repositories.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReposViewHolder {
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ReposViewHolder( // nueva línea implementada
            binding,
            onEditClicked,  // nueva línea implementada
            onDeleteClicked // nueva línea implementada
        )
    }

    override fun onBindViewHolder(holder: ReposViewHolder, position: Int) {
        holder.bind(repositories[position])
    }

    fun updateRepositories(newRepositories: List<Repo>) {
        repositories = newRepositories
        notifyDataSetChanged()
    }
}