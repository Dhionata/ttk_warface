import br.com.dhionata.Set
import br.com.dhionata.TTKCalculator
import br.com.dhionata.Weapon
import org.junit.jupiter.api.Test

class TTKCalculatorTest {

    @Test
    fun bulletsToKillWithProtection() {
        val list = Weapon.fuzileiroWeapons.filter { weapon -> weapon.name.startsWith("STK") }
        list.forEach {
            TTKCalculator.bulletsToKillWithProtection(
                it, Set.sirocco, true, true
            )
        }
    }
}
