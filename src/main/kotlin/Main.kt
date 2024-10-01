package br.com.dhionata

fun main() {
    // Lista de armas do Fuzileiro
    val fuzileiroWeapons = listOf(
        Weapon("AK Alpha", 100, 800, 6.5, 1.0).addMods(6.0, 4),
        Weapon("AK Alpha RAJADA", 100, 800, 6.5, 1.0).addMods(-35.0, 50),
        Weapon("AK Alpha RAJADA", 100, 800, 6.5, 1.0).addMods(-35.0, 50).addMods(6.0, 4),
        Weapon("AK-12", 105, 808, 7.0, 1.25),
        Weapon("Beretta", 111, 810, 4.0, 1.4).addMods(10.0),
        Weapon("Carmel Modificada", 96, 720, 7.0, 1.07).addMods(-27.5, 70, null, 12.0),
        Weapon("Cobalt", 95, 735, 7.0, 1.62),
        Weapon("Kord", 175, 640, 6.0, 1.15).addMods(10.0),
        Weapon("PKM Zenit", 105, 793, 5.5, 1.06),
        Weapon("QBZ", 106, 720, 7.0, 1.12).addMods(8.0),
        Weapon("STK", 110, 825, 4.0, 1.25).addMods(8.0, bodyMultiplierAddPercentage = 13.0),
        Weapon("STK Modificada", 110, 720, 4.0, 1.25).addMods(8.0 - 42.0, 60, headMultiplierAddPercentage = 13.0 - 10.0),
    ).sortedBy { it.ttk.values.last() }

    // Lista de armas do Engenheiro
    val engenheiroWeapons = listOf(
        Weapon("Tavor CTAR-21", 102, 970, 4.0, 1.6).addMods(10.0),
        Weapon("Honey Badger", 128, 785, 6.0, 1.24).addMods(7.0),
        Weapon("Kriss Super V Custom (Mod)", 100, 740, 4.5, 1.1).addMods(5.0, 9).addMods(40.0),
        Weapon("Kriss Super V Custom (Mod)", 100, 740, 4.5, 1.1).addMods(5.0, 16, 20.0).addMods(40.0),
        Weapon("Magpul", 100, 1010, 4.0, 1.42),
        Weapon("Magpul (Mod Cadência)", 100, 1010, 4.0, 1.42).addMods(8.0),
        Weapon("Magpul (Mod Dano Corporal)", 100, 1010, 4.0, 1.42).addMods(bodyMultiplierAddPercentage = 13.0),
        Weapon("Magpul (Ambas Modificações)", 100, 1010, 4.0, 1.42).addMods(8.0, bodyMultiplierAddPercentage = 13.0),
        Weapon("PP-2011", 120, 790, 5.8, 1.12),
        Weapon("PP-2011 (Mod Cadência)", 120, 790, 5.8, 1.12).addMods(6.8),
        Weapon("PP-2011 (Mod Dano Corporal)", 120, 790, 5.8, 1.21).addMods(bodyMultiplierAddPercentage = 8.0),
        Weapon("PP-2011 (Ambas Modificações)", 120, 790, 5.8, 1.21).addMods(6.8, bodyMultiplierAddPercentage = 8.0),
        Weapon("CSV-9 Comodo", 92, 980, 4.8, 1.2).addMods(-13.0, 29, 25.0).addMods(bodyMultiplierAddPercentage = 8.0)
    ).sortedBy { it.ttk.values.last() }

    println("\n==== TTK no corpo ====\n")

    println("\n=== Classe Fuzileiro ===\n")
    fuzileiroWeapons.forEach {
        println(it.toString())
    }

    println("\n=== Classe Engenheiro ===\n")
    engenheiroWeapons.forEach {
        println(it.toString())
    }

    println("\n==== TTK na cabeça ====\n")

    println("\n=== Classe Fuzileiro ===\n")
    fuzileiroWeapons.sortedBy { it.ttk.values.first() }.forEach {
        println(it.toString())
    }

    println("\n=== Classe Engenheiro ===\n")
    engenheiroWeapons.sortedBy { it.ttk.values.first() }.forEach {
        println(it.toString())
    }

    println("\n=== Classe Fuzileiro ===\n")
    val weaponCalculator = WeaponCalculator()
    weaponCalculator.findBestTTK(fuzileiroWeapons)

    println("\n=== Classe Engenheiro ===\n")
    weaponCalculator.findBestTTK(engenheiroWeapons)

}
