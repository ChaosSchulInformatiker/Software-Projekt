package tk.q11mk.database

class DataType<T> private constructor(val str: String) {
    override fun toString() = str

    companion object {
        val STRING = DataType<String>("STRING")
        val INT = DataType<Int>("INT")
        val JSON = DataType<String>("JSON")
    }
}