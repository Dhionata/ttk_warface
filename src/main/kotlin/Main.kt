package br.com.dhionata

import br.com.dhionata.Weapon.WeaponsLists.engenheiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.fuzileiroWeapons

fun main() {
    println("\n=== Classe Fuzileiro ===\n")
    fuzileiroWeapons.forEach {
        println(it.toString())
    }

    println("\n=== Classe Engenheiro ===\n")
    engenheiroWeapons.forEach {
        println(it.toString())
    }

    println("\n==== TTK na cabeça ====\n")

    println("\n=== Classe Fuzileiro ===\n")
    fuzileiroWeapons.sortedBy { it.ttk.values.first() }.forEach {
        println(it.toString())
    }

    println("\n=== Classe Engenheiro ===\n")
    engenheiroWeapons.sortedBy { it.ttk.values.first() }.forEach {
        println(it.toString())
    }

    println("\n=== Classe Fuzileiro ===\n")
    val weaponCalculator = WeaponCalculator()
    weaponCalculator.findBestTTK(fuzileiroWeapons)

    println("\n=== Classe Engenheiro ===\n")
    weaponCalculator.findBestTTK(engenheiroWeapons)
}
