package br.com.dhionata

import br.com.dhionata.weapon.Weapon
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.tan

object TTKCalculator {

    // Constantes para simulação de alvo (em metros)
    private const val HEAD_RADIUS = 0.13 // Cabeça (aprox. 13cm raio)
    private const val BODY_RADIUS = 0.35 // Corpo (aprox. 35cm raio - Hitbox generosa do Warface)

    // Fator de Densidade de Dispersão
    private const val SPREAD_DENSITY_FACTOR = 3.0
    /**
     * Calcula o dano de UM ÚNICO projétil/pellet.
     * Retorna Double para manter precisão antes da soma total.
     */
    private fun calculateSinglePelletDamage(
        weaponDamagePerPellet: Double, // Dano total da arma / numero de pellets
        damageMultiplier: Double,
        absorption: Double,
        pellets: Int,
        resistance: Double,
        weaponTypeResistance: Double,
    ): Double {
        // Regra da Wiki: Absorption é dividida pelo número de pellets
        val effectiveAbsorption = absorption / pellets.toDouble()

        val baseDamage = (weaponDamagePerPellet * damageMultiplier) - effectiveAbsorption

        if (baseDamage <= 0) return 0.0

        return baseDamage * (1 - resistance) * (1 - weaponTypeResistance)
    }

    /**
     * Calcula a porcentagem de acerto baseada na geometria real.
     */
    private fun calculateHitRate(
        distance: Double,
        spreadDegrees: Double,
        isHeadshot: Boolean,
    ): Double {
        // Distância mínima (cano encostado)
        if (distance <= 1.5) return 1.0
        if (spreadDegrees <= 0.0) return 1.0

        // 1. Ajusta o ângulo (Graus) dividindo pelo fator de densidade
        // Isso converte o "Espalhamento Total" para um "Raio Efetivo de Dano"
        val effectiveAngle = spreadDegrees / SPREAD_DENSITY_FACTOR

        // 2. Calcula o Raio do Cone em metros usando Tangente (Matemática correta)
        val spreadRadius = distance * tan(Math.toRadians(effectiveAngle))

        // 3. Define o raio do alvo
        val targetRadius = if (isHeadshot) HEAD_RADIUS else BODY_RADIUS

        // 4. Se o alvo é maior que o cone, acerta tudo
        if (targetRadius >= spreadRadius) return 1.0

        // 5. Proporção de Área (Chance de acerto)
        val hitRatio = (targetRadius * targetRadius) / (spreadRadius * spreadRadius)

        return min(1.0, max(0.0, hitRatio))
    }

