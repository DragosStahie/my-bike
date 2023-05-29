package com.mybike.common

import androidx.core.view.forEachIndexed
import androidx.core.view.size
import com.google.android.material.bottomnavigation.BottomNavigationView

val Any.TAG: String
    get() {
        val tag = javaClass.simpleName
        return if (tag.length <= 23) tag else tag.substring(0, 23)
    }

fun BottomNavigationView.setNextMenuItemChecked() {
    val selectedIndex = getSelectedItemIndex()
    if (selectedIndex != -1 && selectedIndex < menu.size - 1) {
        menu.getItem(selectedIndex + 1).isChecked = true
    }
}

fun BottomNavigationView.setPreviousMenuItemChecked() {
    val selectedIndex = getSelectedItemIndex()
    if (selectedIndex != -1 && selectedIndex > 0) {
        menu.getItem(selectedIndex - 1).isChecked = true
    }
}

fun BottomNavigationView.getSelectedItemIndex(): Int {
    var selectedIndex = -1
    menu.forEachIndexed { index, item ->
        if (item.isChecked) {
            selectedIndex = index
        }
    }
    return selectedIndex
}