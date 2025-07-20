package br.com.dhionata

import java.math.BigDecimal
import java.math.RoundingMode

object WeaponPresenter {

    private fun printSortedWeapons(title: String, weapons: List<Weapon>, comparator: Comparator<Weapon>) {
        println(title)
        weapons.sortedWith(comparator).forEach { println(it) }
    }

    private fun printBestTTKForClass(className: String, weapons: List<Weapon>, debug: Boolean) {
        println("\n=== Classe $className ===\n")
        if (weapons.isNotEmpty()) {
            println("Contra Conjunto: ${weapons.first().set.name}")
            TTKCalculator.findBestTTK(weapons, debug).toList().forEach { TTKCalculator.printWeaponTTKWithProtection(it) }
        }
    }

    private fun printAverageStats(
        title: String,
        weapons: List<Weapon>,
        shotsSelector: (Weapon) -> Int,
        timeSelector: (Weapon) -> Double,
    ) {
        println(title)

        println("== Tiros ==")
        val averageShots = weapons.map(shotsSelector).average()
        println(BigDecimal(averageShots).setScale(2, RoundingMode.HALF_UP))

        println("== Tempo ==")
        val averageTime = weapons.map(timeSelector).average()
        println(BigDecimal(averageTime).setScale(2, RoundingMode.HALF_UP))
    }

    fun printDetailedAllWeaponsInfo(fuzileiroWeapons: List<Weapon>, engenheiroWeapons: List<Weapon>, sniperWeapons: List<Weapon>, pistolas: List<Weapon>, debug: Boolean = false) {
        if (fuzileiroWeapons.isEmpty() && engenheiroWeapons.isEmpty() && sniperWeapons.isEmpty()) {
            println("Nenhuma arma para analisar.")
            return
        }
        val currentSet = (fuzileiroWeapons + engenheiroWeapons + sniperWeapons).first().set
        println("\n=== Detalhes para o conjunto ===\n\n==== ${currentSet.name} ====")

        val bodyComparator = compareBy<Weapon> { it.ttk[1].second }.thenBy { it.ttk.first().second }.thenBy { it.ttk.last().second }
        val headComparator = compareBy<Weapon> { it.ttk.first().second }.thenBy { it.ttk[1].second }.thenBy { it.ttk.last().second }
        val averageComparator = compareBy<Weapon> { it.ttk.last().second }.thenBy { it.ttk.first().second }.thenBy { it.ttk[1].second }

        mapOf(
            "\n==== TTK no corpo ====" to bodyComparator,
            "\n==== TTK na cabeça ====" to headComparator,
            "\n==== Média do TTK por Arma ====" to averageComparator
        ).forEach { (title, comparator) ->
            println(title)
            printSortedWeapons("\n=== Classe Fuzileiro ===\n", fuzileiroWeapons, comparator)
            printSortedWeapons("\n=== Classe Engenheiro ===\n", engenheiroWeapons, comparator)
            printSortedWeapons("\n=== Classe Sniper ===\n", sniperWeapons, comparator)
            printSortedWeapons("\n=== Pistolas ===\n", pistolas, comparator)
        }

        val allPrimaryWeapons = fuzileiroWeapons + engenheiroWeapons + sniperWeapons
        mapOf(
            "\n=== Fuzileiro + Engenheiro + Sniper ===\n\n==== TTK na cabeça ====" to headComparator,
            "\n=== Fuzileiro + Engenheiro + Sniper ===\n\n==== TTK no corpo ====" to bodyComparator,
            "\n=== Fuzileiro + Engenheiro + Sniper ===\n\n==== TTK Médio ====" to averageComparator
        ).forEach { (title, comparator) ->
            printSortedWeapons(title, allPrimaryWeapons, comparator)
        }

        println("\n=== Melhores TTKs ===")
        printBestTTKForClass("Fuzileiro", fuzileiroWeapons, debug)
        printBestTTKForClass("Engenheiro", engenheiroWeapons, debug)
        printBestTTKForClass("Sniper", sniperWeapons, debug)
        printBestTTKForClass("Pistolas", pistolas, debug)

        println("\n==== Tempo médio de resistência contra Fuzi. + Eng. + Sniper com o conjunto ${currentSet.name}")
        printAverageStats("\n=== Cabeça ===", allPrimaryWeapons, { it.ttk.first().first }, { it.ttk.first().second })
        printAverageStats("\n=== Corpo ===", allPrimaryWeapons, { it.ttk[1].first }, { it.ttk[1].second })
    }
}
