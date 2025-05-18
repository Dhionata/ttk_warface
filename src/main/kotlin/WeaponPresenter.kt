package br.com.dhionata

import java.math.BigDecimal
import java.math.RoundingMode

object WeaponPresenter {

    fun printDetailedAllWeaponsInfo(fuzileiroWeapons: List<Weapon>, engenheiroWeapons: List<Weapon>, pistolas: List<Weapon>, debug: Boolean = false) {
        println("\n=== Detalhes para o conjunto ===\n\n==== ${fuzileiroWeapons.first().set.name} ====")

        println("\n==== TTK no corpo ====")
        val bodyComparator = compareBy<Weapon> { it.ttk[1].second }.thenBy { it.ttk.first().second }.thenBy { it.ttk.last().second }

        println("\n=== Classe Fuzileiro ===\n")
        fuzileiroWeapons.sortedWith(bodyComparator).forEach { println(it) }

        println("\n=== Classe Engenheiro ===\n")
        engenheiroWeapons.sortedWith(bodyComparator).forEach { println(it) }

        println("\n=== Pistolas ===\n")
        pistolas.sortedWith(bodyComparator).forEach { println(it) }

        println("\n==== TTK na cabeça ====")
        val headComparator = compareBy<Weapon> { it.ttk.first().second }.thenBy { it.ttk[1].second }.thenBy { it.ttk.last().second }

        println("\n=== Classe Fuzileiro ===\n")
        fuzileiroWeapons.sortedWith(headComparator).forEach { println(it) }

        println("\n=== Classe Engenheiro ===\n")
        engenheiroWeapons.sortedWith(headComparator).forEach { println(it) }

        println("\n=== Pistolas ===\n")
        pistolas.sortedWith(headComparator).forEach { println(it) }

        println("\n==== Média do TTK por Arma ====")
        val averageComparator = compareBy<Weapon> { it.ttk.last().second }.thenBy { it.ttk.first().second }.thenBy { it.ttk[1].second }

        println("\n=== Classe Fuzileiro ===\n")
        fuzileiroWeapons.sortedWith(averageComparator).forEach { println(it) }

        println("\n=== Classe Engenheiro ===\n")
        engenheiroWeapons.sortedWith(averageComparator).forEach { println(it) }

        println("\n=== Pistolas ===\n")
        pistolas.sortedWith(averageComparator).forEach { println(it) }

        println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK na cabeça ====\n")
        (fuzileiroWeapons + engenheiroWeapons).sortedWith(headComparator).forEach { println(it) }

        println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK no corpo ====\n")
        (fuzileiroWeapons + engenheiroWeapons).sortedWith(bodyComparator).forEach { println(it) }

        println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK Médio ====\n")
        (fuzileiroWeapons + engenheiroWeapons).sortedWith(averageComparator).forEach { println(it) }

        println("\n=== Melhores TTKs ===")

        println("\n=== Classe Fuzileiro ===\n")
        println("Contra Conjunto: ${fuzileiroWeapons.first().set.name}")
        TTKCalculator.findBestTTK(fuzileiroWeapons, debug).toList().forEach { TTKCalculator.printWeaponTTKWithProtection(it) }

        println("\n=== Classe Engenheiro ===\n")
        TTKCalculator.findBestTTK(engenheiroWeapons, debug).toList().forEach { TTKCalculator.printWeaponTTKWithProtection(it) }

        println("\n=== Pistolas ===\n")
        TTKCalculator.findBestTTK(pistolas, debug).toList().forEach { TTKCalculator.printWeaponTTKWithProtection(it) }

        println("\n==== Tempo médio de resistência contra Fuzi. + Eng. com o conjunto ${fuzileiroWeapons.first().set.name}")

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
