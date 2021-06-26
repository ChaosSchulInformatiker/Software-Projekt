import tk.q11mk.database.Database
import tk.q11mk.database.Table

fun main() {
    Database("jdbc:mysql://127.0.0.1:3306", "Test", "test").use { db ->
        val s = db.createSchema("schema").getOrThrow()
        val t = s.createTable("table", "id").getOrThrow()
        t.addColumn("column", Table.Column.Type.JSON).getOrThrow()
    }
}