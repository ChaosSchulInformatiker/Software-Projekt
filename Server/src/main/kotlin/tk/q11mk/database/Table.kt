package tk.q11mk.database

import java.sql.SQLException
import java.sql.Statement

class Table internal constructor(val name: String, pkName: String, private val schemaName: String, private val stmt: Statement) {
    private val columns = mutableListOf(Column(pkName, "INT", null, listOf("NOT NULL", "AUTO_INCREMENT")))

    /**
     * Add a column at the end of the table
     *
     * @param name The name of the column
     * @param type The type (e.g. INT, JSON, ...)
     * @param default The default value. If it's a string enclose it in single quotes (')
     * @param flags Additional flags (e.g. NOT NULL, AUTO_INCREMENT)
     * @return A wrapper of the column
     *
     * @author Simon
     */
    fun addColumn(name: String, type: String, default: String? = null, vararg flags: String) =
        addColumnAfter(name, type, default, columns.last().name, *flags)

    /**
     * Add a column after the column with the specified name
     *
     * @see addColumn
     * @param after The name of the column before the new one
     *
     * @author Simon
     */
    fun addColumnAfter(name: String, type: String, default: String? = null, after: String, vararg flags: String) = try {
        stmt.executeUpdate("ALTER TABLE `$schemaName`.`${this.name}`\nADD COLUMN `$name` $type ${
            if ("NOT NULL" in flags) "" else "NULL"
        } ${flags.joinToString(" ")} ${default?.let { "DEFAULT $it" } ?: ""} AFTER `$after`;")
        val c = Column(name, type, default, flags.toList())
        columns.add(c)
        Result.success(c)
    } catch (e: SQLException) {
        Result.failure(e)
    }

    data class Column internal constructor(val name: String, val type: String, val default: String? = null, val flags: List<String>)
}