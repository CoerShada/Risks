package ru.com.risks.common.data

data class Factory(var id: Int, var name: String) {

    constructor(name: String) : this(-1, name) {
    }
}
