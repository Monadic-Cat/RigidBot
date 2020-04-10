import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.channel.*

val commands = arrayListOf<Command>();

fun main() {
    println("Setting up...")
    commands.add(Command("ping", "Says pong.", "ping", { args, message ->
        if (args.size != 0) {
            false
        } else {
            replyText(textChannel(message), "Pong!")
            true
        }
    }))
    commands.add(Command("pong", "Says ping.", "pong", { args, message ->
        if (args.size != 0) {
            false
        } else {
            replyText(textChannel(message), "Ping!")
            true
        }
    }))
	val api: DiscordApi = DiscordApiBuilder().setToken(readFile(".env")).login().join()
    api.addMessageCreateListener { e ->
        val message = e.getMessage()
        val content = message.getContent()
        val symbol = "$"
		if (!message.getAuthor().isBotUser() && content.startsWith(symbol) && message.getChannel() is ServerTextChannel) {
            val components = content.substring(symbol.length).split(" ")
            val name = components[0]
            val args = components.drop(1)
            val channel = textChannel(message)
            for (command in commands) {
                if (name == command.name) {
                    if (!command.func(args, message)) {
                        replyText(channel, "Usage: " + command.usage)
                    }
                }
            }
        }
	}
}