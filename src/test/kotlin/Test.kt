import br.com.dhionata.Set
import br.com.dhionata.TTKCalculator
import br.com.dhionata.Weapon
import org.junit.jupiter.api.Test

class Test {

    @Test
    fun test() {
        TTKCalculator.bulletsToKillWithProtection(Weapon.pistolas.find { it.name == "Taurus Judge (Mod Cadência)" }!!, Set.sirocco, debug = true)
    }
}
