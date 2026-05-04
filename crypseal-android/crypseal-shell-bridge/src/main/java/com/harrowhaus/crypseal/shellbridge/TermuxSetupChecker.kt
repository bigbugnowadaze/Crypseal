package com.harrowhaus.crypseal.shellbridge

import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

class TermuxSetupChecker(private val context: Context) {

    fun isTermuxInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.termux", 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    fun hasRunCommandPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, 
            "com.termux.permission.RUN_COMMAND"
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun getSetupState(): TermuxSetupState {
        if (!isTermuxInstalled()) return TermuxSetupState.NOT_INSTALLED
        if (!hasRunCommandPermission()) return TermuxSetupState.PERMISSION_DENIED
        return TermuxSetupState.READY
    }
}

enum class TermuxSetupState {
    NOT_INSTALLED,
    PERMISSION_DENIED,
    READY
}
