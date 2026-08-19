package com.example.comptegouttes

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TutorialActivity : AppCompatActivity() {

    private val titles = arrayOf(
        "Bienvenue",
        "Compteur manuel",
        "Générateur de fréquence",
        "Calculs automatiques",
        "Navigation",
        "Informations"
    )

    private val texts = arrayOf(
        "Ce tutoriel en 6 étapes vous explique tous les boutons de l'application. Touchez CONTINUER pour avancer, ou PASSER pour le quitter à tout moment.",
        "Écran Compteur : touchez APPUYER ICI en rythme. Le grand chiffre affiche la fréquence en appuis/min, même avec un seul appui (ex : 1 appui / 2 min). RÉINITIALISER remet tout à zéro.",
        "Écran Générateur : tapez une fréquence (événements/min), puis touchez ▶ : la barre bleue glisse de gauche à droite avec un tic sonore à chaque événement. ⬜ arrête tout.",
        "Écran Calculs : touchez un bouton pour ouvrir : Débit de perfusion, Débit de P.S.E. pour drogue vasoactive, Débit de transfusion, Gestogramme ou Score de Glasgow.",
        "Les 4 boutons en bas changent d'écran : Compteur, Générateur, Calculs, Info. Vous pouvez aussi glisser l'écran vers la gauche ou la droite. La flèche ← revient en arrière.",
        "L'écran Info décrit toutes les fonctionnalités et les coordonnées de l'auteur (téléphone, e-mail, Facebook, GitHub). Le tutoriel ne s'affichera qu'au premier lancement. Bon travail !"
    )

    private var step = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tutorial)

        val tvStep = findViewById<TextView>(R.id.tvStep)
        val tvTitle = findViewById<TextView>(R.id.tvTutoTitle)
        val tvText = findViewById<TextView>(R.id.tvTutoText)
        val btnSkip = findViewById<Button>(R.id.btnSkip)
        val btnNext = findViewById<Button>(R.id.btnNext)

        fun show() {
            tvStep.text = "Étape ${step + 1}/${titles.size}"
            tvTitle.text = titles[step]
            tvText.text = texts[step]
            btnNext.text = if (step == titles.size - 1) "Terminer" else "Continuer"
        }

        btnSkip.setOnClickListener { finish() }
        btnNext.setOnClickListener {
            if (step == titles.size - 1) finish() else { step++; show() }
        }

        show()
    }
}
