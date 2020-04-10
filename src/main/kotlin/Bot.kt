
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder

fun main() {
	println("Setting up bot.")
	val api: DiscordApi = DiscordApiBuilder().setToken(readFile(".env")).login().join()
	api.addMessageCreateListener {
		e -> println("testing")
	};
	println("Bot has been setup.")
}