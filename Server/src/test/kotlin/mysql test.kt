import tk.q11mk.accounts.changeClassData
import tk.q11mk.database.*
import tk.q11mk.schedule.Schedule

fun main() {
    /*Database("jdbc:mysql://127.0.0.1:3306", "Test", "test").use { db ->
        /*val s = db.createSchema("schema").getOrThrow()
        val t = s.createTable("table", "id").getOrThrow()
        t.addColumn("column", Table.Column.Type.JSON).getOrThrow()*/
        val s = db.getSchema("schedule").getOrThrow()
        val mon = s.getTable<Int>("mon").getOrThrow()
        val c = mon.get<String>(0, "SCHN").getOrThrow()
        println(c)
    }*/
    /*Database("jdbc:mysql://127.0.0.1:3306", "Test", "test").use { db ->
        val t = db.getSchema("schedules").getOrThrow().getTable<Int>("mon").getOrThrow()
        //t.addColumn("SCHN", DataType.STRING(100)).getOrThrow()
        //t.set("SCHN", 0, "{}").getOrThrow()
        t.insertRow(listOf(0, "{}")).getOrThrow()
    }*/
    /*getScheduleTable(0).addColumn("SCHN", DataType.STRING(256)).getOrThrow()
    getScheduleTable(0)//.set("SCHN", 1, """{"class": "7A"}""").getOrThrow()
        .insertRow(listOf(1, """{"class": "7A"}""")).getOrThrow()*/

    //getScheduleTable(0).set("SCHN", 1, """{"class":"7A","subject":"M"}""")
    //getScheduleTable(1).set("POHL", 5, """{"class":"7A","teacher":"POHL","subject":"Inf","room":"E209","substituted":true,"substitute_teacher":"SCHN","substitute_room":"E209"}""").getOrThrow()
    //println(Schedule.Day.fromRequest(1, "7A", "Inf, M, D"))

    //println(getScheduleTable(1).getLike<String>("POHL", """%"class"%:%"7A"%"""))

    //Schedule.Day.fromRequest(0, "7A", "D,E,M,F")

    //idsTable.insertRow(listOf(1234567890L, "Simon", "Neumann", "simon.neumann@maristenkolleg.de", false)).getOrThrow()
    //println(getAccountFromId(1234567890))
    //println(idsTable.has("51234567890"))

    //println(changeClassData(1234567890.toString(), "11Q", "M1,D3"))
}