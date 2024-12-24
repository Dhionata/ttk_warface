package br.com.dhionata

import br.com.dhionata.Weapon.WeaponsLists.engenheiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.fuzileiroWeapons
import br.com.dhionata.Weapon.WeaponsLists.pistolas

fun main() {
    println("\n==== TTK no corpo ====")

    println("\n=== Classe Fuzileiro ===\n")
    fuzileiroWeapons.forEach {
        println(it)
    }

    println("\n=== Classe Engenheiro ===\n")
    engenheiroWeapons.forEach {
        println(it)
    }

    println("\n=== Pistolas ===\n")
    pistolas.forEach {
        println(it)
    }

    println("\n==== TTK na cabeça ====")

    println("\n=== Classe Fuzileiro ===\n")
    fuzileiroWeapons.sortedBy { it.ttk.values.first() }.forEach {
        println(it)
    }

    println("\n=== Classe Engenheiro ===\n")
    engenheiroWeapons.sortedBy { it.ttk.values.first() }.forEach {
        println(it)
    }

    println("\n=== Pistolas ===\n")
    pistolas.sortedBy { it.ttk.values.first() }.forEach {
        println(it)
    }

    println("\n==== Média do TTK por Arma ====")

    println("\n=== Classe Fuzileiro ===\n")
    fuzileiroWeapons.sortedBy { it.ttk.values.last() }.forEach {
        println(it)
    }

    println("\n=== Classe Engenheiro ===\n")
    engenheiroWeapons.sortedBy { it.ttk.values.last() }.forEach {
        println(it)
    }

    println("\n=== Pistolas ===\n")
    pistolas.sortedBy { it.ttk.values.last() }.forEach {
        println(it)
    }

    println("\n=== Melhores TTKs ===")

    println("\n=== Classe Fuzileiro ===\n")
    val ttkCalculator = TTKCalculator()
    ttkCalculator.findBestTTK(fuzileiroWeapons).toList().forEach { ttkCalculator.printWeaponTTKWithProtection(it) }

    println("\n=== Classe Engenheiro ===\n")
    ttkCalculator.findBestTTK(engenheiroWeapons).toList().forEach { ttkCalculator.printWeaponTTKWithProtection(it) }

    println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK na cabeça ====\n")
    (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk.values.first() }.forEach { println(it) }

    println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK no corpo ====\n")
    (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk.values.elementAt(1) }.forEach { println(it) }

    println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK Médio ====\n")
    (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk.values.last() }.forEach { println(it) }
}
