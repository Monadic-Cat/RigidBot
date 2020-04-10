import java.io.File
import org.javacord.api.entity.channel.*
import org.javacord.api.entity.message.Message

fun readFile(name: String): String {
	return File(name).readText(Charsets.UTF_8)
}
fun writeFile(name: String, text: String) {
	File(name).writeText(text)
}

fun textChannel(message: Message): ServerTextChannel {
    return message.getChannel().asServerTextChannel().get()
}
fun replyText(channel: ServerTextChannel, text: String) {
    channel.sendMessage(text)
}
