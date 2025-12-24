package com.example.nihongoflashcardapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nihongoflashcardapp.databinding.ItemLevelBinding
import com.example.nihongoflashcardapp.models.Level

class LevelAdapter(
    private val levels: List<Level>,
    private val onClick: (Level) -> Unit
) : RecyclerView.Adapter<LevelAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLevelBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(level: Level) {
            binding.txtLevelName.text = level.name
            binding.txtLevelDesc.text = level.description
            binding.txtLevelFullTitle.text = level.description
            binding.root.setOnClickListener {
                onClick(level)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLevelBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = levels.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(levels[position])
    }
}
