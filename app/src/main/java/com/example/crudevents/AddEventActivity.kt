package com.example.crudevents

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.crudevents.events.Event
import com.example.crudevents.events.EventViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

/**
 * Tela de Formulário para Adicionar ou Editar um evento.
 */
class AddEventActivity : AppCompatActivity() {

    private val viewModel: EventViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_event)

        val nome = findViewById<EditText>(R.id.editNome)
        val data = findViewById<EditText>(R.id.editData)
        
        // Configuração do Seletor de Data (Calendário)
        data.setOnClickListener {
            val calendario = Calendar.getInstance()
            val dialog = DatePickerDialog(
                this,
                { _, ano, mes, dia ->
                    data.setText("%02d/%02d/%04d".format(dia, mes + 1, ano))
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            )
            dialog.show()
        }
        
        val local = findViewById<EditText>(R.id.editLocal)
        val publico = findViewById<EditText>(R.id.editPublico)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)

        // Verificamos se recebemos um ID por Intent. 
        // Se sim, significa que estamos EDITANDO, não criando.
        val eventId = intent.getIntExtra("EVENT_ID", -1)
        if (eventId != -1) {
            nome.setText(intent.getStringExtra("EVENT_NOME"))
            data.setText(intent.getStringExtra("EVENT_DATA"))
            local.setText(intent.getStringExtra("EVENT_LOCAL"))
            publico.setText(intent.getIntExtra("EVENT_PUBLICO", 0).toString())
            btnSalvar.text = "Atualizar" // Muda o texto do botão
        }

        btnSalvar.setOnClickListener {
            // Pegamos o texto da data e convertemos para o objeto LocalDate
            val dateStr = data.text.toString()
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val localDate = try {
                LocalDate.parse(dateStr, formatter)
            } catch (e: Exception) {
                LocalDate.now() // Se falhar, usa a data de hoje
            }

            // Criamos o objeto Event com os dados dos campos
            val event = Event(
                id = if (eventId != -1) eventId else 0, // Se for edição, mantém o ID original
                nome = nome.text.toString(),
                data = localDate,
                local = local.text.toString(),
                publicoEstimado = publico.text.toString().toIntOrNull() ?: 0
            )

            // Decide se deve Inserir Novo ou Atualizar Existente
            if (eventId != -1) {
                viewModel.update(event)
            } else {
                viewModel.insert(event)
            }
            
            finish() // Fecha esta tela e volta para a MainActivity
        }
    }
}
