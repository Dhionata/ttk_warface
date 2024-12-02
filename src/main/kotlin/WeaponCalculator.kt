package br.com.dhionata

class WeaponCalculator {

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
        multiplier: Double,
        armor: Double,
        health: Double,
        bodyProtection: Double,
        headProtection: Double,
        absorption: Double,
        resistance: Double = 0.0,
        weaponTypeResistance: Double = 0.0,
        isHeadshot: Boolean = false,
        entityDmgMult: Double = 1.0,
        cyborgDmgBuff: Double = 0.0,
    ): Int {
        var remainingArmor = armor
        var remainingHealth = health
        var shots = 0

        // Selecionar a proteção apropriada com base no tipo de tiro
        val equipmentProtection = if (isHeadshot) headProtection else bodyProtection

        // Calcula o damageMultiplier corretamente
        val damageMultiplier = (multiplier * entityDmgMult * (1 + cyborgDmgBuff)) - equipmentProtection

        // Verifica se o damageMultiplier é negativo
        val effectiveDamageMultiplier = if (damageMultiplier < 0) 0.0 else damageMultiplier

        // Calcula o dano
        var finalDamage = calculateDamage(weapon.damage, effectiveDamageMultiplier, absorption, resistance, weaponTypeResistance)

        // Loop para aplicar o dano até que a saúde chegue a 0 ou menos
        while (remainingHealth > 0) {
            // Calcula a distribuição do dano
            val armorDamage = finalDamage * 0.8
            val healthDamage = finalDamage * 0.2

            /* println("Tiro $shots:")
            println(" - Dano Total: $finalDamage")
            println(" - Dano à Armadura: $armorDamage")
            println(" - Dano à Saúde: $healthDamage")
            println(" - Armadura Antes do Tiro: $remainingArmor")
            println(" - Saúde Antes do Tiro: $remainingHealth") */

            // Aplica o dano à armadura
            remainingArmor -= armorDamage

            // Se a armadura ficar negativa, o dano restante vai para a saúde
            if (remainingArmor < 0) {
                remainingHealth += remainingArmor // remainingArmor é negativo
                remainingArmor = 0.0
            }

            // Aplica o dano à saúde
            remainingHealth -= healthDamage

            /*  println(" - Armadura Após o Tiro: $remainingArmor")
             println(" - Saúde Após o Tiro: $remainingHealth")
             println("------------------------------") */

            shots++
        }

        return shots
    }


    fun calculateTTKWithProtection(
        weapon: Weapon,
        classStats: ClassStats = ClassStats.FuzileiroStats,
        isHeadshot: Boolean
    ): Pair<Int, Double> { // Retorna (shots, totalTimeSeconds)
        val shotsNeeded = bulletsToKillWithProtection(
            weapon,
            if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier,
            classStats.armor,
            classStats.hp,
            classStats.bodyProtection,
            classStats.headProtection,
            classStats.absorption,
            classStats.resistance,
            classStats.weaponTypeResistance,
            isHeadshot,
            classStats.entityDmgMult,
            classStats.cyborgDmgBuff
        )

        // Calcula o tempo total baseado na taxa de fogo (DPM)
        val shotsPerSecond = weapon.fireRate / 60.0
        val totalTime = shotsNeeded / shotsPerSecond

        return Pair(shotsNeeded, totalTime)
    }

    fun printWeaponTTKWithProtection(
        weapon: Weapon,
        classStats: ClassStats = ClassStats.FuzileiroStats,
    ) {
        println("Classe: ${classStats.name}")
        println("Arma: ${weapon.name}")

        // Calcula para tiro na cabeça
        val (shotsHead, ttkHead) = calculateTTKWithProtection(weapon, classStats, isHeadshot = true)
        println(" - Tiros na Cabeça: $shotsHead")
        println(" - TTK Cabeça (s): ${"%.3f".format(ttkHead)}")
        weapon.ttk.put(shotsHead, ttkHead)

        // Calcula para tiro no corpo
        val (shotsBody, ttkBody) = calculateTTKWithProtection(weapon, classStats, isHeadshot = false)
        println(" - Tiros no Corpo: $shotsBody")
        println(" - TTK Corpo (s): ${"%.3f".format(ttkBody)}")

        val ttkMedia = ttkBody + ttkHead / 2.0
        println(" - TTK Médio: ${"%.3f".format(ttkMedia)}")
        println("------------------------------")
        weapon.ttk.put(shotsBody, ttkBody)
    }

    fun findBestTTK(
        weapons: List<Weapon>,
        classStats: ClassStats = ClassStats.FuzileiroStats,
    ): Triple<Weapon?, Weapon?, Weapon?> {
        var bestHeadTTK = Double.MAX_VALUE
        lateinit var bestHeadWeapon: Weapon
        var bestBodyTTK = Double.MAX_VALUE
        lateinit var bestBodyWeapon: Weapon
        var bestBulletHead: Int? = null
        var bestBulletBody: Int? = null
        var bestTTKMedia: Double = Double.MAX_VALUE
        lateinit var bestTTKMediaWeapon: Weapon

        for (weapon in weapons) {
            val (bulletHead, ttkHead) = calculateTTKWithProtection(weapon, classStats, isHeadshot = true)
            if (ttkHead < bestHeadTTK) {
                bestHeadTTK = ttkHead
                bestHeadWeapon = weapon
                bestBulletHead = bulletHead
            }

            val (bulletBody, ttkBody) = calculateTTKWithProtection(weapon, classStats, isHeadshot = false)
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
        println("Melhor TTK Médio (Cabeça e Corpo): ${bestTTKMediaWeapon?.name} com TTK Médio de ${"%.3f".format(bestTTKMedia)} segundos")
        println("===================================")

        return Triple(bestHeadWeapon, bestBodyWeapon, bestTTKMediaWeapon)
    }
}
