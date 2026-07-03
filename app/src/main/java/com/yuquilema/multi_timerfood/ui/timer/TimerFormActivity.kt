package com.yuquilema.multi_timerfood.ui.timer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.snackbar.Snackbar
import com.yuquilema.multi_timerfood.R
import com.yuquilema.multi_timerfood.data.entity.Timer
import com.yuquilema.multi_timerfood.databinding.ActivityTimerFormBinding

class TimerFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTimerFormBinding
    private val viewModel: TimerViewModel by viewModels {
        TimerViewModelFactory(application)
    }

    private var timerId: Int = -1
    private var currentTimer: Timer? = null
    private var selectedSound: String = "Default"

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Snackbar.make(
                binding.root,
                "Sin el permiso no recibirá avisos al terminar sus timers",
                Snackbar.LENGTH_LONG,
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTimerFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { finish() }

        timerId = intent.getIntExtra(EXTRA_TIMER_ID, -1)

        if (timerId != -1) {
            viewModel.getById(timerId) { timer: Timer? ->
                if (timer != null) {
                    currentTimer = timer
                    populateFields(timer)
                    if ("RUNNING" == timer.estado) {
                        setReadOnlyMode()
                    }
                }
            }
        }

        binding.btnGuardar.setOnClickListener {
            checkPermissionsAndSave()
        }
    }

    private fun populateFields(timer: Timer) {
        binding.etNombre.setText(timer.nombreAlimento)
        binding.etDuracion.setText(timer.duracionSegundos.toString())
        binding.etCategoria.setText(timer.categoria)
        binding.etNotas.setText(timer.notas)
        selectedSound = timer.sonido
    }

    private fun setReadOnlyMode() {
        binding.etNombre.isEnabled = false
        binding.etDuracion.isEnabled = false
        binding.etCategoria.isEnabled = false
        binding.etNotas.isEnabled = false
        binding.btnGuardar.isEnabled = false
        binding.tvReadOnlyWarning.visibility = View.VISIBLE
    }

    private fun checkPermissionsAndSave() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                return
            }
        }
        showSoundSelectorAndSave()
    }

    private fun showSoundSelectorAndSave() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_sound_selector, binding.root as? android.view.ViewGroup, false)
        val radioGroup = view.findViewById<RadioGroup>(R.id.rgSounds)
        
        val sounds = listOf("Default", "Alarm", "Bell", "Whistle")
        sounds.forEach { sound ->
            val rb = RadioButton(this).apply {
                text = sound
                id = View.generateViewId()
                tag = sound
            }
            radioGroup.addView(rb)
            if (sound == selectedSound) rb.isChecked = true
        }

        view.findViewById<View>(R.id.btnConfirmSound).setOnClickListener {
            val checkedId = radioGroup.checkedRadioButtonId
            val selectedRb = radioGroup.findViewById<RadioButton>(checkedId)
            selectedSound = (selectedRb?.tag as? String) ?: "Default"
            bottomSheetDialog.dismiss()
            saveTimer()
        }

        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun saveTimer() {
        val nombre = binding.etNombre.text.toString().trim()
        val duracionStr = binding.etDuracion.text.toString().trim()
        val categoria = binding.etCategoria.text.toString().trim()
        val notas = binding.etNotas.text.toString().trim()

        if (nombre.isEmpty() || duracionStr.isEmpty()) {
            Toast.makeText(this, "Nombre y duración son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val duracion = duracionStr.toIntOrNull() ?: 0
        if (duracion <= 0) {
            Toast.makeText(this, "Duración debe ser mayor a 0", Toast.LENGTH_SHORT).show()
            return
        }

        val timer = currentTimer ?: Timer()
        timer.nombreAlimento = nombre
        timer.duracionSegundos = duracion
        timer.categoria = categoria.ifBlank { "General" }
        timer.notas = notas
        timer.sonido = selectedSound

        if (timerId == -1) {
            viewModel.insert(timer)
        } else {
            viewModel.update(timer)
        }

        finish()
    }

    companion object {
        const val EXTRA_TIMER_ID = "extra_timer_id"
    }
}
