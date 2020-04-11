import io.datatree.Tree

var config = Tree("{}")
fun loadConfig() {
	val contents = readFile("config.json")
	if (!contents.isEmpty()) {
		config = Tree(contents)
	}
}
fun saveConfig() {
	writeFile("config.json", config.toString())
}

fun configHandler() {
	(Thread {
		while(true) {
			Thread.sleep(3000)
			saveConfig()
		}
	}).start()
}
