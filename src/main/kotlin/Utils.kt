
import java.io.File

fun readFile(name: String): String {
	return File(name).readText(Charsets.UTF_8)
}
fun writeFile(name: String, text: String) {
	File(name).writeText(text)
}
