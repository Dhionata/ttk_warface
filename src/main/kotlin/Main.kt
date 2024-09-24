package br.com.dhionata

data class Weapon(
    val name: String,
    val damage: Double,
    val fireRate: Double, // DPM (disparos por minuto)
    val headMultiplier: Double,
    val bodyMultiplier: Double
)

// Classe para armazenar estatísticas de cada classe
data class ClassStats(
    val name: String,
    val armor: Double,
    val bodyProtection: Double, // Body Protection%
    val headProtection: Double, // Head Protection%
    val legProtection: Double,  // Leg Protection%
    val absorption: Double,    // Absorption (flat value)
    val resistance: Double,    // Resistance (percentage)
    val weaponTypeResistance: Double, // Weapon Type Resistance (percentage)
    val cyborgDmgBuff: Double = 0.0,  // Bonus contra Cyborgs
    val entityDmgMult: Double = 1.0,  // Multiplicador contra entidades específicas
    val hp: Double = 125.0             // HP inicial
)

// Função para calcular o dano ajustado considerando as proteções
fun calculateDamage(
    weaponDamage: Double,
    damageMultiplier: Double,
    absorption: Double, // Flat value
    resistance: Double,
    weaponTypeResistance: Double
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
    cyborgDmgBuff: Double = 0.0
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
    var finalDamage =
        calculateDamage(weapon.damage, effectiveDamageMultiplier, absorption, resistance, weaponTypeResistance)

    // Loop para aplicar o dano até que a saúde chegue a 0 ou menos
    while (remainingHealth > 0) {
        // Calcula a distribuição do dano
        val armorDamage = finalDamage * 0.8
        val healthDamage = finalDamage * 0.2

        println("Tiro $shots:")
        println(" - Dano Total: $finalDamage")
        println(" - Dano à Armadura: $armorDamage")
        println(" - Dano à Saúde: $healthDamage")
        println(" - Armadura Antes do Tiro: $remainingArmor")
        println(" - Saúde Antes do Tiro: $remainingHealth")

        // Aplica o dano à armadura
        remainingArmor -= armorDamage

        // Se a armadura ficar negativa, o dano restante vai para a saúde
        if (remainingArmor < 0) {
            remainingHealth += remainingArmor // remainingArmor é negativo
            remainingArmor = 0.0
        }

        // Aplica o dano à saúde
        remainingHealth -= healthDamage

        println(" - Armadura Após o Tiro: $remainingArmor")
        println(" - Saúde Após o Tiro: $remainingHealth")
        println("------------------------------")

        shots++
    }

    return shots
}

fun calculateTTKWithProtection(
    weapon: Weapon,
    classStats: ClassStats,
    isHeadshot: Boolean
): Pair<Int, Double> { // Retorna (shots, totalTimeSeconds)
    val shotsNeeded = bulletsToKillWithProtection(
        weapon = weapon,
        multiplier = if (isHeadshot) weapon.headMultiplier else weapon.bodyMultiplier,
        armor = classStats.armor,
        health = classStats.hp,
        bodyProtection = classStats.bodyProtection,
        headProtection = classStats.headProtection,
        absorption = classStats.absorption,
        resistance = classStats.resistance,
        weaponTypeResistance = classStats.weaponTypeResistance,
        isHeadshot = isHeadshot,
        entityDmgMult = classStats.entityDmgMult,
        cyborgDmgBuff = classStats.cyborgDmgBuff
    )

    // Calcula o tempo total baseado na taxa de fogo (DPM)
    val shotsPerSecond = weapon.fireRate / 60.0
    val totalTime = shotsNeeded / shotsPerSecond

    return Pair(shotsNeeded, totalTime)
}

fun printWeaponTTKWithProtection(
    weapon: Weapon,
    classStats: ClassStats
) {
    println("Classe: ${classStats.name}")
    println("Arma: ${weapon.name}")

    // Calcula para tiro na cabeça
    val (shotsHead, ttkHead) = calculateTTKWithProtection(weapon, classStats, isHeadshot = true)
    println(" - Tiros na Cabeça: $shotsHead")
    println(" - TTK Cabeça (s): ${"%.3f".format(ttkHead)}")

    // Calcula para tiro no corpo
    val (shotsBody, ttkBody) = calculateTTKWithProtection(weapon, classStats, isHeadshot = false)
    println(" - Tiros no Corpo: $shotsBody")
    println(" - TTK Corpo (s): ${"%.3f".format(ttkBody)}")
    println("------------------------------")
}

