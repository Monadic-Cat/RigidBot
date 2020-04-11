import groovy.lang.Binding
import groovy.lang.GroovyShell
import java.awt.Color
import org.javacord.api.DiscordApi
import org.javacord.api.DiscordApiBuilder
import org.javacord.api.entity.channel.*

val commands = arrayListOf<Command>()

fun main() {
	println("Setting up...")
	loadConfig()
	configHandler()

	val api: DiscordApi = DiscordApiBuilder().setToken(readFile(".env")).login().join()
	
	commands.add(Command("config", "Configures the current guild.", listOf("config symbol [text?]"), { args, message ->
		if (args.size == 1 && args[0] == "symbol") {
			val guildid = guildId(message)
			val guild = guildConfig(guildid)
			val symbol = guild.get("symbol").asString()
			replyText(textChannel(message), "The guild symbol is **" + symbol + "**")
			true
		} else if (args.size >= 2 && args[0] == "symbol") {
			val guildid = guildId(message)
			val guild = guildConfig(guildid)
			val text = joinList(args.drop(1), " ")
			guild.put("symbol", text)
			replyText(textChannel(message), "The guild symbol is now **" + text + "**")
			true
		} else {
			false
		}
	}))
	commands.add(Command("help", "Shows this page.", listOf("help"), { args, message ->
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
	commands.add(Command("eval", "Evaluates JavaScript code.", listOf("eval [code...]"), { args, message ->
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
	commands.add(Command("ping", "Says pong.", listOf("ping"), { args, message ->
		if (args.size != 0) {
			false
		} else {
			replyText(textChannel(message), "Pong!")
			true
		}
	}))
	commands.add(Command("pong", "Says ping.", listOf("pong"), { args, message ->
		if (args.size != 0) {
			false
		} else {
			replyText(textChannel(message), "Ping!")
			true
		}
	}))
	commands.add(Command("geval", "Evals groovy code.", listOf("geval <code>"), { args, message ->
		if (api.getOwnerId() == message.getAuthor().getId()) {
			try {
				val sharedData = Binding()
				val shell = GroovyShell(sharedData)
				sharedData.setProperty("message", message)
				val result = shell.evaluate(message.getContent().substring(7))
				replyText(textChannel(message), "" + result)
			} catch (e: Throwable) {
				replyText(textChannel(message), "Error: ```groovy\n" + e.toString() + "\n```")
			}
		} else {
			replyText(textChannel(message), "<@" + message.getAuthor().getId() + ">, you don't own me! 😿")
		}
		true
	}))
	commands.add(Command("purge", "Deletes a specified number of messages up to 100", listOf("purge <number>"), {args, message ->
		if (args.size != 1) {
		    false
		} else {
		    if(!textChannel(message).canManageMessages(api.getYourself())) {
			   replyText(textChannel(message), "Error: I am lacking permissions to do that")
		    } else if (!message.getAuthor().canManageMessagesInTextChannel()) {
			replyText(textChannel(message), "Error: You are lacking permission to delete messages")
		    } else {
			   try {
			       val numberOfMessages = args[0].toInt()
			       
			       if (numberOfMessages < -1 || numberOfMessages > 100) {
				   replyText(textChannel(message), "Error: Can't delete " + numberOfMessages + " messages!")
			       } else {
				   textChannel(message).bulkDelete(textChannel(message).getMessages(numberOfMessages).join())
			       }
			   } catch(e: NumberFormatException) {
		               replyText(textChannel(message), "Error: Invalid number " + args[0])
			   }
		    }
		    true
		}
	}))

	api.addMessageCreateListener { e ->
		val message = e.getMessage()
		if (message.getChannel() is ServerTextChannel) {
			val content = message.getContent()
			val channel = message.getChannel() as ServerTextChannel
			val symbol = guildConfig(channel.getServer().getId()).get("symbol").asString()
			if (!message.getAuthor().isBotUser() && content.startsWith(symbol)) {
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
}
