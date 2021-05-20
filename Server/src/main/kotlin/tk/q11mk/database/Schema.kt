package tk.q11mk.database

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
    fun createTable(name: String, pkName: String) = try {
        val sql = """CREATE TABLE `${this.name}`.`$name` (
  `$pkName` INT NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`$pkName`));"""
        stmt.executeUpdate(sql)
        Result.success(Table(name, pkName, this.name, stmt))
    } catch (e: SQLException) {
        Result.failure(e)
    }
}