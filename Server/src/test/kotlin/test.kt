import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import tk.q11mk.nextId
import tk.q11mk.schedule.Schedule
import tk.q11mk.utils.getSecretProperty

fun main() {
    /*val mod = getSecretProperty("id_modulo").toLong()
    var i: Long
    var c = 0
    while (true) {
        ++c
        i = nextId()
        if (i >= 10_000_000_000) {
            println("=== Count: $c ===")
            break
        }
        println("$i (${i % mod})")
    }*/
    //println(Json.encodeToString(exampleSchedule))
}

/*fun main() {
    val schedule = Schedule(
        Schedule.Day(listOf(null, null,
            Schedule.Lesson("M", "SCHN", "7A"),
            Schedule.Lesson("M", "SCHN", "7A"),
        )),
        Schedule.Day(listOf()),
        Schedule.Day(listOf()),
        Schedule.Day(listOf()),
        Schedule.Day(listOf()),
    )

    println(schedule.serialize().toJSONString())
}*/

/*val map = hashMapOf<String, Int>()

suspend fun main() {
    coroutineScope {
        launch {
            map["A"] = 1234
            println(map)
            remove("A")
            delay(1000L)
            map["B"] = 5678
            println(map)
            remove("B")
            delay(10000L)
            println(map)
        }
    }
}

suspend fun CoroutineScope.remove(k: String) {
        launch {
            delay(5000L)
            map.remove(k)
            println("R " + map)
        }
}*/