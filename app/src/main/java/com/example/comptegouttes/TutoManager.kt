package com.example.comptegouttes

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView

object TutoManager {

    var active = false
    private var stepIndex = 0
    private val shownViews = mutableListOf<View>()

    data class Step(val screen: String, val targetId: Int?, val text: String, val goTo: String? = null)

    private val steps = listOf(
        Step("counter", R.id.btnTap, "1/10 — Voici le bouton APPUYER ICI : touchez-le en rythme pour compter. Le résultat s'affiche toujours (ex : 1 appui /min, 1 appui / 2 min)."),
        Step("counter", R.id.btnReset, "2/10 — RÉINITIALISER remet le compteur et le chronomètre à zéro."),
        Step("counter", R.id.btnNavGenerator, "3/10 — Ce bouton ouvre le Générateur de fréquence. Touchez CONTINUER : l'application y va toute seule.", "generator"),
        Step("generator", R.id.etFreq, "4/10 — Tapez ici la fréquence voulue, par exemple 60 événements/min."),
        Step("generator", R.id.btnPlay, "5/10 — ▶ démarre : la barre bleue glisse de gauche à droite avec un tic sonore à chaque événement."),
        Step("generator", R.id.btnStop, "6/10 — ⬜ arrête le générateur et fige le chronomètre."),
        Step("generator", R.id.btnNavCalc, "7/10 — Ce bouton ouvre les Calculs automatiques. Touchez CONTINUER pour y aller.", "calc"),
        Step("calc", R.id.calcList, "8/10 — Voici la liste des calculs : Débit de perfusion, Débit de P.S.E., Débit de transfusion, Gestogramme, Score de Glasgow. Touchez un bouton pour ouvrir."),
        Step("calc", R.id.btnNavInfo, "9/10 — Ce bouton ouvre les Informations. Touchez CONTINUER pour y aller.", "info"),
        Step("info", null, "10/10 — Ici : toutes les fonctionnalités et les contacts cliquables de l'auteur (téléphone, e-mail, Facebook, GitHub). Tutoriel terminé !")
    )

    fun begin() {
        active = true
        stepIndex = 0
    }

    fun attach(activity: Activity, screen: String) {
        if (!active) return
        if (stepIndex >= steps.size) { active = false; return }
        if (steps[stepIndex].screen != screen) return
        activity.window.decorView.post { show(activity) }
    }

    private fun show(activity: Activity) {
        clearViews()
        val step = steps[stepIndex]

        val target = step.targetId?.let { activity.findViewById<View>(it) }
        val spot = SpotlightView(activity, target)
        spot.isClickable = true
        addView(activity, spot, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT))

        val card = LinearLayout(activity)
        card.orientation = LinearLayout.VERTICAL
        card.setBackgroundResource(R.drawable.bg_neon_card)
        card.setPadding(dp(activity, 16), dp(activity, 14), dp(activity, 16), dp(activity, 14))

        val tv = TextView(activity)
        tv.text = step.text
        tv.setTextColor(Color.WHITE)
        tv.textSize = 15f
        card.addView(tv)

        val row = LinearLayout(activity)
        row.orientation = LinearLayout.HORIZONTAL
        row.setPadding(0, dp(activity, 12), 0, 0)

        val btnSkip = Button(activity)
        btnSkip.text = "Passer"
        btnSkip.setTextColor(Color.parseColor("#C7C9E4"))
        btnSkip.setBackgroundResource(R.drawable.bg_chip_unselected)
        btnSkip.setOnClickListener {
            active = false
            clearViews()
        }

        val btnNext = Button(activity)
        btnNext.text = if (stepIndex == steps.size - 1) "Terminer" else "Continuer"
        btnNext.setTextColor(Color.WHITE)
        btnNext.setBackgroundResource(R.drawable.bg_button_gradient)
        btnNext.setOnClickListener {
            val goTo = step.goTo
            stepIndex++
            if (stepIndex >= steps.size) {
                active = false
                clearViews()
                return@setOnClickListener
            }
            if (goTo != null) {
                clearViews()
                val targetAct = when (goTo) {
                    "generator" -> GeneratorActivity::class.java
                    "calc" -> CalcActivity::class.java
                    else -> InfoActivity::class.java
                }
                activity.startActivity(Intent(activity, targetAct))
            } else {
                show(activity)
            }
        }

        val pSkip = LinearLayout.LayoutParams(0, dp(activity, 46), 1f)
        pSkip.marginEnd = dp(activity, 10)
        row.addView(btnSkip, pSkip)
        row.addView(btnNext, LinearLayout.LayoutParams(0, dp(activity, 46), 1f))
        card.addView(row)

        val cardParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT)
        cardParams.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        cardParams.leftMargin = dp(activity, 16)
        cardParams.rightMargin = dp(activity, 16)
        cardParams.bottomMargin = dp(activity, 96)
        addView(activity, card, cardParams)
    }

    private fun addView(activity: Activity, v: View, params: FrameLayout.LayoutParams) {
        activity.addContentView(v, params)
        shownViews.add(v)
    }

    private fun clearViews() {
        for (v in shownViews) (v.parent as? ViewGroup)?.removeView(v)
        shownViews.clear()
    }

    private fun dp(a: Activity, v: Int) = (v * a.resources.displayMetrics.density).toInt()
}
