package com.example.comptegouttes

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
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

    data class Step(
        val screen: String,
        val targetId: Int?,
        val text: String,
        val goTo: String? = null,
        val centerCard: Boolean = false
    )

    private val steps = listOf(
        Step("counter", R.id.btnTap, "Appuyez ici en rythme à chaque fois que vous effectuez une action. L'application comptera vos appuis par minute."),
        Step("counter", R.id.btnReset, "RÉINITIALISER remet le compteur et le chronomètre à zéro."),
        Step("counter", R.id.btnNavGenerator, "Ce bouton ouvre le Générateur de fréquence. Touchez SUIVANT : l'application y va toute seule.", "generator"),
        Step("generator", R.id.etFreq, "Tapez ici la fréquence voulue, par exemple 60 événements/min."),
        Step("generator", R.id.btnPlay, "▶ démarre : la barre bleue glisse de gauche à droite avec un tic sonore à chaque événement."),
        Step("generator", R.id.btnStop, "⬜ arrête le générateur et fige le chronomètre."),
        Step("generator", R.id.btnNavCalc, "Ce bouton ouvre les Calculs automatiques. Touchez SUIVANT pour y aller.", "calc"),
        Step("calc", R.id.calcList, "Voici la liste des calculs : Débit de perfusion, Débit de P.S.E. pour drogue vasoactive, Débit de transfusion, Gestogramme et Score de Glasgow. Touchez un bouton pour ouvrir.", null, true),
        Step("calc", R.id.btnNavInfo, "Ce bouton ouvre les Informations. Touchez SUIVANT pour y aller.", "info"),
        Step("info", null, "Ici : toutes les fonctionnalités et les contacts de l'auteur (téléphone, e-mail, Facebook, GitHub). Tutoriel terminé !")
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

        // Voile + pointillés blancs autour du bouton
        val spot = SpotlightView(activity, target)
        spot.isClickable = true
        addView(activity, spot, FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))

        // Carte BLANCHE (style de la photo)
        val card = LinearLayout(activity)
        card.orientation = LinearLayout.VERTICAL
        card.setBackgroundResource(R.drawable.bg_tuto_card)
        card.setPadding(dp(activity, 20), dp(activity, 18), dp(activity, 14), dp(activity, 10))
        card.elevation = dp(activity, 10).toFloat()

        val tv = TextView(activity)
        tv.text = step.text
        tv.setTextColor(Color.parseColor("#555555"))
        tv.textSize = 16f
        tv.setLineSpacing(dp(activity, 4).toFloat(), 1f)   // <-- ligne corrigée
        card.addView(tv)

        val row = LinearLayout(activity)
        row.orientation = LinearLayout.HORIZONTAL
        row.gravity = Gravity.CENTER_VERTICAL
        row.setPadding(0, dp(activity, 14), 0, 0)

        val tvCount = TextView(activity)
        tvCount.text = "${stepIndex + 1}/${steps.size}"
        tvCount.setTextColor(Color.parseColor("#2196F3"))
        tvCount.textSize = 15f
        tvCount.typeface = Typeface.DEFAULT_BOLD
        row.addView(tvCount)

        val spacer = View(activity)
        row.addView(spacer, LinearLayout.LayoutParams(0, 0, 1f))

        val btnSkip = Button(activity)
        btnSkip.text = "PASSER"
        btnSkip.setTextColor(Color.parseColor("#777777"))
        btnSkip.setBackgroundColor(Color.TRANSPARENT)
        btnSkip.textSize = 14f
        btnSkip.setOnClickListener {
            active = false
            clearViews()
        }
        row.addView(btnSkip, LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, dp(activity, 44)))

        val btnNext = Button(activity)
        btnNext.text = if (stepIndex == steps.size - 1) "TERMINÉ" else "SUIVANT"
        btnNext.setTextColor(Color.WHITE)
        btnNext.setBackgroundResource(R.drawable.bg_tuto_next)
        btnNext.textSize = 14f
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
        val pNext = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, dp(activity, 44))
        pNext.marginStart = dp(activity, 6)
        row.addView(btnNext, pNext)

        card.addView(row)

        // Placement : centrée si demandé (étape 8/10) ou sans cible,
        // sinon au-dessus / en dessous du bouton sans jamais le cacher
        val content = activity.findViewById<View>(android.R.id.content)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.leftMargin = dp(activity, 24)
        params.rightMargin = dp(activity, 24)

        if (target == null || step.centerCard) {
            params.gravity = Gravity.CENTER
        } else {
            val tLoc = IntArray(2)
            target.getLocationOnScreen(tLoc)
            val cLoc = IntArray(2)
            content.getLocationOnScreen(cLoc)
            val relTop = tLoc[1] - cLoc[1]
            val relBottom = relTop + target.height

            if (relBottom > content.height * 0.55) {
                params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                params.bottomMargin = content.height - relTop + dp(activity, 12)
            } else {
                params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                params.topMargin = relBottom + dp(activity, 12)
            }
        }
        addView(activity, card, params)
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
