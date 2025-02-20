package br.com.dhionata

import java.math.BigDecimal
import java.math.RoundingMode

object WeaponPresenter {

    fun printDetailedAllWeaponsInfo(fuzileiroWeapons: List<Weapon>, engenheiroWeapons: List<Weapon>, pistolas: List<Weapon>, set: Set, debug: Boolean = false) {
        println("\n=== Detalhes para o conjunto ===\n\n==== ${set.name} ====")

        println("\n==== TTK no corpo ====")

        println("\n=== Classe Fuzileiro ===\n")
        fuzileiroWeapons.sortedBy { it.ttk[1].second }.forEach { println(it) }

        println("\n=== Classe Engenheiro ===\n")
        engenheiroWeapons.sortedBy { it.ttk[1].second }.forEach { println(it) }

        println("\n=== Pistolas ===\n")
        pistolas.sortedBy { it.ttk[1].second }.forEach { println(it) }

        println("\n==== TTK na cabeça ====")

        println("\n=== Classe Fuzileiro ===\n")
        fuzileiroWeapons.sortedBy { it.ttk.first().second }.forEach { println(it) }

        println("\n=== Classe Engenheiro ===\n")
        engenheiroWeapons.sortedBy { it.ttk.first().second }.forEach { println(it) }

        println("\n=== Pistolas ===\n")
        pistolas.sortedBy { it.ttk.first().second }.forEach { println(it) }

        println("\n==== Média do TTK por Arma ====")

        println("\n=== Classe Fuzileiro ===\n")
        fuzileiroWeapons.sortedBy { it.ttk.last().second }.forEach { println(it) }

        println("\n=== Classe Engenheiro ===\n")
        engenheiroWeapons.sortedBy { it.ttk.last().second }.forEach { println(it) }

        println("\n=== Pistolas ===\n")
        pistolas.sortedBy { it.ttk.last().second }.forEach { println(it) }

        println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK na cabeça ====\n")
        (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk.first().second }.forEach { println(it) }

        println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK no corpo ====\n")
        (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk[1].second }.forEach { println(it) }

        println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK Médio ====\n")
        (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk.last().second }.forEach { println(it) }

        println("\n=== Melhores TTKs ===")

        println("\n=== Classe Fuzileiro ===\n")
        TTKCalculator.findBestTTK(fuzileiroWeapons, set, debug).toList().forEach { TTKCalculator.printWeaponTTKWithProtection(it, set) }

        println("\n=== Classe Engenheiro ===\n")
        TTKCalculator.findBestTTK(engenheiroWeapons, set, debug).toList().forEach { TTKCalculator.printWeaponTTKWithProtection(it, set) }

        println("\n==== Tempo médio de resistência contra Fuzi. + Eng. com o conjunto ${set.name}")

        println("\n=== Cabeça ===")

        println("\n== Tiros ==")
        val mediaDeTirosNaCabeca = (fuzileiroWeapons + engenheiroWeapons).map { it.ttk.first().first }.average()
        println(BigDecimal(mediaDeTirosNaCabeca).setScale(2, RoundingMode.HALF_UP))

        println("\n== Tempo ==")
        val tempoMedioDeResistenciaCabeca = (fuzileiroWeapons + engenheiroWeapons).map { it.ttk.first().second }.average()
        println(BigDecimal(tempoMedioDeResistenciaCabeca).setScale(2, RoundingMode.HALF_UP))

        println("\n=== Corpo ===")

        println("\n== Tiros ==")
        val mediaDeTirosNaCorpo = (fuzileiroWeapons + engenheiroWeapons).map { it.ttk[1].first }.average()
        println(BigDecimal(mediaDeTirosNaCorpo).setScale(2, RoundingMode.HALF_UP))

        println("\n== Tempo ==")
        val tempoMedioDeResistenciaCorpo = (fuzileiroWeapons + engenheiroWeapons).map { it.ttk[1].second }.average()
        println(BigDecimal(tempoMedioDeResistenciaCorpo).setScale(2, RoundingMode.HALF_UP))
    }
}
