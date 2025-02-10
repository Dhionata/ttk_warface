package br.com.dhionata

object TTKCalculator {

    fun calculateDamage(
        weaponDamage: Int,
        damageMultiplier: Double,
        absorption: Double, // Flat value
        resistance: Double,
        weaponTypeResistance: Double,
    ): Double {
        val damage = (weaponDamage * damageMultiplier - (absorption)) * (1 - resistance) * (1 - weaponTypeResistance)
        return if (damage < 0) throw IllegalArgumentException("Há alteração nos parâmetros de dano!") else damage // O dano não pode ser negativo
    }

    fun bulletsToKillWithProtection(
        weapon: Weapon,
        set: Set = Set.sirocco,
        isHeadshot: Boolean = false,
        debug: Boolean = false,
    ): Int {
        var remainingArmor = set.armor
        var remainingHealth = set.hp
        var shots = 0

        // Selecionar a proteção apropriada com base no tipo de tiro
        val equipmentProtection = if (isHeadshot) set.headProtection else set.bodyProtection

        // Calcula o damageMultiplier corretamente
        val damageMultiplier = ((if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier) * set.entityDmgMult * (1 + set.cyborgDmgBuff)) - equipmentProtection

        // Verifica se o damageMultiplier é negativo
        val effectiveDamageMultiplier = if (damageMultiplier < 0) 0.0 else damageMultiplier

        // Calcula o dano
        var finalDamage = calculateDamage(weapon.damage, effectiveDamageMultiplier, set.absorption, set.resistance, set.weaponTypeResistance)

        // Calcula a distribuição do dano
        val armorDamage = finalDamage * 0.8
        val healthDamage = finalDamage * 0.2

        // Loop para aplicar o dano até que a saúde chegue a 0 ou menos
        while (remainingHealth > 0) {

            if (debug) {
                println("------------------------------")
                println("Arma: ${weapon.name}")
                println(if (isHeadshot) "Cabeça" else "Corpo")
                println("Tiro ${shots + 1}:")
                println(" - Dano Total: $finalDamage")
                println(" - Dano à Armadura: $armorDamage")
                println(" - Dano à Saúde: $healthDamage")
                println(" - Armadura Antes do Tiro: $remainingArmor")
                println(" - Saúde Antes do Tiro: $remainingHealth")
            }
            // Aplica o dano à armadura
            remainingArmor -= armorDamage

            // Se a armadura ficar negativa, o dano restante vai para a saúde
            if (remainingArmor < 0) {
                remainingHealth += remainingArmor // remainingArmor é negativo
                remainingArmor = 0.0
            }

            // Aplica o dano à saúde
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


    fun calculateTTKWithProtection(
        weapon: Weapon,
        set: Set = Set.sirocco,
        isHeadshot: Boolean,
        debug: Boolean = false,
    ): Pair<Int, Double> { // Retorna (shots, totalTimeSeconds)
        val shotsNeeded = bulletsToKillWithProtection(
            weapon,
            set,
            isHeadshot,
            debug
        )

        // Calcula o tempo total baseado na taxa de fogo (DPM)
        val shotsPerSecond = weapon.fireRate / 60.0
        val totalTime = shotsNeeded / shotsPerSecond

        return Pair(shotsNeeded, totalTime)
    }

    fun printWeaponTTKWithProtection(
        weapon: Weapon,
        setStats: Set = Set.sirocco,
    ) {
        println("Conjunto: ${setStats.name}")
        println("Arma: ${weapon.name}")

        println(" - ${weapon.ttk.first().first} tiro(s) na cabeça em ${"%.3f".format(weapon.ttk.first().second)}(s)")

        println(" - ${weapon.ttk.elementAt(1).first} tiro(s) no corpo em ${"%.3f".format(weapon.ttk.elementAt(1).second)}(s)")

        println(" - TTK Médio: ${weapon.ttk.last().first} tiro(s) em ${"%.3f".format(weapon.ttk.last().second)}(s)")
        println("------------------------------")
    }

    fun findBestTTK(
        weapons: List<Weapon>,
        set: Set = Set.sirocco,
        debug: Boolean = false,
    ): Triple<Weapon, Weapon, Weapon> {
        val bestHeadWeapon = weapons.minBy { it.ttk.first().second }
        val bestBodyWeapon = weapons.minBy { it.ttk.elementAt(1).second }
        val bestTTKMediaWeapon = weapons.minBy { it.ttk.last().second }

        println("===================================")
        println(
            "Melhor TTK para a Cabeça: ${bestHeadWeapon.name} com TTK de ${"%.3f".format(bestHeadWeapon.ttk.first().second)} segundos em ${
                bulletsToKillWithProtection
                    (bestHeadWeapon, set, true, debug)
            } tiro(s)"
        )
        println(
            "Melhor TTK para o Corpo: ${bestBodyWeapon.name} com TTK de ${"%.3f".format(bestBodyWeapon.ttk.elementAt(1).second)} segundos em ${
                bulletsToKillWithProtection
                    (bestBodyWeapon, set, debug = debug)
            } tiros(s)"
        )
        println(
            "Melhor TTK Médio (Cabeça e Corpo): ${bestTTKMediaWeapon.name} com TTK Médio de ${"%.3f".format(bestTTKMediaWeapon.ttk.last().second)} segundos em ${
                (bulletsToKillWithProtection(
                    bestTTKMediaWeapon,
                    set,
                    true,
                    debug
                ) + bulletsToKillWithProtection(bestTTKMediaWeapon, set, debug = debug)) / 2
            } tiro(s)"
        )
        println("===================================")

        return Triple(bestHeadWeapon, bestBodyWeapon, bestTTKMediaWeapon)
    }
}
