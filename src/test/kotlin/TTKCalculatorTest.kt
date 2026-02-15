import br.com.dhionata.Set
import br.com.dhionata.calculator.DamageCalculator
import br.com.dhionata.calculator.TTKCalculator
import br.com.dhionata.weapon.WeaponRepository
import org.junit.jupiter.api.Test

class TTKCalculatorTest {

    // Variável de classe para definir a distância máxima do teste
    private val maxDistance = 30
    private val defaultSet = Set.SetsAndEnemy.Sirocco

    // Instanciando o calculador padrão
    private val calculator = TTKCalculator(DamageCalculator())

    @Test
    fun testEngenheiroWeapons() {
        println("=== Testando Armas de Engenheiro ===")
        WeaponRepository.engenheiroWeapons.forEach { weapon ->
            println("\n========================================")
            println("Arma: ${weapon.name}")
            println("========================================")
            for (distance in 0..maxDistance) {
                println("\n--- Distância: ${distance}m ---")
                calculator.calculateTTK(
                    weapon, defaultSet, isHeadshot = false, debug = true, distance = distance.toDouble()
                )
            }
        }
    }

    @Test
    fun testFuzileiroWeapons() {
        println("=== Testando Armas de Fuzileiro ===")
        WeaponRepository.fuzileiroWeapons.forEach { weapon ->
            println("\n========================================")
            println("Arma: ${weapon.name}")
            println("========================================")
            for (distance in 0..maxDistance) {
                println("\n--- Distância: ${distance}m ---")
                calculator.calculateTTK(
                    weapon, defaultSet, isHeadshot = false, debug = true, distance = distance.toDouble()
                )
            }
        }
    }

    @Test
    fun testMedicWeapons() {
        println("=== Testando Armas de Médico ===")
        WeaponRepository.medicWeapons.forEach { weapon ->
            println("\n========================================")
            println("Arma: ${weapon.name}")
            println("========================================")
            for (distance in 0..maxDistance) {
                println("\n--- Distância: ${distance}m ---")
                calculator.calculateTTK(
                    weapon, defaultSet, isHeadshot = false, debug = true, distance = distance.toDouble()
                )
            }
        }
    }

    @Test
    fun testSniperWeapons() {
        println("=== Testando Armas de Sniper ===")
        WeaponRepository.sniperWeapons.forEach { weapon ->
            println("\n========================================")
            println("Arma: ${weapon.name}")
            println("========================================")
            for (distance in 0..maxDistance) {
                println("\n--- Distância: ${distance}m ---")
                calculator.calculateTTK(
                    weapon, defaultSet, isHeadshot = false, debug = true, distance = distance.toDouble()
                )
            }
        }
    }

    @Test
    fun testPistolas() {
        println("=== Testando Pistolas ===")
        WeaponRepository.pistolas.forEach { weapon ->
            println("\n========================================")
            println("Arma: ${weapon.name}")
            println("========================================")
            for (distance in 0..maxDistance) {
                println("\n--- Distância: ${distance}m ---")
                calculator.calculateTTK(
                    weapon, defaultSet, isHeadshot = false, debug = true, distance = distance.toDouble()
                )
            }
        }
    }
}
