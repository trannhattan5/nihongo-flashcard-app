package com.example.nihongoflashcardapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.nihongoflashcardapp.databinding.ItemLessonBinding
import com.example.nihongoflashcardapp.models.Lesson
import com.example.nihongoflashcardapp.repository.LessonProgressRepository

class LessonAdapter(
    private val lessons: List<Lesson>,
    private val onLearn: (Lesson) -> Unit,
    private val onReview: (Lesson) -> Unit,
    private val onPlay: (Lesson) -> Unit
) : RecyclerView.Adapter<LessonAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemLessonBinding)
        : RecyclerView.ViewHolder(binding.root) {

        fun bind(lesson: Lesson) {
            binding.txtLessonTitle.text = lesson.title

            LessonProgressRepository().loadLessonProgress(
                lesson.id,
                lesson.totalCards
            ) { remembered, notRemembered, notLearned ->
                binding.txtTotalCards.text =
                    "✔ $remembered  ✖ $notRemembered  ⏳ $notLearned"
            }

            binding.root.setOnClickListener { onLearn(lesson) }
            binding.btnReview.setOnClickListener { onReview(lesson) }
            binding.btnStart.setOnClickListener { onPlay(lesson) }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemLessonBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount() = lessons.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lessons[position])
    }
}
