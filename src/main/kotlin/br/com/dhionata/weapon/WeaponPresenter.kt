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

    private fun printTTKRanksByDistance(weapons: List<Weapon>, isHeadshot: Boolean, maxDistance: Int = 120) {
        if (weapons.isEmpty()) return

        val title = if (isHeadshot) "Cabeça" else "Corpo"
        println("\n=== Ranking de Melhor TTK por Distância ($title) ===")

        // 1. Otimização e Segurança: Usar Listas em vez de Mapas para evitar problemas com hashCode de objetos mutáveis.
        // cache[weaponIndex][distanceIndex] = TTK
        val ttkCache: List<List<Double>> = weapons.map { weapon ->
            (1..maxDistance).map { distance ->
                TTKCalculator.calculateTTKAtDistance(weapon, isHeadshot, distance.toDouble()).second
            }
        }

        // 2. Encontrar o índice da melhor arma para cada metro.
        // Retorna uma lista de Pair<Distancia, IndiceDaMelhorArma>
        val bestWeaponIndexPerMeter = (1..maxDistance).map { distance ->
            val distanceIndex = distance - 1
            // Encontra o índice da arma com o menor TTK nesta distância
            val bestWeaponIndex = ttkCache.indices.minByOrNull { weaponIndex ->
                ttkCache[weaponIndex][distanceIndex]
            } ?: -1
            distance to bestWeaponIndex
        }

        if (bestWeaponIndexPerMeter.isEmpty()) return

        // 3. Consolidar os resultados em faixas de distância.
        val distanceRanges = mutableListOf<Triple<Int, Int, Int>>() // Start, End, WeaponIndex
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
        // Adiciona o último range
        if (currentBestIndex != -1) {
            distanceRanges.add(Triple(startDistance, maxDistance, currentBestIndex))
        }

        // 4. Imprimir os resultados finais.
        distanceRanges.forEach { (start, end, weaponIndex) ->
            val weapon = weapons[weaponIndex]
            // Recupera os valores do cache (lembrando que distance 1 é index 0)
            val ttkStart = ttkCache[weaponIndex][start - 1]
            val ttkEnd = ttkCache[weaponIndex][end - 1]

            println("De ${start}m a ${end}m: ${weapon.name} (TTK varia de ${"%.3f".format(ttkStart)}s a ${"%.3f".format(ttkEnd)}s)")
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

        // Imprime os rankings de TTK por distância para todas as armas primárias
        val allWeapons = fuzileiroWeapons + engenheiroWeapons + sniperWeapons + pistolas
        printTTKRanksByDistance(allWeapons, isHeadshot = true)
        printTTKRanksByDistance(allWeapons, isHeadshot = false)
    }
}
