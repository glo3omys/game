import android.widget.RadioGroup
import com.example.myapplication.MainActivity.Companion.prefs
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