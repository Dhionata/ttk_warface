package br.com.dhionata

object WeaponPresenter {

    fun printDetailedAllWeaponsInfo(fuzileiroWeapons: List<Weapon>, engenheiroWeapons: List<Weapon>, pistolas: List<Weapon>, set: Set, debug: Boolean = false) {
        println("\n=== Detalhes para o conjunto ===\n\n==== ${set.name} ====")

        println("\n==== TTK no corpo ====")

        println("\n=== Classe Fuzileiro ===\n")
        fuzileiroWeapons.sortedBy { it.ttk.elementAt(1).second }.forEach { println(it) }

        println("\n=== Classe Engenheiro ===\n")
        engenheiroWeapons.sortedBy { it.ttk.elementAt(1).second }.forEach { println(it) }

        println("\n=== Pistolas ===\n")
        pistolas.sortedBy { it.ttk.elementAt(1).second }.forEach { println(it) }

        println("\n==== TTK na cabeça ====")

        println("\n=== Classe Fuzileiro ===\n")
        fuzileiroWeapons.sortedBy { it.ttk.first().second }.forEach { println(it) }

        println("\n=== Classe Engenheiro ===\n")
        engenheiroWeapons.sortedBy { it.ttk.first().second }.forEach { println(it) }

        println("\n=== Pistolas ===\n")
        pistolas.sortedBy { it.ttk.first().second }.forEach { println(it) }

        println("\n==== Média do TTK por Arma ====")

        println("\n=== Classe Fuzileiro ===\n")
        fuzileiroWeapons.sortedBy { it.ttk.last().second }.forEach { println(it) }

        println("\n=== Classe Engenheiro ===\n")
        engenheiroWeapons.sortedBy { it.ttk.last().second }.forEach { println(it) }

        println("\n=== Pistolas ===\n")
        pistolas.sortedBy { it.ttk.last().second }.forEach { println(it) }

        println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK na cabeça ====\n")
        (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk.first().second }.forEach { println(it) }

        println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK no corpo ====\n")
        (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk.elementAt(1).second }.forEach { println(it) }

        println("\n=== Fuzileiro + Engenheiro ===\n\n==== TTK Médio ====\n")
        (fuzileiroWeapons + engenheiroWeapons).sortedBy { it.ttk.last().second }.forEach { println(it) }

        println("\n=== Melhores TTKs ===")

        println("\n=== Classe Fuzileiro ===\n")
        TTKCalculator.findBestTTK(fuzileiroWeapons, set, debug).toList().forEach { TTKCalculator.printWeaponTTKWithProtection(it, set) }

        println("\n=== Classe Engenheiro ===\n")
        TTKCalculator.findBestTTK(engenheiroWeapons, set, debug).toList().forEach { TTKCalculator.printWeaponTTKWithProtection(it, set) }

    }
}
