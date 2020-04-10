class Command(
    val name: String,
    val desc: String,
    val usage: String,
    val func: (args: Array<String>) -> Boolean
) {}