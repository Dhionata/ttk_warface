package br.com.dhionata

import kotlin.math.roundToInt

object TTKCalculator {

    // Calcula o dano total (após absorção, resistência e weaponTypeResistance)
    // Retorna um valor inteiro, arredondado para o inteiro mais próximo.
    fun calculateDamageInt(
        weaponDamage: Int,
        damageMultiplier: Double,
        absorption: Double, // Valor fixo de absorção
        pellets: Int = 1,   // Número de pellets (para dividir a absorção se necessário)
        resistance: Double, // Percentual de resistência (0.0 a 1.0)
        weaponTypeResistance: Double, // Percentual de resistência específica ao tipo de arma
    ): Int {
        val effectiveAbsorption = absorption / pellets
        val baseDamage = weaponDamage * damageMultiplier - effectiveAbsorption
        if (baseDamage <= 0) return 0  // Se o dano base for menor ou igual a zero, ignora o tiro.
        val finalDamage = baseDamage * (1 - resistance) * (1 - weaponTypeResistance)
        return finalDamage.roundToInt()  // Arredonda o dano final para um inteiro.
    }

    // Calcula quantos tiros são necessários para zerar a armadura e o HP do alvo,
    // considerando que HP e ARMOR são valores inteiros e que o dano é distribuído (80% na armadura e 20% no HP).
    fun bulletsToKillWithProtectionInt(
        weapon: Weapon,
        set: Set,
        isHeadshot: Boolean = false,
        debug: Boolean = false,
    ): Int {
        var remainingArmor = set.armor
        var remainingHealth = set.hp
        var shots = 0

        // Seleciona a proteção correta: usa headProtection para headshots e bodyProtection para tiros no corpo.
        val equipmentProtection = if (isHeadshot) set.headProtection else set.bodyProtection

        // Calcula o multiplicador de dano (aplicando, se existir, EntityDmgMult e CyborgDmgBuff)
        val damageMultiplier = ((if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier) * set.entityDmgMult * (1 + set.cyborgDmgBuff)) - equipmentProtection

        // Calcula o dano total do tiro (já arredondado para inteiro)
        val finalDamage = calculateDamageInt(
            weapon.damage, damageMultiplier, set.absorption, pellets = 1, // Supondo 1 pellet; se o arma disparar mais, altere esse valor.
            resistance = set.resistance, weaponTypeResistance = set.weaponTypeResistance
        )

        // Distribui o dano: 80% afeta a armadura e 20% o HP.
        // Para garantir que a soma seja igual ao dano total, calculamos um e subtraímos dele o outro.
        val armorDamage = (finalDamage * 80) / 100
        val healthDamage = finalDamage - armorDamage

        // Aplica o dano em loop até que o HP seja zerado.
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
            // Aplica dano à armadura.
            remainingArmor -= armorDamage
            if (remainingArmor < 0) {
                // Se o dano exceder a armadura, o excesso é aplicado ao HP.
                remainingHealth += remainingArmor  // remainingArmor é negativo aqui.
                remainingArmor = 0
            }
            // Aplica dano ao HP.
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

    /* Calcula o Time-To-Kill (TTK) em segundos, considerando a fireRate da arma (em disparos por minuto).
    Retorna um Pair onde o primeiro elemento é o número de tiros e o segundo o tempo total em segundos. */
    fun calculateTTKWithProtectionInt(
        weapon: Weapon,
        set: Set = Set.sirocco,
        isHeadshot: Boolean,
        debug: Boolean = false,
    ): Pair<Int, Double> {
        val shotsNeeded = bulletsToKillWithProtectionInt(weapon, set, isHeadshot, debug)
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
        set: Set = Set.sirocco,
        debug: Boolean = false,
    ): Triple<Weapon, Weapon, Weapon> {
        val bestHeadWeapon = weapons.minBy { it.ttk.first().second }
        val bestBodyWeapon = weapons.minBy { it.ttk.elementAt(1).second }
        val bestTTKMediaWeapon = weapons.minBy { it.ttk.last().second }

        println("===================================")
        println(
            "Melhor TTK para a Cabeça: ${bestHeadWeapon.name} com TTK de ${"%.3f".format(bestHeadWeapon.ttk.first().second)} segundos em ${
                bulletsToKillWithProtectionInt(bestHeadWeapon, set, true, debug)
            } tiro(s)"
        )
        println(
            "Melhor TTK para o Corpo: ${bestBodyWeapon.name} com TTK de ${"%.3f".format(bestBodyWeapon.ttk.elementAt(1).second)} segundos em ${
                bulletsToKillWithProtectionInt(bestBodyWeapon, set, debug = debug)
            } tiros(s)"
        )
        println(
            "Melhor TTK Médio (Cabeça e Corpo): ${bestTTKMediaWeapon.name} com TTK Médio de ${"%.3f".format(bestTTKMediaWeapon.ttk.last().second)} segundos em ${
                (bulletsToKillWithProtectionInt(
                    bestTTKMediaWeapon, set, true, debug
                ) + bulletsToKillWithProtectionInt(bestTTKMediaWeapon, set, debug = debug)) / 2
            } tiro(s)"
        )
        println("===================================")

        return Triple(bestHeadWeapon, bestBodyWeapon, bestTTKMediaWeapon)
    }
}
