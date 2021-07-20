import tk.q11mk.database.Database

fun main() {
    Database("jdbc:mysql://127.0.0.1:3306", "Test", "test").use { db ->
        /*val s = db.createSchema("schema").getOrThrow()
        val t = s.createTable("table", "id").getOrThrow()
        t.addColumn("column", Table.Column.Type.JSON).getOrThrow()*/
        val s = db.getSchema("schedule").getOrThrow()
        val mon = s.getTable<Int>("mon").getOrThrow()
        val c = mon.get<String>(0, "SCHN").getOrThrow()
        println(c)
    }
}