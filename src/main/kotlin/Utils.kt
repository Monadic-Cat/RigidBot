import java.io.File
import org.javacord.api.entity.channel.*
import org.javacord.api.entity.message.Message

fun readFile(name: String): String {
	val file = File(name)
	if (!file.exists()) file.createNewFile()
	return file.readText(Charsets.UTF_8).trim()
}
fun writeFile(name: String, text: String) {
	val file = File(name)
	if (!file.exists()) file.createNewFile()
	file.writeText(text)
}

fun textChannel(message: Message): ServerTextChannel {
	return message.getChannel().asServerTextChannel().get()
}
fun replyText(channel: ServerTextChannel, text: String) {
	channel.sendMessage(text)
}
