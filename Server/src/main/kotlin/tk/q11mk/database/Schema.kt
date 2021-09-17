package tk.q11mk.database

import java.sql.Statement

class Schema internal constructor(val name: String, private val stmt: Statement) {

    /**
     * Creates a table in this schema
     *
     * @param name The name of the table
     * @param pkName The name of the primary Key. This is added as a not null auto increment int.
     * @return A wrapper of the table
     *
     * @author Simon
     */
    fun createTable(name: String, vararg columns: Pair<String, DataType<*>>) = runCatching {
        stmt.run(buildString {
            append("CREATE TABLE `")
            append(this@Schema.name)
            append("`.`")
            append(name)
            append("` (`")
            append("` ")
            append(", ")
            for ((c, t) in columns) {
                append('`')
                append(c)
                append("` ")
                append(t)
                append(", ")
            }/*
            append("PRIMARY KEY (`")
            append(primaryKey.first)
            append("`));")*/
        })
        Table(name, this.name, stmt)
    }

    /*fun <P> createTable(name: String, pkName: String, type: Table.Column.Type<P>, vararg flags: String = arrayOf("NOT NULL", "AUTO_INCREMENT")) = try {
        @Language("sql") val sql = """CREATE TABLE `${this.name}`.`$name` (
`$pkName` $type ${
            if ("NOT NULL" in flags) "" else "NULL"
        } ${flags.joinToString(" ")},
PRIMARY KEY (`$pkName`));"""
        stmt.executeUpdate(sql)
        Result.success(Table<P>(name, pkName, this.name, stmt))
    } catch (e: SQLException) {
        Result.failure(e)
    }*/

    fun getTable(name: String): Result<Table> = runCatching {
        //stmt.query("SHOW KEYS FROM `${this.name}`.`$name` WHERE Key_name = 'PRIMARY'").apply(ResultSet::next).getString("Column_name"),
        Table(name, this.name, this.stmt)
    }/*.also {
        it.onFailure {
            it.printStackTrace()
        }
    }*/

    fun deleteTable(name: String) = runCatching<Unit> {
        stmt.run("DROP TABLE `${this.name}`.`$name`")
    }
}