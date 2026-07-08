package com.yuquilema.multi_timerfood.ui.timer

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yuquilema.multi_timerfood.data.entity.Timer
import com.yuquilema.multi_timerfood.databinding.ActivityTimerListBinding
import com.yuquilema.multi_timerfood.databinding.DialogNuevoTimerBinding

class TimerListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerListBinding
    private lateinit var adapter: TimerAdapter
    private val viewModel: TimerViewModel by viewModels {
        TimerViewModelFactory(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = TimerAdapter(
            onItemClick = { _ -> /* abrir detalle/editar */ },
            onDeleteClick = { timer ->
                viewModel.delete(timer)
            },
        )
        binding.rvTimers.adapter = adapter
        binding.rvTimers.layoutManager = LinearLayoutManager(this)

        // Observa el LiveData: cada vez que Room cambia, esto se dispara solo
        viewModel.allTimers.observe(this) { lista ->
            adapter.submitList(lista)
        }

        // Muestra los errores de persistencia en vez de dejarlos pasar en silencio.
        viewModel.error.observe(this) { mensaje ->
            if (mensaje != null) {
                Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        binding.fabAgregar.setOnClickListener {
            mostrarFormularioNuevoTimer()
        }
    }

    private fun mostrarFormularioNuevoTimer() {
        val dialogBinding = DialogNuevoTimerBinding.inflate(layoutInflater)
        AlertDialog.Builder(this)
            .setTitle("Nuevo temporizador")
            .setView(dialogBinding.root)
            .setPositiveButton("Guardar") { _, _ ->
                val nombre = dialogBinding.etNombreAlimento.text.toString()
                val minutos = dialogBinding.etMinutos.text.toString().toIntOrNull() ?: 0
                val categoria = dialogBinding.etCategoria.text.toString()

                if ((nombre.isBlank() || (minutos <= 0))) {
                    Toast.makeText(this, "Completa nombre y duración", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val nuevoTimer = Timer(
                    nombre,
                    minutos * 60,
                    categoria.ifBlank { "General" }
                )
                viewModel.insert(nuevoTimer)
                // No hace falta refrescar la lista manualmente — LiveData lo hace solo
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
