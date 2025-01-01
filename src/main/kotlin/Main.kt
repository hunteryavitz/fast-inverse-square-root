import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.system.measureNanoTime

fun fastInverseSqrt(number: Float): Float {
    var i: Int
    val threeHalves = 1.5f

    val x2 = number * 0.5f
    var y = number

    // convert float bits to integer
    i = java.lang.Float.floatToRawIntBits(y)

    // magic constant and bit manipulation
    i = 0x5f3759df - (i shr 1)

    // convert integer bits back to float
    y = java.lang.Float.intBitsToFloat(i)

    // Newton-Raphson iteration
    y *= (threeHalves - (x2 * y * y))

    return y
}

@Composable
@Preview
fun App() {
    var input by remember { mutableStateOf("4.0") }
    var result by remember { mutableStateOf("") }
    var exactResult by remember { mutableStateOf("") }
    var error by remember { mutableStateOf("") }
    var fastTime by remember { mutableStateOf("") }
    var exactTime by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Fast Inverse Square Root", modifier = Modifier.padding(8.dp))

        BasicTextField(
            value = input,
            onValueChange = { input = it },
            modifier = Modifier
                .padding(8.dp)
                .width(200.dp)
                .background(color = androidx.compose.ui.graphics.Color.LightGray)
        )

        Button(onClick = {
            val number = input.toFloatOrNull() ?: return@Button

            // measure performance of fast inverse sqrt
            val fastTimeTaken = measureNanoTime {
                val fastResult = fastInverseSqrt(number)
                result = "Fast Approximation: $fastResult"
            }

            // measure performance of exact inverse sqrt
            val exactTimeTaken = measureNanoTime {
                val exact = 1.0f / sqrt(number)
                exactResult = "Exact Result: $exact"
                error = "Error: ${abs(1.0f / sqrt(number) - fastInverseSqrt(number))}"
            }

            fastTime = "Fast Approximation Time: $fastTimeTaken ns"
            exactTime = "Exact Calculation Time: $exactTimeTaken ns"
        }) {
            Text("Calculate")
        }

        // display results
        Spacer(modifier = Modifier.height(16.dp))
        Text("Results")
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(result)
            Text(exactResult)
            Text(error)
        }

        // display performance
        Spacer(modifier = Modifier.height(16.dp))
        Text("Performance")
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(fastTime)
            Text(exactTime)
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
