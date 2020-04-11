import org.javacord.api.entity.message.Message

class Command(
	val name: String,
	val desc: String,
	val usage: List<String>,
	val func: (List<String>, Message) -> Boolean
) {}
