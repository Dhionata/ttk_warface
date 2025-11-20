import kotlin.math.roundToInt

object TTKCalculator {

    private fun calculateDamageInt(
        weaponDamage: Double,
        damageMultiplier: Double,
        absorption: Double,
        pellets: Int = 1,
        resistance: Double,
        weaponTypeResistance: Double,
    ): Int {
        val effectiveAbsorption = absorption / pellets
        val baseDamage = weaponDamage * damageMultiplier - effectiveAbsorption
        if (baseDamage <= 0) return 0
        val finalDamage = baseDamage * (1 - resistance) * (1 - weaponTypeResistance)
        return finalDamage.roundToInt()
    }

    fun bulletsToKillWithProtectionInt(
        weapon: Weapon,
        isHeadshot: Boolean = false,
        debug: Boolean = false,
    ): Int {
        var remainingArmor = weapon.set.armor
        var remainingHealth = weapon.set.hp
        var shots = 0

        val equipmentProtection = if (isHeadshot) weapon.set.headProtection else weapon.set.bodyProtection

        val damageMultiplier =
            ((if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier) * weapon.set.entityDmgMult * (1 + weapon.set.cyborgDmgBuff)) - equipmentProtection

        val finalDamage = calculateDamageInt(
            weapon.damage, damageMultiplier, weapon.set.absorption, resistance = weapon.set.resistance, weaponTypeResistance = weapon.set.weaponTypeResistance
        )

        val armorDamage = (finalDamage * 80) / 100
        val healthDamage = finalDamage - armorDamage

        while (remainingHealth > 0) {
            if (debug) {
                println("------------------------------")
                println("Arma: ${weapon.name}")
                println(if (isHeadshot) "Headshot" else "Corpo")
                println("Tiro ${shots + 1}:")
                println(" - Dano Total (arredondado): $finalDamage")
                println(" - Dano à Armadura: $armorDamage")
                println(" - Dano à Saúde: $healthDamage")
                println(" - Armadura Antes do Tiro: $remainingArmor")
                println(" - Saúde Antes do Tiro: $remainingHealth")
            }

            remainingArmor -= armorDamage
            if (remainingArmor < 0) {
                remainingHealth += remainingArmor
                remainingArmor = 0
            }

            remainingHealth -= healthDamage

            if (debug) {
                println(" - Armadura Após o Tiro: $remainingArmor")
                println(" - Saúde Após o Tiro: $remainingHealth")
                println("------------------------------")
            }
            shots++
        }

        return shots
    }

    fun calculateTTKWithProtectionInt(
        weapon: Weapon,
        isHeadshot: Boolean,
        debug: Boolean = false,
    ): Pair<Int, Double> {
        val shotsNeeded = bulletsToKillWithProtectionInt(weapon, isHeadshot, debug)
        val shotsPerSecond = weapon.fireRate / 60.0
        val totalTime = shotsNeeded / shotsPerSecond
        return Pair(shotsNeeded, totalTime)
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
            "Melhor TTK para o Corpo: ${bestBodyWeapon.name} com TTK de ${"%.3f".format(bestBodyWeapon.ttk.elementAt(1).second)} segundos em ${
                bulletsToKillWithProtectionInt(bestBodyWeapon, debug = debug)
            } tiros(s)"
        )
        println(
            "Melhor TTK Médio (Cabeça e Corpo): ${bestTTKMediaWeapon.name} com TTK Médio de ${"%.3f".format(bestTTKMediaWeapon.ttk.last().second)} segundos em ${
                (bulletsToKillWithProtectionInt(
                    bestTTKMediaWeapon, true, debug
                ) + bulletsToKillWithProtectionInt(bestTTKMediaWeapon, debug = debug)) / 2
            } tiro(s)"
        )
        println("===================================")

        return Triple(bestHeadWeapon, bestBodyWeapon, bestTTKMediaWeapon)
    }
}
