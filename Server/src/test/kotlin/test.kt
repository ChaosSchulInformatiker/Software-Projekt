import tk.q11mk.schedule.Schedule

fun main() {
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
}