package tk.q11mk.database

import org.intellij.lang.annotations.Language
import java.sql.ResultSet
import java.sql.SQLException
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
    fun <P> createTable(name: String, columns: LinkedHashMap<String, DataType<*>>, primaryKeyName: String) = runCatching {
        stmt.run(buildString {
            append("CREATE TABLE `")
            append(this@Schema.name)
            append("`.`")
            append(name)
            append("` (")
            for ((c, t) in columns) {
                append('`')
                append(c)
                append("` `")
                append(t)
                append("`, ")
            }
            append("PRIMARY KEY (`")
            append(primaryKeyName)
            append("`));")
        })
        Table<P>(name, primaryKeyName, this.name, stmt)
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

    fun <P> getTable(name: String): Result<Table<P>> = runCatching {
        Table(name, stmt.query("SHOW KEYS FROM `${this.name}`.`$name` WHERE Key_name = 'PRIMARY'").apply(ResultSet::next).getString("Column_name"), this.name, this.stmt)
    }

    fun deleteTable(name: String) = runCatching<Unit> {
        stmt.run("DROP TABLE `${this.name}`.`$name`")
    }
}