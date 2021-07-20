package tk.q11mk.database

class DataType<T> private constructor(val str: String) {
    override fun toString() = str

    fun notNull() = DataType<T>("$str NOT NULL")
    fun nullable() = DataType<T?>("$str NULL")

    companion object {
        fun STRING(max: Int) = DataType<String>("VARCHAR($max)")
        val STRING = STRING(30)
        val INT = DataType<Int>("INT")
        val JSON = DataType<String>("JSON")
        val BOOL = DataType<Boolean>("BOOLEAN")
        fun <T> CSV(max: Int = 100) = DataType<List<T>>("VARCHAR($max)")
    }
}