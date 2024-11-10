import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TestTest {

    @Test
    fun runTest() {
        assertTrue {
            (Unit as? Any) is Any
        }
    }
}