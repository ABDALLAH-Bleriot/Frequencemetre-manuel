package com.example.comptegouttes

import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CalcDetailActivity : AppCompatActivity() {

    private val edits = mutableMapOf<String, EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calc_detail)

        val id = intent.getStringExtra("calc_id") ?: ""
        val def = CalcRegistry.byId(id)

        findViewById<TextView>(R.id.tvCalcTitle).text = def?.title ?: "Calcul"

        val container = findViewById<LinearLayout>(R.id.fieldsContainer)

        def?.fields?.forEach { f ->
            val et = EditText(this)
            et.hint = f.hint
            if (f.default.isNotEmpty()) et.setText(f.default)
            et.setBackgroundResource(R.drawable.bg_input)
            et.setTextColor(-0x1)
            et.setHintTextColor(-0x7F785A)
            et.setPadding(dp(14), dp(14), dp(14), dp(14))
            et.inputType = if (f.numeric)
                InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            else
                InputType.TYPE_CLASS_TEXT

            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, dp(10), 0, 0)
            container.addView(et, params)

            edits[f.id] = et
        }

        findViewById<Button>(R.id.btnCompute).setOnClickListener {
            val values = edits.mapValues { it.value.text.toString() }
            findViewById<TextView>(R.id.tvCalcResult).text =
                def?.compute?.invoke(values) ?: "—"
        }
    }

    private fun dp(v: Int) = (v * resources.displayMetrics.density).toInt()
}
