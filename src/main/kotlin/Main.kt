package br.com.dhionata

import br.com.dhionata.Weapon.WeaponsLists.engenheiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.fuzileiroWeapons

fun main() {
    println("\n==== TTK no corpo ====\n")

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

    println("\n==== Média do TTK por Arma ====\n")

    println("\n=== Classe Fuzileiro ===\n")
    fuzileiroWeapons.sortedBy { it.ttk.values.average() }.forEach {
        println("$it TTK Médio: ${it.ttk.values.average()}")
    }

    println("\n=== Classe Engenheiro ===\n")
    engenheiroWeapons.sortedBy { it.ttk.values.average() }.forEach {
        println("$it TTK Médio: ${it.ttk.values.average()}")
    }

    println("\n=== Classe Fuzileiro ===\n")
    val ttkCalculator = TTKCalculator()
    ttkCalculator.findBestTTK(fuzileiroWeapons).toList().forEach { ttkCalculator.printWeaponTTKWithProtection(it!!) }

    println("\n=== Classe Engenheiro ===\n")
    ttkCalculator.findBestTTK(engenheiroWeapons).toList().forEach { ttkCalculator.printWeaponTTKWithProtection(it!!) }

    println("\n=== Fuzileiro + Engenheiro ===\n==== TTK na cabeça ====\n")
    (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk.values.first() }.forEach { println(it.toString()) }

    println("\n=== Fuzileiro + Engenheiro ===\n==== TTK no corpo ====\n")
    (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk.values.last() }.forEach { println(it.toString()) }
}
