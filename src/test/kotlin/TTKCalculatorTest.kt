import br.com.dhionata.TTKCalculator
import br.com.dhionata.weapon.WeaponRepository
import org.junit.jupiter.api.Test

class TTKCalculatorTest {

    @Test
    fun bulletsToKillWithProtection() {
        WeaponRepository.engenheiroWeapons.filter { weapon -> weapon.name.startsWith("PP") }.forEach { weapon ->
            TTKCalculator.bulletsToKillWithProtectionInt(
                weapon, debug = true
            )
        }
    }
}
