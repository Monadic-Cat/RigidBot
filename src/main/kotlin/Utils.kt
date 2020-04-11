import delight.nashornsandbox.*
import java.awt.Color
import java.io.File
import java.util.concurrent.*
import org.javacord.api.entity.channel.*
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.embed.EmbedBuilder
import org.javacord.api.event.*
import org.javacord.api.event.message.reaction.*

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

fun clamp(text: String, a: Int, b: Int): String {
    var operating = text
    if (operating.length > b) {
        operating = operating.substring(0, b)
    } else if (operating.length < a) {
        operating += repeat("_", a - operating.length)
    }
    return operating
}
fun repeat(text: String, times: Int): String {
    var res = ""
    for (index: Int in 0..times) {
        res += text
    }
    return res
}

fun replyText(channel: ServerTextChannel, text: String) {
    channel.sendMessage(text)
}
fun replyPage(origin: Message, title: String, items: List<String>, color: Color) {
    replyPage(origin, title, items, 1, "", "", 10, 2048, color, null)
}
fun replyPage(origin: Message, title: String, items: List<String>, page: Int, prefix: String, postfix: String, pageItems: Int, pageChars: Int, color: Color, message: Message?) {
    val channel = origin.getChannel()
    val header = title
    var currentPage = 1
    var currentItem = 0
    var content = ""
    var result = ""
    var i = 0
    while (i < items.indices.count()) {
        val item = items[i]
        currentItem++
        if (currentItem > pageItems) {
            i--
            if (currentPage == page) {
                result = content
            }
            content = ""
            currentPage++
            currentItem = 0
          } else {
            val temp = "\n" + prefix + item + postfix
            if ((content + temp).length > pageChars) {
                i--
                  if (currentPage == page) {
                    result = content
                  }
                  content = ""
                  currentPage++
                  currentItem = 0
            } else {
                content += temp
            }
        }
        i++
    }
    val embed = EmbedBuilder()
    embed.setTitle(clamp(header + ": _" + page + " / " + currentPage + "_", 0, 256))
    if (result.isEmpty()) {
        result = content
    }
    embed.setDescription(if (result.isEmpty()) "" else result.substring(1, result.length))
    embed.setColor(color)
    val pageHolder = intArrayOf(page)
    val pageCount = currentPage
    val pageControls = { msg: Message ->
        val modifierHook = { e: ReactionEvent ->
            val user = if (e is ReactionAddEvent) e.getUser() else (e as ReactionRemoveEvent).getUser()
            if (!(!origin.getAuthor().asUser().isPresent() || origin.getAuthor().asUser().get() !== user)) {
                val emoji = if (e is ReactionAddEvent) e.getEmoji() else (e as ReactionRemoveEvent).getEmoji()
                emoji.asUnicodeEmoji().ifPresent({ text ->
                    if (text.equals("\u25C0")) {
                        pageHolder[0] -= 1
                        if (pageHolder[0] > pageCount) {
                            pageHolder[0] = pageCount
                        }
                        if (pageHolder[0] < 1) {
                            pageHolder[0] = 1
                        }
                        replyPage(origin, title, items, pageHolder[0], prefix, postfix, pageItems, pageChars, color, msg)
                    } else if (text.equals("\u25B6")) {
                        pageHolder[0] += 1
                        if (pageHolder[0] > pageCount) {
                            pageHolder[0] = pageCount
                        }
                        if (pageHolder[0] < 1) {
                            pageHolder[0] = 1
                        }
                        replyPage(origin, title, items, pageHolder[0], prefix, postfix, pageItems, pageChars, color, msg)
                    } else if (text.equals("\u274C")) {
                        msg.delete()
                    } else if (text.equals("\u23EA")) {
                        pageHolder[0] -= 10
                        if (pageHolder[0] > pageCount) {
                            pageHolder[0] = pageCount
                        }
                        if (pageHolder[0] < 1) {
                            pageHolder[0] = 1
                        }
                        replyPage(origin, title, items, pageHolder[0], prefix, postfix, pageItems, pageChars, color, msg)
                    } else if (text.equals("\u23E9")) {
                        pageHolder[0] += 10
                        if (pageHolder[0] > pageCount) {
                            pageHolder[0] = pageCount
                        } else if (pageHolder[0] < 1) {
                            pageHolder[0] = 1
                        }
                        replyPage(origin, title, items, pageHolder[0], prefix, postfix, pageItems, pageChars, color, msg)
                    }
                })
            }
        }
        msg.addReactionAddListener({ e: ReactionAddEvent -> modifierHook(e) })
        msg.addReactionRemoveListener({ e: ReactionRemoveEvent -> modifierHook(e) })
    }
    if (message == null) {
        channel.sendMessage(embed).thenApply({ msg ->
            msg.addReactions("\u23EA", "\u25C0", "\u274C", "\u25B6", "\u23E9")
            pageControls(msg)
        })
    } else {
        message.edit(embed)
    }
}

fun newSandbox(time: Long, memory: Long): NashornSandbox {
    val sandbox = NashornSandboxes.create()
    if (time >= 0) sandbox.setMaxCPUTime(time)
    if (memory >= 0) sandbox.setMaxMemory(memory)
    sandbox.setExecutor(Executors.newSingleThreadExecutor())
    return sandbox
}
fun evalTimed(code: String, time: Long): String {
    return newSandbox(time, -1L).eval(code).toString()
}

fun joinList(list: List<String>, separator: String): String {
    return list.joinToString(separator)
}
