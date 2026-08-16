package com.example.comptegouttes

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class FieldDef(
    val id: String,
    val hint: String,
    val default: String = "",
    val numeric: Boolean = true
)

data class CalcDef(
    val id: String,
    val title: String,
    val fields: List<FieldDef>,
    val compute: (Map<String, String>) -> String,
    val customActivity: Class<*>? = null
)

object CalcRegistry {

    val all: List<CalcDef> = listOf(

        CalcDef(
            id = "perfusion",
            title = "Débit de perfusion",
            fields = listOf(
                FieldDef("volume", "Volume à perfuser (mL)"),
                FieldDef("duree", "Durée (heures)"),
                FieldDef("facteur", "Facteur gouttes (gtt/mL)", "20")
            ),
            compute = { v ->
                val vol = num(v["volume"])
                val dur = num(v["duree"])
                val fac = num(v["facteur"])
                if (vol == null || dur == null || fac == null || dur <= 0.0 || fac <= 0.0)
                    "Entrez des valeurs valides (> 0)."
                else {
                    val gtt = vol * fac / (dur * 60.0)
                    val mlh = vol / dur
                    String.format(Locale.FRANCE, "≈ %.1f gtt/min (min⁻¹)\n≈ %.1f mL/h", gtt, mlh)
                }
            },
            customActivity = PerfusionActivity::class.java
        ),

        CalcDef(
            id = "pse",
            title = "Débit de P.S.E. pour drogue vasoactive",
            fields = listOf(
                FieldDef("dose", "Dose prescrite (µg/kg/min)"),
                FieldDef("poids", "Poids du patient (kg)"),
                FieldDef("mg", "Médicament dans la seringue (mg)"),
                FieldDef("vol", "Volume total de la seringue (mL)")
            ),
            compute = { v ->
                val dose = num(v["dose"])
                val poids = num(v["poids"])
                val mg = num(v["mg"])
                val vol = num(v["vol"])
                if (dose == null || poids == null || mg == null || vol == null || mg <= 0.0 || vol <= 0.0)
                    "Entrez des valeurs valides (> 0)."
                else {
                    val ugMin = dose * poids
                    val mgH = ugMin * 60.0 / 1000.0
                    val mlh = mgH / (mg / vol)
                    String.format(Locale.FRANCE, "≈ %.2f mL/h\n(%.1f µg/min soit %.3f mg/h)", mlh, ugMin, mgH)
                }
            },
            customActivity = PseActivity::class.java
        ),

        CalcDef(
            id = "gestogramme",
            title = "Gestogramme",
            fields = listOf(
                FieldDef("ddr", "1er jour des dernières règles (JJ/MM/AAAA)", numeric = false)
            ),
            compute = { v ->
                val s = v["ddr"]?.trim() ?: ""
                val fmt = SimpleDateFormat("dd/MM/yyyy", Locale.FRANCE)
                fmt.isLenient = false
                val ddr = try { fmt.parse(s) } catch (e: Exception) { null }
                if (ddr == null) {
                    "Date invalide. Format : JJ/MM/AAAA."
                } else {
                    val days = ((Date().time - ddr.time) / 86400000L).toInt()
                    when {
                        days < 0 -> "La date est dans le futur."
                        days > 44 * 7 -> "Date trop ancienne pour un gestogramme."
                        else -> {
                            val terme = Date(ddr.time + 280L * 86400000L)
                            String.format(
                                Locale.FRANCE,
                                "Âge gestationnel : %d SA + %d jour(s)\nTerme estimé : %s",
                                days / 7, days % 7, fmt.format(terme)
                            )
                        }
                    }
                }
            },
            customActivity = GestogrammeActivity::class.java
        )
    )

    fun byId(id: String): CalcDef? = all.find { it.id == id }

    private fun num(s: String?): Double? =
        s?.replace(',', '.')?.trim()?.toDoubleOrNull()
}