fun findBestTTK(
    weapons: List<Weapon>,
    classStats: ClassStats
) {
    var bestHeadTTK = Double.MAX_VALUE
    var bestHeadWeapon: Weapon? = null
    var bestBodyTTK = Double.MAX_VALUE
    var bestBodyWeapon: Weapon? = null
    var bestBulletHead: Int? = null
    var bestBulletBody: Int? = null

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
    }

    println(
        "Melhor TTK para a Cabeça: ${bestHeadWeapon?.name} com TTK de ${"%.3f".format(bestHeadTTK)} segundos em " +
                "$bestBulletHead tiro(s)"
    )
    println(
        "Melhor TTK para o Corpo: ${bestBodyWeapon?.name} com TTK de ${"%.3f".format(bestBodyTTK)} segundos em " +
                "$bestBulletBody tiros(s)"
    )
    println("===================================")
}

fun main() {
    // Definição das classes com seus bônus e proteções
    val fuzileiroStats = ClassStats(
        name = "Fuzileiro",
        armor = 500.0, // 300 do colete + 200 do conjunto
        bodyProtection = 0.15, // 15%
        headProtection = 0.25, // 25%
        legProtection = 0.25,  // 25%
        absorption = 15.0,     // Absorption flat
        resistance = 0.0,      // Sem resistências adicionais
        weaponTypeResistance = 0.0, // Sem resistências específicas
        cyborgDmgBuff = 0.0,
        entityDmgMult = 1.0,
        hp = 125.0
    )

    // Lista de armas do Fuzileiro
    val fuzileiroWeapons = listOf(
        Weapon("AK Alpha", 100.0, 848.0, 6.5, 1.0),
        Weapon("AK-12", 105.0, 808.0, 7.0, 1.25),
        Weapon("Beretta", 111.0, 891.0, 4.0, 1.4),
        Weapon("Carmel Modificada", 166.0, 553.0, 7.0, 1.23),
        Weapon("Cobalt", 95.0, 800.0, 7.0, 1.62),
        Weapon("Kord", 175.0, 640.0 * 1.1, 6.0, 1.15),
        Weapon("PKM Zenit", 105.0, 793.0, 5.5, 1.06),
        Weapon("QBZ", 106.0, 778.0, 7.0, 1.12),
        Weapon("STK", 110.0, 891.0, 4.0, 1.41),
        Weapon("STK Modificada", 170.0, 510.0, 4.0, 1.27),
        Weapon("Tavor CTAR-21", 102.0, 970.0 * 1.1, 4.0, 1.6)
    ).sortedBy { it.name }

    // Lista de armas do Engenheiro (exemplo, pode ser ajustado conforme necessário)
    val engenheiroWeapons = listOf(
        Weapon("Honey Badger", 128.0, 863.5, 6.0, 1.24),
        Weapon("Kriss Super V Custom (Mod)", 109.0, 740.0 * 1.45, 4.5, 1.1),
        Weapon("Magpul", 100.0, 1010.0, 4.0, 1.42),
        Weapon("Magpul (Mod Cadência)", 100.0, 1010.0 * 1.08, 4.0, 1.42),
        Weapon("Magpul (Mod Dano Corporal)", 100.0, 1010.0, 4.0, 1.60),
        Weapon("Magpul (Ambas Modificações)", 100.0, 1010.0 * 1.08, 4.0, 1.60),
        Weapon("PP-2011", 120.0, 790.0, 5.8, 1.12),
        Weapon("PP-2011 (Mod Cadência)", 120.0, 790.0 * 1.068, 5.8, 1.12),
        Weapon("PP-2011 (Mod Dano Corporal)", 120.0, 790.0, 5.8, 1.21),
        Weapon("PP-2011 (Ambas Modificações)", 120.0, 790.0 * 1.068, 5.8, 1.21)
    ).sortedBy { it.name }

    // Calcula e exibe o TTK para o Fuzileiro
    println("=== Classe Fuzileiro ===")
    fuzileiroWeapons.forEach { weapon ->
        printWeaponTTKWithProtection(weapon, fuzileiroStats)
    }

    // Calcula e exibe o TTK para o Fuzileiro
    println("=== Classe Engenheiro ===")
    engenheiroWeapons.forEach { weapon ->
        printWeaponTTKWithProtection(weapon, fuzileiroStats)
    }

    findBestTTK(fuzileiroWeapons, fuzileiroStats)
    findBestTTK(engenheiroWeapons, fuzileiroStats)
}
