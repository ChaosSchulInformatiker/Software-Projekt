package tk.q11mk.database

import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException
import java.sql.Statement

/**
 * Main class for representing a sql database
 *
 * @constructor Create a database from login data
 * @param url The url of the database, starting with jdbc:mysql://
 * @param username The login username
 * @param password The login password
 *
 * @author Simon
 */
class Database(url: String, username: String, password: String) : AutoCloseable {
    private val connection: Connection = DriverManager.getConnection(url, username, password)
    private val stmt: Statement = connection.createStatement()

    /**
     * Creates a schema (the real database with tables)
     *
     * @param name The name of the schema
     * @return A wrapper of the schema
     *
     * @author Simon
     */
    fun createSchema(name: String) = try {
        stmt.executeUpdate("CREATE SCHEMA `$name`;")
        Result.success(Schema(name, stmt))
    } catch (e: SQLException) {
        Result.failure(e)
    }

    override fun close() {
        connection.close()
        stmt.close()
    }
}