import android.widget.RadioGroup
import com.example.myapplication.MainActivity.Companion.prefs
import com.example.myapplication.MemoryCardGameData
import com.example.myapplication.R
import com.example.myapplication.ScavData

val scavDatas = mutableListOf<ScavData>(
    ScavData(name = "paper", score = 10, false),
    ScavData(name = "pen", score = 20, false),
    ScavData(name = "book", score = 30, false),
    ScavData(name = "glasses", score = 40, false),
    ScavData(name = "coin", score = 50, false),
    ScavData(name = "iPhone", score = 50, false),
    ScavData(name = "iPad", score = 50, false),
    ScavData(name = "needle", score = 50, false),
    ScavData(name = "cup", score = 50, false),
    ScavData(name = "towel", score = 50, false),
    ScavData(name = "fork", score = 50, false),
    ScavData(name = "spoon", score = 50, false),
    ScavData(name = "laptop", score = 50, false),
    ScavData(name = "chopsticks", score = 50, false),
    ScavData(name = "eraser", score = 50, false),
)
val memoryCardGameDatas = listOf<MemoryCardGameData>(
    MemoryCardGameData(name = "spade_1", imageID = R.drawable.spade_1, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_2", imageID = R.drawable.spade_2, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_3", imageID = R.drawable.spade_3, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_4", imageID = R.drawable.spade_4, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_5", imageID = R.drawable.spade_5, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_6", imageID = R.drawable.spade_6, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_7", imageID = R.drawable.spade_7, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_8", imageID = R.drawable.spade_8, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_9", imageID = R.drawable.spade_9, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_10", imageID = R.drawable.spade_10, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_jack", imageID = R.drawable.spade_jack, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_queen", imageID = R.drawable.spade_queen, selected = false, invisible = false),
    MemoryCardGameData(name = "spade_king", imageID = R.drawable.spade_king, selected = false, invisible = false),
)

fun setRadioState(flag: Boolean, radioGroup: RadioGroup) {
    for (i in 0 until radioGroup.childCount)
        radioGroup.getChildAt(i).isEnabled = flag
}

fun updateMyBestScore(gameName: String, score: String) {
    val storedScore = prefs.getSharedPrefs(gameName, score)
    if (storedScore.toInt() > score.toInt())
        return
    prefs.setSharedPrefs(gameName, score)
}