    /**
     * Função Principal: Retorna o número de TIROS para matar.
     * Agora aceita o parâmetro isAiming para diferenciar Hipfire (Quadril) de ADS (Mira).
     */
    fun bulletsToKillWithProtectionInt(
        weapon: Weapon,
        isHeadshot: Boolean = false,
        isAiming: Boolean = false, // <--- NOVO PARÂMETRO (Padrão false = Hipfire)
        debug: Boolean = false,
        distance: Double = 0.0,
    ): Int {
        // Dados do Alvo
        val remainingArmor = weapon.set.armor
        var remainingHealth = weapon.set.hp

        // Multiplicadores
        val equipmentProtection = if (isHeadshot) weapon.set.headProtection else weapon.set.bodyProtection
        val damageMultiplier =
            ((if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier) * weapon.set.entityDmgMult * (1 + weapon.set.cyborgDmgBuff)) - equipmentProtection

        // Dano Base na Distância
        val effectiveDamage = if (distance > 0) weapon.getEffectiveDamage(distance) else weapon.damage

        // --- Lógica Diferenciada: Shotgun vs Rifle ---
        val totalPellets = weapon.pellets
        val damagePerPellet = effectiveDamage / totalPellets

        if (debug && totalPellets > 1) {
            println("--- DEBUG SHOTGUN (Dist: $distance m) ---")
            println("Dano Total Efetivo: $effectiveDamage (Min: ${weapon.minDamage})")
            println("Pellets: $totalPellets -> Dano/Pellet: $damagePerPellet")
        }

        // 1. Calcula dano de 1 pellet sofrendo absorção
        val singlePelletDamage = calculateSinglePelletDamage(
            damagePerPellet,
            damageMultiplier,
            weapon.set.absorption,
            totalPellets,
            weapon.set.resistance,
            weapon.set.weaponTypeResistance
        )

        // 2. Define quantos pellets acertam
        val hitRate = if (totalPellets > 1) {

            // Lógica de Seleção baseada no parâmetro isAiming
            // Se isAiming for TRUE e a arma tiver zoom configurado, usa o Zoom.
            // Caso contrário, usa o spreadMin (Quadril).
            val shouldUseScope = isAiming && weapon.zoomSpreadMin > 0

            val usedSpread = if (shouldUseScope) weapon.zoomSpreadMin else weapon.spreadMin

            val rate = calculateHitRate(distance, usedSpread, isHeadshot)

            if (debug) {
                val modeStr = if (shouldUseScope) "MIRA (Zoom)" else "QUADRIL (Hip)"
                println("Distância: $distance m | Modo: $modeStr")
                println("Spread Base: ${if(shouldUseScope) weapon.zoomSpreadMin else weapon.spreadMin} | Spread Usado: $usedSpread")
                println("HitRate: ${"%.2f".format(rate * 100)}% | Pellets Acertados: ${"%.2f".format(totalPellets * rate)}")
            }
            rate
        } else {
            1.0
        }

        // 2. Cálculo do Dano Bruto (Pellets que acertaram * Dano por Pellet)
        val rawDamage = singlePelletDamage * totalPellets * hitRate

        // --- NOVO: FATOR DE PENALIDADE DE MEMBROS A LONGA DISTÂNCIA ---
        // A 30m, é impossível acertar apenas o tronco com uma shotgun.
        // Se a distância for > 15m, aplicamos uma penalidade de dano de 25%
        // para simular pellets acertando braços/pernas em vez do tronco.
        val limbPenalty = if (distance > 15.0 && totalPellets > 1) 0.75 else 1.0

        val finalDamagePerShot = (rawDamage * limbPenalty).roundToInt()

        if (debug && totalPellets > 1) {
            println("Dano Final do Tiro: $finalDamagePerShot")
        }

        if (finalDamagePerShot <= 0) {
            if (debug) println("DEBUG: Dano final por tiro <= 0. Impossível matar.")
            return Int.MAX_VALUE
        }

        // --- Simulação dos Tiros (Lógica de Armadura Warface) ---
        val armorAbsorptionRatio = if (weapon.name.contains("SED", true)) 0.99 else 0.80

        // Modo DEBUG detalhado passo a passo
        if (debug) {
            var shots = 0
            // Reinicia variáveis locais para simulação
            var simArmor = remainingArmor
            var simHealth = remainingHealth

            while (simHealth > 0) {
                shots++
                val absorbAmount = (finalDamagePerShot * armorAbsorptionRatio).roundToInt()

                val currentArmorDmg: Double
                val currentHealthDmg: Double

                if (simArmor > 0) {
                    if (simArmor - absorbAmount < 0) {
                        currentArmorDmg = simArmor
                        currentHealthDmg = (finalDamagePerShot - absorbAmount).toDouble()
                    } else {
                        currentArmorDmg = absorbAmount.toDouble()
                        currentHealthDmg = (finalDamagePerShot - absorbAmount).toDouble()
                    }
                } else {
                    currentArmorDmg = 0.0
                    currentHealthDmg = finalDamagePerShot.toDouble()
                }

                simArmor -= currentArmorDmg
                if (simArmor < 0) simArmor = 0.0
                simHealth -= currentHealthDmg

                println("Tiro $shots | Dano: $finalDamagePerShot | HP Restante: ${"%.1f".format(simHealth)}")

                // Prevenção de loop infinito em debug
                if (shots > 100) break
            }
            return shots
        }

        // --- Cálculo Matemático Otimizado (Sem loop) ---
        var shots = 0

        // Fase 1: Com Armadura
        val absorbAmount = (finalDamagePerShot * armorAbsorptionRatio).roundToInt()
        val damageToHealthPhase1 = finalDamagePerShot - absorbAmount

        if (remainingArmor > 0 && absorbAmount > 0) {
            val shotsToBreakArmor = ceil(remainingArmor / absorbAmount).toInt()
            val potentialHealthDamage = shotsToBreakArmor.toDouble() * damageToHealthPhase1

            if (remainingHealth <= potentialHealthDamage) {
                // Morre antes da armadura quebrar
                return ceil(remainingHealth / damageToHealthPhase1).toInt()
            } else {
                // Sobrevive à quebra da armadura
                shots += shotsToBreakArmor
                remainingHealth -= potentialHealthDamage
            }
        }

        // Fase 2: Sem Armadura
        if (remainingHealth > 0) {
            shots += ceil(remainingHealth / finalDamagePerShot).toInt()
        }

        return shots
    }

    /**
     * Calcula o TTK retornando o PAR (Tiros, Tempo).
     * Mantém compatibilidade com o resto do seu código.
     */
    fun calculateTTKWithProtectionInt(
        weapon: Weapon,
        isHeadshot: Boolean,
        debug: Boolean = false,
    ): Pair<Int, Double> {
        val shotsNeeded = bulletsToKillWithProtectionInt(weapon, isHeadshot, debug)

        if (shotsNeeded == Int.MAX_VALUE) {
            return Pair(shotsNeeded, Double.POSITIVE_INFINITY)
        }

        val shotsPerSecond = weapon.fireRate / 60.0

        // Cálculo de tempo clássico (Ciclo completo)
        var timeToKill = shotsNeeded / shotsPerSecond

        // Se a arma tem capacidade de pente definida e os tiros necessários excedem a capacidade
        if (weapon.magazineCapacity in 1..<shotsNeeded) {
            val reloadsNeeded = (shotsNeeded - 1) / weapon.magazineCapacity
            timeToKill += reloadsNeeded * (weapon.reloadTime / 1000.0)
        }

        return Pair(shotsNeeded, timeToKill)
    }

