import tk.q11mk.database.DataType
import tk.q11mk.database.Database
import tk.q11mk.database.getLesson

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
    println(getLesson("mon", 0, "SCHN"))
}