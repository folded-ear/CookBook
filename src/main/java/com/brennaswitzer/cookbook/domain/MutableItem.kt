package com.brennaswitzer.cookbook.domain

interface MutableItem : Item {
    // I have omitted setRaw(String) from the interface, as I believe changes to
    // the raw string are an implementation detail. But moving from the raw
    // string to the other properties is definitely Item-ish. But that's up
    // for discussion. :)
    override var quantity: Quantity?
    override var preparation: String?
    override var ingredient: Ingredient?
}
