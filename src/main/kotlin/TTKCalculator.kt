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
        return if (damage < 0) 0.0 else damage // O dano não pode ser negativo
    }

    fun bulletsToKillWithProtection(
        weapon: Weapon,
        set: Set,
        isHeadshot: Boolean = false,
        debug: Boolean = false,
    ): Int {
        var remainingArmor = set.armor
        var remainingHealth = set.hp
        var shots = 1

        // Selecionar a proteção apropriada com base no tipo de tiro
        val equipmentProtection = if (isHeadshot) set.headProtection else set.bodyProtection

        // Calcula o damageMultiplier corretamente
        val damageMultiplier = (if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier * set.entityDmgMult * (1 + set.cyborgDmgBuff)) - equipmentProtection

        // Verifica se o damageMultiplier é negativo
        val effectiveDamageMultiplier = if (damageMultiplier < 0) 0.0 else damageMultiplier

        // Calcula o dano
        var finalDamage = calculateDamage(weapon.damage, effectiveDamageMultiplier, set.absorption, set.resistance, set.weaponTypeResistance)

        // Loop para aplicar o dano até que a saúde chegue a 0 ou menos
        while (remainingHealth > 0) {
            // Calcula a distribuição do dano
            val armorDamage = finalDamage * 0.8
            val healthDamage = finalDamage * 0.2

            if (debug) {
                println("------------------------------")
                println("Tiro $shots:")
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

        println(" - Tiros na Cabeça: ${weapon.ttk.keys.first()}")
        println(" - TTK Cabeça (s): ${"%.3f".format(weapon.ttk.values.first())}")

        println(" - Tiros no Corpo: ${weapon.ttk.keys.elementAt(1)}")
        println(" - TTK Corpo (s): ${"%.3f".format(weapon.ttk.values.elementAt(1))}")

        println(" - Média de Tiros: ${weapon.ttk.keys.last()}")
        println(" - TTK Médio (s): ${"%.3f".format(weapon.ttk.values.last())}")
        println("------------------------------")
    }

    fun findBestTTK(
        weapons: List<Weapon>,
        setStats: Set = Set.sirocco,
        debug: Boolean = false,
    ): Triple<Weapon, Weapon, Weapon> {
        var bestHeadTTK = Double.MAX_VALUE
        lateinit var bestHeadWeapon: Weapon
        var bestBodyTTK = Double.MAX_VALUE
        lateinit var bestBodyWeapon: Weapon
        var bestBulletHead: Int? = null
        var bestBulletBody: Int? = null
        var bestTTKMedia: Double = Double.MAX_VALUE
        lateinit var bestTTKMediaWeapon: Weapon

        for (weapon in weapons) {
            val (bulletHead, ttkHead) = calculateTTKWithProtection(weapon, setStats, true, debug)
            if (ttkHead < bestHeadTTK) {
                bestHeadTTK = ttkHead
                bestHeadWeapon = weapon
                bestBulletHead = bulletHead
            }

            val (bulletBody, ttkBody) = calculateTTKWithProtection(weapon, setStats, false, debug)
            if (ttkBody < bestBodyTTK) {
                bestBodyTTK = ttkBody
                bestBodyWeapon = weapon
                bestBulletBody = bulletBody
            }

            val media = (ttkHead + ttkBody) / 2

            if (media < bestTTKMedia) {
                bestTTKMedia = media
                bestTTKMediaWeapon = weapon
            }
        }

        println("===================================")
        println("Melhor TTK para a Cabeça: ${bestHeadWeapon.name} com TTK de ${"%.3f".format(bestHeadTTK)} segundos em $bestBulletHead tiro(s)")
        println("Melhor TTK para o Corpo: ${bestBodyWeapon.name} com TTK de ${"%.3f".format(bestBodyTTK)} segundos em $bestBulletBody tiros(s)")
        println("Melhor TTK Médio (Cabeça e Corpo): ${bestTTKMediaWeapon.name} com TTK Médio de ${"%.3f".format(bestTTKMedia)} segundos")
        println("===================================")

        return Triple(bestHeadWeapon, bestBodyWeapon, bestTTKMediaWeapon)
    }
}
