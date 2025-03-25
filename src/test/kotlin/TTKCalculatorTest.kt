import br.com.dhionata.Set
import br.com.dhionata.TTKCalculator
import br.com.dhionata.Weapon
import org.junit.jupiter.api.Test

class TTKCalculatorTest {

    @Test
    fun bulletsToKillWithProtection() {
        Weapon.engenheiroWeapons.filter { weapon -> weapon.name.startsWith("PP") }.forEach { weapon ->
            TTKCalculator.bulletsToKillWithProtectionInt(
                weapon, Set.sirocco, false, true
            )
        }
    }
}
