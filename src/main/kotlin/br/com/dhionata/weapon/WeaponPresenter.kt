package br.com.dhionata.weapon

import br.com.dhionata.analysis.AnalyzedWeapon
import br.com.dhionata.calculator.ITTKCalculator
import br.com.dhionata.formatValue
import kotlin.math.roundToInt

class WeaponPresenter(
    private val ttkCalculator: ITTKCalculator,
) {

    private fun formatWeaponInfo(analyzedWeapon: AnalyzedWeapon): String {
        val weapon = analyzedWeapon.weapon
        val targetSet = analyzedWeapon.targetSet

        val maxHeadDist = ttkCalculator.calculateMaxDistanceForKill(weapon, targetSet, true)
        val maxBodyDist = ttkCalculator.calculateMaxDistanceForKill(weapon, targetSet, false)

        val oneHitKillInfo = if (maxHeadDist > 0.0 || maxBodyDist > 0.0) {
            val headDistStr = if (maxHeadDist.isInfinite()) "Infinita" else "${"%.2f".format(maxHeadDist)}m"
            val bodyDistStr = if (maxBodyDist.isInfinite()) "Infinita" else "${"%.2f".format(maxBodyDist)}m"
            "| Dist. Max (1 tiro): Cabeça[$headDistStr], Corpo[$bodyDistStr] "
        } else {
            ""
        }

        val distanceToMinDamage = if (weapon.damageDropPerMeter > 0) {
            weapon.range + ((weapon.damage - weapon.minDamage) / weapon.damageDropPerMeter)
        } else {
            Double.POSITIVE_INFINITY
        }
        val minDamageDistStr = if (distanceToMinDamage.isInfinite()) "Infinita" else "${"%.2f".format(distanceToMinDamage)}m"

        val accuracyInfo = if (weapon.spreadMin > 0 || weapon.spreadMax > 0) {
            "| Precisão: Hip[${calculateAccuracy(weapon.spreadMin, weapon.spreadMax)}], Aim[${calculateAccuracy(weapon.zoomSpreadMin, weapon.zoomSpreadMax)}] "
        } else {
            ""
        }

        val pelletsInfo = if (weapon.pellets > 1) "| Pellets: ${weapon.pellets} " else ""

        val magazineInfo = if (weapon.magazineCapacity > 0) "| Pente: ${weapon.magazineCapacity} " else ""
        val reloadInfo = if (weapon.reloadTime > 0) "| Recarga: ${formatValue(weapon.reloadTime)}ms " else ""

        return "Nome: ${weapon.name} | Dano: ${formatValue(weapon.damage)} $pelletsInfo| Cadência: ${weapon.fireRate} | Cabeça X ${formatValue(weapon.headMultiplier)} | Corpo X ${
            formatValue(weapon.bodyMultiplier)
        } | Alcance: ${
            formatValue(weapon.range)
        }m | Queda/m: ${
            formatValue(weapon.damageDropPerMeter)
        } | Dano Mín.: ${weapon.minDamage} @ $minDamageDistStr $oneHitKillInfo$accuracyInfo$magazineInfo$reloadInfo| TTK[Tiro(s) em Tempo(s)]: Cabeça[${analyzedWeapon.ttkHead.first} em ${
            formatValue(analyzedWeapon.ttkHead.second, 3)
        }], Corpo[${analyzedWeapon.ttkBody.first} em ${
            formatValue(analyzedWeapon.ttkBody.second, 3)
        }], Média[${analyzedWeapon.ttkAverage.first} em ${formatValue(analyzedWeapon.ttkAverage.second, 3)}]"
    }

    private fun calculateAccuracy(min: Double, max: Double): Int {
        val sum = min + max
        return when {
            sum <= 20 -> ((40 - sum) / 0.4).roundToInt()
            sum <= 60 -> (50 - (sum - 20)).roundToInt()
            else -> (10 - ((sum - 60) * 0.165)).roundToInt()
        }
    }

    private fun printSortedWeapons(title: String, weapons: List<AnalyzedWeapon>, comparator: Comparator<AnalyzedWeapon>) {
        println(title)
        weapons.sortedWith(comparator).forEach { println(formatWeaponInfo(it)) }
    }

    private fun printBestTTKForClass(className: String, weapons: List<AnalyzedWeapon>, debug: Boolean) {
        println("\n=== Classe $className ===\n")
        if (weapons.isNotEmpty()) {
            println("Contra Conjunto: ${weapons.first().targetSet.name}")
            val bestHead = weapons.minBy { it.ttkHead.second }
            val bestBody = weapons.minBy { it.ttkBody.second }
            val bestAvg = weapons.minBy { it.ttkAverage.second }

            println("Melhor TTK Cabeça: ${bestHead.weapon.name} (${formatValue(bestHead.ttkHead.second, 3)}s)")
            println("Melhor TTK Corpo: ${bestBody.weapon.name} (${formatValue(bestBody.ttkBody.second, 3)}s)")
            println("Melhor TTK Média: ${bestAvg.weapon.name} (${formatValue(bestAvg.ttkAverage.second, 3)}s)")
        }
    }

    private fun printAverageStats(
        title: String,
        weapons: List<AnalyzedWeapon>,
        shotsSelector: (AnalyzedWeapon) -> Int,
        timeSelector: (AnalyzedWeapon) -> Double,
    ) {
        println(title)

        println("== Tiros ==")
        val averageShots = weapons.map(shotsSelector).average()
        println(formatValue(averageShots))

        println("== Tempo ==")
        val averageTime = weapons.map(timeSelector).average()
        println(formatValue(averageTime) + "\n")
    }

    private fun printTTKRanksByDistance(className: String, weapons: List<AnalyzedWeapon>, mode: String, maxDistance: Int = 120) {
        if (weapons.isEmpty()) return

        val title = when (mode) {
            "HEAD" -> "Cabeça"
            "BODY" -> "Corpo"
            "AVG" -> "Média"
            else -> "Desconhecido"
        }
        println("\n=== Ranking de Melhor TTK por Distância - $className ($title) ===")

        val ttkCache: List<List<Double>> = weapons.map { analyzedWeapon ->
            val weapon = analyzedWeapon.weapon
            val targetSet = analyzedWeapon.targetSet
            (1..maxDistance).map { distance ->
                val d = distance.toDouble()
                when (mode) {
                    "HEAD" -> ttkCalculator.calculateTTK(weapon, targetSet, true, d).second
                    "BODY" -> ttkCalculator.calculateTTK(weapon, targetSet, false, d).second
                    "AVG" -> {
                        val head = ttkCalculator.calculateTTK(weapon, targetSet, true, d).second
                        val body = ttkCalculator.calculateTTK(weapon, targetSet, false, d).second
                        (head + body) / 2.0
                    }

                    else -> Double.POSITIVE_INFINITY
                }
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
            val name = weapons[weaponIndex].weapon.name
            val ttkStart = ttkCache[weaponIndex][start - 1]
            val ttkEnd = ttkCache[weaponIndex][end - 1]

            println("De ${start}m a ${end}m: $name (TTK varia de ${"%.3f".format(ttkStart)}s a ${"%.3f".format(ttkEnd)}s)")
        }
    }

    private fun printWeaponTTKEvolution(className: String, weapons: List<AnalyzedWeapon>, maxDistance: Int = 120) {
        if (weapons.isEmpty()) return

        println("\n=== Evolução do TTK por Distância - $className ===")

        weapons.forEach { analyzedWeapon ->
            val weapon = analyzedWeapon.weapon
            val targetSet = analyzedWeapon.targetSet

            println("\nArma: ${weapon.name}")
            println("Distância | TTK Cabeça | TTK Corpo")
            println("----------|------------|----------")

            for (distance in 0..maxDistance) {
                val ttkHead = ttkCalculator.calculateTTK(weapon, targetSet, true, distance.toDouble()).second
                val ttkBody = ttkCalculator.calculateTTK(weapon, targetSet, false, distance.toDouble()).second

                val headStr = if (ttkHead.isInfinite()) "---" else "%.3fs".format(ttkHead)
                val bodyStr = if (ttkBody.isInfinite()) "---" else "%.3fs".format(ttkBody)

                println("${distance.toString().padEnd(9)} | ${headStr.padEnd(10)} | $bodyStr")
            }
        }
    }

    fun printDetailedAllWeaponsInfo(
        fuzileiroWeapons: List<AnalyzedWeapon>,
        engenheiroWeapons: List<AnalyzedWeapon>,
        sniperWeapons: List<AnalyzedWeapon>,
        medicWeapons: List<AnalyzedWeapon>,
        pistolas: List<AnalyzedWeapon>,
        debug: Boolean = false,
    ) {
        if (fuzileiroWeapons.isEmpty() && engenheiroWeapons.isEmpty() && sniperWeapons.isEmpty() && medicWeapons.isEmpty() && pistolas.isEmpty()) {
            println("Nenhuma arma para analisar.")
            return
        }
        val currentSet = (fuzileiroWeapons + engenheiroWeapons + sniperWeapons + medicWeapons + pistolas).firstOrNull()?.targetSet
        println("\n=== Detalhes para o conjunto ===\n\n==== ${currentSet?.name} ====")

        val bodyComparator = compareBy<AnalyzedWeapon> { it.ttkBody.second }.thenBy { it.ttkHead.second }.thenBy { it.ttkAverage.second }
        val headComparator = compareBy<AnalyzedWeapon> { it.ttkHead.second }.thenBy { it.ttkBody.second }.thenBy { it.ttkAverage.second }
        val averageComparator = compareBy<AnalyzedWeapon> { it.ttkAverage.second }.thenBy { it.ttkHead.second }.thenBy { it.ttkBody.second }

        mapOf(
            "\n==== TTK no corpo ====" to bodyComparator, "\n==== TTK na cabeça ====" to headComparator, "\n==== Média do TTK por Arma ====" to averageComparator
        ).forEach { (title, comparator) ->
            println(title)
            printSortedWeapons("\n=== Classe Fuzileiro ===\n", fuzileiroWeapons, comparator)
            printSortedWeapons("\n=== Classe Engenheiro ===\n", engenheiroWeapons, comparator)
            printSortedWeapons("\n=== Classe Sniper ===\n", sniperWeapons, comparator)
            printSortedWeapons("\n=== Classe Médico ===\n", medicWeapons, comparator)
            printSortedWeapons("\n=== Pistolas ===\n", pistolas, comparator)
        }

        val allPrimaryWeapons = fuzileiroWeapons + engenheiroWeapons + sniperWeapons + medicWeapons
        mapOf(
            "\n=== Fuzileiro + Engenheiro + Sniper + Médico ===\n\n==== TTK na cabeça ====" to headComparator,
            "\n=== Fuzileiro + Engenheiro + Sniper + Médico ===\n\n==== TTK no corpo ====" to bodyComparator,
            "\n=== Fuzileiro + Engenheiro + Sniper + Médico ===\n\n==== TTK Médio ====" to averageComparator
        ).forEach { (title, comparator) ->
            printSortedWeapons(title, allPrimaryWeapons, comparator)
        }

        println("\n=== Melhores TTKs ===")
        printBestTTKForClass("Fuzileiro", fuzileiroWeapons, debug)
        printBestTTKForClass("Engenheiro", engenheiroWeapons, debug)
        printBestTTKForClass("Sniper", sniperWeapons, debug)
        printBestTTKForClass("Médico", medicWeapons, debug)
        printBestTTKForClass("Pistolas", pistolas, debug)

        if (currentSet != null) {
            println("\n==== Tempo médio de resistência contra Fuzi. + Eng. + Sniper + Méd. com o conjunto ${currentSet.name}")
            printAverageStats("\n=== Cabeça ===", allPrimaryWeapons, { it.ttkHead.first }, { it.ttkHead.second })
            printAverageStats("\n=== Corpo ===", allPrimaryWeapons, { it.ttkBody.first }, { it.ttkBody.second })
        }

        val classes = mapOf(
            "Fuzileiro" to fuzileiroWeapons, "Engenheiro" to engenheiroWeapons, "Sniper" to sniperWeapons, "Médico" to medicWeapons, "Pistolas" to pistolas
        )

        classes.forEach { (className, weapons) ->
            if (weapons.isNotEmpty()) {
                printTTKRanksByDistance(className, weapons, "HEAD")
                printTTKRanksByDistance(className, weapons, "BODY")
                printTTKRanksByDistance(className, weapons, "AVG")

                printWeaponTTKEvolution(className, weapons)
            }
        }
    }
}
