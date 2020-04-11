import java.awt.Color
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.channel.*

val commands = arrayListOf<Command>()

fun main() {
    println("Setting up...")
    loadConfig()
    configHandler()
    commands.add(Command("help", "Shows this page.", listOf("help"),
    { args, message ->
              if (args.size != 0) {
          false
              } else {
          val list = arrayListOf<String>()
          for (command in commands) {
                      list.add("** - " + command.name + ":** " + command.desc)
          }
          replyPage(message, "***Commands***", list, Color(255, 0, 255))
          true
              }
    }))
    commands.add(Command("eval", "Evaluates JavaScript code.", listOf("eval [code...]"),
    { args, message ->
              if (args.size == 0) {
          false
              } else {
          (Thread {
                       try {
               replyText(textChannel(message), "Result: " + evalTimed(joinList(args, " "), 1000L))
                       } catch (e: Throwable) {
               replyText(textChannel(message), "Error: " + e)
                       }
          }).start()
          true
              }
    }))
    commands.add(Command("ping", "Says pong.", listOf("ping"),
    { args, message ->
              if (args.size != 0) {
          false
              } else {
          replyText(textChannel(message), "Pong!")
          true
              }
    }))
    commands.add(Command("pong", "Says ping.", listOf("pong"),
    { args, message ->
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
            for (command in commands) {
                if (name == command.name) {
                    if (!command.func(args, message)) {
                        replyPage(message, "***Usages***", command.usage, Color.ORANGE)
                    }
                }
            }
        }
    }
}
