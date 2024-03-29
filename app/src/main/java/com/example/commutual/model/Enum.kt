/**
 * This file contains two enum classes
 * CategoryEnum defines enums for task and post categories
 * MessageTabEnum defines enums for the tabs for the MessageScreen
 */

package com.example.commutual.model

import androidx.annotation.StringRes
import com.example.commutual.R


enum class CategoryEnum(@StringRes val categoryStringRes: Int) {
    ANY(R.string.any),
    NONE(R.string.empty_string),
    ACADEMICS(R.string.academics),
    ART(R.string.art),
    ATHLETICS(R.string.athletics),
    CODING(R.string.coding),
    HEALTH_AND_WELLNESS(R.string.health_and_wellness),
    MISCELLANEOUS(R.string.miscellaneous),
    MUSIC(R.string.music),
    WORK(R.string.work)
}

enum class MessageTabEnum(@StringRes val tabStringRes: Int) {
    CHAT(R.string.chat),
    TASKS(R.string.tasks)
}