    /**
     * Mesma lógica, mas permitindo passar uma distância customizada.
     */
    fun calculateTTKAtDistance(
        weapon: Weapon,
        isHeadshot: Boolean,
        distance: Double,
        isAiming: Boolean = false, // Adicione aqui também
        debug: Boolean = false,
    ): Pair<Int, Double> {
        val shotsNeeded = bulletsToKillWithProtectionInt(weapon, isHeadshot, isAiming, debug, distance)

        if (shotsNeeded == Int.MAX_VALUE) {
            return Pair(shotsNeeded, Double.POSITIVE_INFINITY)
        }

        val shotsPerSecond = weapon.fireRate / 60.0
        var timeToKill = shotsNeeded / shotsPerSecond

        // Se a arma tem capacidade de pente definida e os tiros necessários excedem a capacidade
        if (weapon.magazineCapacity in 1..<shotsNeeded) {
            val reloadsNeeded = (shotsNeeded - 1) / weapon.magazineCapacity
            timeToKill += reloadsNeeded * (weapon.reloadTime / 1000.0)
        }

        return Pair(shotsNeeded, timeToKill)
    }

    fun calculateMaxDistanceForKill(
        weapon: Weapon,
        isHeadshot: Boolean,
    ): Double {
        val hp = weapon.set.hp
        val armor = weapon.set.armor
        val equipmentProtection = if (isHeadshot) weapon.set.headProtection else weapon.set.bodyProtection
        val baseMultiplier = if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier
        val damageMultiplier = (baseMultiplier * weapon.set.entityDmgMult * (1 + weapon.set.cyborgDmgBuff)) - equipmentProtection

        if (damageMultiplier <= 0) return 0.0

        val damageForKill = min(hp * 5.0, hp + armor)

        val resistanceFactor = (1 - weapon.set.resistance) * (1 - weapon.set.weaponTypeResistance)
        if (resistanceFactor <= 0) return 0.0

        val damageBeforeResistance = damageForKill / resistanceFactor

        val damageBeforeAbsorption = damageBeforeResistance + weapon.set.absorption

        val requiredWeaponDamage = damageBeforeAbsorption / damageMultiplier

        if (weapon.damage < requiredWeaponDamage) return 0.0
        if (weapon.minDamage >= requiredWeaponDamage) return Double.POSITIVE_INFINITY

        val allowedDrop = weapon.damage - requiredWeaponDamage
        val distAdd = allowedDrop / weapon.damageDropPerMeter

        return weapon.range + distAdd
    }

    fun printWeaponTTKWithProtection(
        weapon: Weapon,
    ) {
        println("Arma: ${weapon.name}")

        println(" - ${weapon.ttk.first().first} tiro(s) na cabeça em ${"%.3f".format(weapon.ttk.first().second)}(s)")

        println(" - ${weapon.ttk.elementAt(1).first} tiro(s) no corpo em ${"%.3f".format(weapon.ttk.elementAt(1).second)}(s)")

        println(" - TTK Médio: ${weapon.ttk.last().first} tiro(s) em ${"%.3f".format(weapon.ttk.last().second)}(s)")
        println("------------------------------")
    }

    fun findBestTTK(
        weapons: List<Weapon>,
        debug: Boolean = false,
    ): Triple<Weapon, Weapon, Weapon> {
        val bestHeadWeapon = weapons.minBy { it.ttk.first().second }
        val bestBodyWeapon = weapons.minBy { it.ttk.elementAt(1).second }
        val bestTTKMediaWeapon = weapons.minBy { it.ttk.last().second }

        println("===================================")
        println(
            "Melhor TTK para a Cabeça: ${bestHeadWeapon.name} com TTK de ${"%.3f".format(bestHeadWeapon.ttk.first().second)} segundos em ${
                bulletsToKillWithProtectionInt(bestHeadWeapon, true, debug)
            } tiro(s)"
        )
        println(
            "Melhor TTK para o Corpo: ${bestBodyWeapon.name} com TTK de ${"%.3f".format(bestBodyWeapon.ttk.elementAt(1).second)} segundos em${
                bulletsToKillWithProtectionInt
                    (bestBodyWeapon, debug = debug)
            } tiros(s)"
        )
        println(
            "Melhor TTK Médio (Cabeça e Corpo): ${bestTTKMediaWeapon.name} com TTK Médio de ${"%.3f".format(bestTTKMediaWeapon.ttk.last().second)} segundos em ${
                (bulletsToKillWithProtectionInt(bestTTKMediaWeapon, true, debug) + bulletsToKillWithProtectionInt(bestTTKMediaWeapon, debug = debug)) / 2
            } tiro(s)"
        )
        println("===================================")

        return Triple(bestHeadWeapon, bestBodyWeapon, bestTTKMediaWeapon)
    }
}
