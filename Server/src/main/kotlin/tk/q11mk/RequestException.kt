package tk.q11mk

class RequestException(msg: String, val responseCode: Int = 500) : Exception(msg)