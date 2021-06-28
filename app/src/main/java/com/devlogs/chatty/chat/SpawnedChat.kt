package com.devlogs.chatty.chat

import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatPresentableModel
import com.devlogs.chatty.screen.chatscreen.chatscreen.model.ChatType
import kotlin.collections.ArrayList
import kotlin.random.Random

val spawnMessage : ArrayList<ChatPresentableModel> = arrayListOf(
//    ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Hello, mấy nay khỏe không", 12328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "lâu quá không gặp", 123284238423),
//    ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Mốt lên đi ún bia", 12329428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "thuylinh@gmail.com", "Ok t cũng nhớ m ghê", 12322428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "thuylinh@gmail.com", "Đi chỗ nào", 12332328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "thuylinh@gmail.com", "Rogue đuy :)) mê chổ đó lắm", 12328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Okay", 12328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Chỗ đó cũng được", 12328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Có mấy loại t cũng chưa thử bao giờ", 12328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Uống xong đi vài vòng Sài gòn chơi, chứ mấy nay nhớ Sài Gòn vl", 12328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "thuylinhp@gmail.com", "Ok m", 12328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "thuylinhp@gmail.com", "Tới với m luôn", 12328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "thuylinhp@gmail.com", "Mà nay m khỏi bao, để t bao cho", 12328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "thuylinhp@gmail.com", "Từ chối quýnh m lìn", 12328428423),
//    ChatPresentableModel("123123", ChatType.TEXT, "tiendvlp@gmail.com", "Đm, đỉnh v, ok lun", 12328428423),
)

fun spawnChat () {
    var startTime = 1612242742000
    val delta = 1000 * 60 * 5
    for (i in 0..400) {
        startTime += delta
        val rand = Random.nextInt(-1,2)
        val sender = if (rand == 0) "tiendvlp@gmail.com" else "thuylinh@gmail.com"
        spawnMessage.add(ChatPresentableModel("$i", ChatType.TEXT, sender, randomMessage(i),  startTime))
    }
}

fun randomMessage (index: Int) : String {
    val length = Random.nextInt(1,50)
    var message = "$index. "

    var randomWord = listOf<String>(
        "Im", "not", "You", "me", "how", "could", "no", "i", "will", "never", "girl friend", "boy", "FPT", "School", "Go to", "Now you know", "!", "?"
    )

    for (i in 1..length) {
        val wordsIndex = Random.nextInt(0,randomWord.size-1)
        message +=  randomWord[wordsIndex] + " ";
    }

    return message
}