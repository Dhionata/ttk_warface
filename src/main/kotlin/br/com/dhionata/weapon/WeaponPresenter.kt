package br.com.dhionata.weapon

import br.com.dhionata.TTKCalculator
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
        println("${BigDecimal(averageTime).setScale(2, RoundingMode.HALF_UP)}\n")
    }

    private fun printTTKRanksByDistance(className: String, weapons: List<Weapon>, isHeadshot: Boolean, maxDistance: Int = 120) {
        if (weapons.isEmpty()) return

        val title = if (isHeadshot) "Cabeça" else "Corpo"
        println("\n=== Ranking de Melhor TTK por Distância - $className ($title) ===")

        val ttkCache: List<List<Double>> = weapons.map { weapon ->
            (1..maxDistance).map { distance ->
                TTKCalculator.calculateTTKAtDistance(weapon, isHeadshot, distance.toDouble()).second
            }
        }

        val bestWeaponIndexPerMeter = (1..maxDistance).map { distance ->
            val distanceIndex = distance - 1
            val bestWeaponIndex = ttkCache.indices.minByOrNull { weaponIndex ->
                ttkCache[weaponIndex][distanceIndex]
            } ?: -1
            distance to bestWeaponIndex
        }

        if (bestWeaponIndexPerMeter.isEmpty()) return

        val distanceRanges = mutableListOf<Triple<Int, Int, Int>>()
        var currentBestIndex = bestWeaponIndexPerMeter.first().second
        var startDistance = 1

        for ((distance, weaponIndex) in bestWeaponIndexPerMeter) {
            if (weaponIndex != currentBestIndex) {
                if (currentBestIndex != -1) {
                    distanceRanges.add(Triple(startDistance, distance - 1, currentBestIndex))
                }
                startDistance = distance
                currentBestIndex = weaponIndex
            }
        }

        if (currentBestIndex != -1) {
            distanceRanges.add(Triple(startDistance, maxDistance, currentBestIndex))
        }

        distanceRanges.forEach { (start, end, weaponIndex) ->
            val weapon = weapons[weaponIndex]
            val ttkStart = ttkCache[weaponIndex][start - 1]
            val ttkEnd = ttkCache[weaponIndex][end - 1]

            println("De ${start}m a ${end}m: ${weapon.name} (TTK varia de ${"%.3f".format(ttkStart)}s a ${"%.3f".format(ttkEnd)}s)")
        }
    }

    private fun printWeaponTTKEvolution(className: String, weapons: List<Weapon>, maxDistance: Int = 120) {
        if (weapons.isEmpty()) return

        println("\n=== Evolução do TTK por Distância (a cada 5m) - $className ===")

        weapons.forEach { weapon ->
            println("\nArma: ${weapon.name}")
            println("Distância | TTK Cabeça | TTK Corpo")
            println("----------|------------|----------")

            for (distance in 5..maxDistance step 5) {
                val ttkHead = TTKCalculator.calculateTTKAtDistance(weapon, true, distance.toDouble()).second
                val ttkBody = TTKCalculator.calculateTTKAtDistance(weapon, false, distance.toDouble()).second

                val headStr = if (ttkHead.isInfinite()) "---" else "%.3fs".format(ttkHead)
                val bodyStr = if (ttkBody.isInfinite()) "---" else "%.3fs".format(ttkBody)

                println("${distance.toString().padEnd(9)} | ${headStr.padEnd(10)} | $bodyStr")
            }
        }
    }

    fun printDetailedAllWeaponsInfo(
        fuzileiroWeapons: List<Weapon>,
        engenheiroWeapons: List<Weapon>,
        sniperWeapons: List<Weapon>,
        pistolas: List<Weapon>,
        debug: Boolean = false,
    ) {
        if (fuzileiroWeapons.isEmpty() && engenheiroWeapons.isEmpty() && sniperWeapons.isEmpty() && pistolas.isEmpty()) {
            println("Nenhuma arma para analisar.")
            return
        }
        val currentSet = (fuzileiroWeapons + engenheiroWeapons + sniperWeapons + pistolas).firstOrNull()?.set
        println("\n=== Detalhes para o conjunto ===\n\n==== ${currentSet?.name} ====")

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

        if (currentSet != null) {
            println("\n==== Tempo médio de resistência contra Fuzi. + Eng. + Sniper com o conjunto ${currentSet.name}")
            printAverageStats("\n=== Cabeça ===", allPrimaryWeapons, { it.ttk.first().first }, { it.ttk.first().second })
            printAverageStats("\n=== Corpo ===", allPrimaryWeapons, { it.ttk[1].first }, { it.ttk[1].second })
        }

        val classes = mapOf(
            "Fuzileiro" to fuzileiroWeapons,
            "Engenheiro" to engenheiroWeapons,
            "Sniper" to sniperWeapons,
            "Pistolas" to pistolas
        )

        classes.forEach { (className, weapons) ->
            if (weapons.isNotEmpty()) {
                printTTKRanksByDistance(className, weapons, isHeadshot = true)
                printTTKRanksByDistance(className, weapons, isHeadshot = false)

                printWeaponTTKEvolution(className, weapons)
            }
        }
    }
}
