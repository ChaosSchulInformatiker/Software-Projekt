package tk.q11mk

import kotlinx.browser.document
import kotlinx.dom.appendText

fun main() {
    println("Starting")
    document.body!!.appendText(hw())
}