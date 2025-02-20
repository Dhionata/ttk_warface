import br.com.dhionata.Set
import br.com.dhionata.TTKCalculator
import br.com.dhionata.Weapon
import org.junit.jupiter.api.Test

class TTKCalculatorTest {

    @Test
    fun bulletsToKillWithProtection() {
        Weapon.fuzileiroWeapons.filter { weapon -> weapon.name.startsWith("STK") }.forEach { weapon ->
            TTKCalculator.bulletsToKillWithProtectionInt(
                weapon, Set.sirocco, true, true
            )
        }
    }
}
