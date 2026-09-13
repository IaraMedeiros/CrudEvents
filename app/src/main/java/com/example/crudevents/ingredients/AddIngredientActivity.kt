package com.example.crudevents.ingredients

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.crudevents.R

class AddIngredientActivity : AppCompatActivity() {

    private val viewModel: IngredientViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_ingredient)

        val toolbar = findViewById<com.google.android.material.appbar.MaterialToolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        val editNome = findViewById<EditText>(R.id.editNome)
        val editQtd = findViewById<EditText>(R.id.editQuantidade)
        val editUnidade = findViewById<EditText>(R.id.editUnidade)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)

        val id = intent.getIntExtra("ID", -1)
        if (id != -1) {
            editNome.setText(intent.getStringExtra("NOME"))
            editQtd.setText(intent.getDoubleExtra("QTD", 0.0).toString())
            editUnidade.setText(intent.getStringExtra("UNIDADE"))
            btnSalvar.text = "Atualizar"
        }

        btnSalvar.setOnClickListener {
            val ingredient = Ingredient(
                id = if (id != -1) id else 0,
                nome = editNome.text.toString(),
                quantidadeEstoque = editQtd.text.toString().toDoubleOrNull() ?: 0.0,
                unidade = editUnidade.text.toString()
            )

            if (id != -1) viewModel.update(ingredient)
            else viewModel.insert(ingredient)

            finish()
        }

        findViewById<Button>(R.id.btnVoltar).setOnClickListener {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}
