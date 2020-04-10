
import java.io.File

fun readFile(name: String): String = File(name).readText(Charsets.UTF_8)
