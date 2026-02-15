import br.com.dhionata.TTKCalculator
import br.com.dhionata.weapon.WeaponRepository
import org.junit.jupiter.api.Test

class TTKCalculatorTest {

    // Variável de classe para definir a distância máxima do teste
    private val maxDistance = 30

    @Test
    fun testEngenheiroWeapons() {
        println("=== Testando Armas de Engenheiro ===")
        WeaponRepository.engenheiroWeapons.forEach { weapon ->
            println("\n========================================")
            println("Arma: ${weapon.name}")
            println("========================================")
            for (distance in 0..maxDistance) {
                println("\n--- Distância: ${distance}m ---")
                TTKCalculator.bulletsToKillWithProtectionInt(
                    weapon, debug = true, distance = distance.toDouble()
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
                TTKCalculator.bulletsToKillWithProtectionInt(
                    weapon, debug = true, distance = distance.toDouble()
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
                TTKCalculator.bulletsToKillWithProtectionInt(
                    weapon, debug = true, distance = distance.toDouble()
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
                TTKCalculator.bulletsToKillWithProtectionInt(
                    weapon, debug = true, distance = distance.toDouble()
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
                TTKCalculator.bulletsToKillWithProtectionInt(
                    weapon, debug = true, distance = distance.toDouble()
                )
            }
        }
    }
}
