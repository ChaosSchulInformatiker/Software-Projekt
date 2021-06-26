package tk.q11mk.database

import tk.q11mk.JSONSerializable
import java.sql.SQLException
import java.sql.Statement

class Table <P> internal constructor(val name: String, pkName: String, private val schemaName: String, private val stmt: Statement) {
    private val columns: MutableList<Column<*>> = mutableListOf(Column(pkName, Column.Type.INT, null, listOf("NOT NULL", "AUTO_INCREMENT")))

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
    fun <T> addColumn(name: String, type: Column.Type<T>, default: T? = null, vararg flags: String) =
        addColumnAfter(name, type, default, columns.last().name, *flags)

    /**
     * Add a column after the column with the specified name
     *
     * @see addColumn
     * @param after The name of the column before the new one
     *
     * @author Simon
     */
    fun <T> addColumnAfter(name: String, type: Column.Type<T>, default: T? = null, after: String, vararg flags: String) = try {
        stmt.executeUpdate("ALTER TABLE `$schemaName`.`${this.name}`\nADD COLUMN `$name` $type ${
            if ("NOT NULL" in flags) "" else "NULL"
        } ${flags.joinToString(" ")} ${default?.let { "DEFAULT $it" } ?: ""} AFTER `$after`;")
        val c = Column(name, type, default, flags.toList())
        columns.add(c)
        Result.success(c)
    } catch (e: SQLException) {
        Result.failure(e)
    }

    fun <T> getCell(pk: P, column: String): Result<T> = try {
        throw SQLException()
    } catch (e: SQLException) {
        Result.failure<Nothing>(e)
    }

    data class Column<T> internal constructor(val name: String, val type: Type<T>, val default: T? = null, val flags: List<String>) {
        class Type<T> private constructor(val str: String) {
            override fun toString() = str

            companion object {
                val STRING = Type<String>("STRING")
                val INT = Type<Int>("INT")
                val JSON = Type<String>("JSON")
            }
        }
    }
}