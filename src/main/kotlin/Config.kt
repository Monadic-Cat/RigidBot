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

fun guildConfig(id: Long): Tree {
	if (!config.isExists("guilds")) {
		config.putMap("guilds")
	}
	val tree = config.get("guilds")
	if (!tree.isExists("" + id)) {
		tree.putMap("" + id)
	}
	val guild = tree.get("" + id)
	if (!guild.isExists("users")) {
		guild.putMap("users")
	}
	if (!guild.isExists("roles")) {
		guild.putMap("roles")
	}
	if (!guild.isExists("symbol")) {
		guild.put("symbol", "$")
	}
	if (!guild.isExists("extensions")) {
		guild.putList("extensions")
	}
	return guild
}

fun configHandler() {
    (Thread {
        while (true) {
            Thread.sleep(3000)
            saveConfig()
        }
    }).start()
}
