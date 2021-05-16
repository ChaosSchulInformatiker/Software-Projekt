package tk.q11mk

class RequestException(msg: String, val responseCode: String = "500") : Exception(msg)