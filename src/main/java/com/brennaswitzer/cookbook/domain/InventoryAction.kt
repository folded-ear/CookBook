package com.brennaswitzer.cookbook.domain

enum class InventoryAction {

    /** You got some! */
    ACQUIRE,

    /** You used some! */
    CONSUME,

    /** You threw some away! */
    DISCARD,

    /** You lost track and need a reset! */
    RESET;

}
