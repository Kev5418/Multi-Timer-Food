package com.yuquilema.multi_timerfood.ui.timer

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.yuquilema.multi_timerfood.data.entity.Timer
import com.yuquilema.multi_timerfood.databinding.ItemTimerBinding
import java.util.Locale

class TimerAdapter(
    private val onItemClick: (Timer) -> Unit,
    private val onDeleteClick: (Timer) -> Unit,
) : ListAdapter<Timer, TimerAdapter.TimerViewHolder>(TimerDiffCallback()) {

    inner class TimerViewHolder(private val binding: ItemTimerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(timer: Timer) {
            binding.tvNombreAlimento.text = timer.nombreAlimento
            binding.tvCategoria.text = timer.categoria
            binding.tvDuracion.text = formatDuracion(timer.duracionSegundos)
            binding.tvEstado.text = timer.estado

            binding.root.setOnClickListener { onItemClick(timer) }
            binding.root.setOnLongClickListener {
                onDeleteClick(timer)
                true
            }
        }

        private fun formatDuracion(segundos: Int): String {
            val h = segundos / 3600
            val m = (segundos % 3600) / 60
            val s = segundos % 60
            return String.format(Locale.getDefault(), "%02d:%02d:%02d", h, m, s)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimerViewHolder {
        val binding = ItemTimerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TimerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TimerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

class TimerDiffCallback : DiffUtil.ItemCallback<Timer>() {
    override fun areItemsTheSame(oldItem: Timer, newItem: Timer) = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Timer, newItem: Timer) = oldItem == newItem
}
