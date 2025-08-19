package com.rifsxd.ksunext.ui.util

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable

/**
 * Utility object for common settings operations across different settings screens.
 * Provides helper functions for SharedPreferences management and state handling.
 */
object SettingsUtils {
    
    /**
     * Gets the default settings SharedPreferences instance.
     */
    fun getSettingsPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
    
    /**
     * Creates a saveable boolean state that automatically syncs with SharedPreferences.
     * 
     * @param prefs The SharedPreferences instance
     * @param key The preference key
     * @param defaultValue The default value if the preference doesn't exist
     * @param onChanged Optional callback when the value changes
     * @return A MutableState that automatically saves to preferences
     */
    @Composable
    fun rememberBooleanPreference(
        prefs: SharedPreferences,
        key: String,
        defaultValue: Boolean = false,
        onChanged: ((Boolean) -> Unit)? = null
    ): MutableState<Boolean> {
        val state = rememberSaveable {
            mutableStateOf(prefs.getBoolean(key, defaultValue))
        }
        
        return object : MutableState<Boolean> {
            override var value: Boolean
                get() = state.value
                set(newValue) {
                    state.value = newValue
                    prefs.edit().putBoolean(key, newValue).apply()
                    onChanged?.invoke(newValue)
                }
            
            override fun component1(): Boolean = value
            override fun component2(): (Boolean) -> Unit = { value = it }
        }
    }
    
    /**
     * Creates a saveable string state that automatically syncs with SharedPreferences.
     * 
     * @param prefs The SharedPreferences instance
     * @param key The preference key
     * @param defaultValue The default value if the preference doesn't exist
     * @param onChanged Optional callback when the value changes
     * @return A MutableState that automatically saves to preferences
     */
    @Composable
    fun rememberStringPreference(
        prefs: SharedPreferences,
        key: String,
        defaultValue: String = "",
        onChanged: ((String) -> Unit)? = null
    ): MutableState<String> {
        val state = rememberSaveable {
            mutableStateOf(prefs.getString(key, defaultValue) ?: defaultValue)
        }
        
        return object : MutableState<String> {
            override var value: String
                get() = state.value
                set(newValue) {
                    state.value = newValue
                    prefs.edit().putString(key, newValue).apply()
                    onChanged?.invoke(newValue)
                }
            
            override fun component1(): String = value
            override fun component2(): (String) -> Unit = { value = it }
        }
    }
    
    /**
     * Batch update multiple boolean preferences.
     * 
     * @param prefs The SharedPreferences instance
     * @param updates Map of preference keys to their new values
     */
    fun updateBooleanPreferences(
        prefs: SharedPreferences,
        updates: Map<String, Boolean>
    ) {
        prefs.edit().apply {
            updates.forEach { (key, value) ->
                putBoolean(key, value)
            }
            apply()
        }
    }
    
    /**
     * Batch update multiple string preferences.
     * 
     * @param prefs The SharedPreferences instance
     * @param updates Map of preference keys to their new values
     */
    fun updateStringPreferences(
        prefs: SharedPreferences,
        updates: Map<String, String>
    ) {
        prefs.edit().apply {
            updates.forEach { (key, value) ->
                putString(key, value)
            }
            apply()
        }
    }
    
    /**
     * Remove multiple preferences at once.
     * 
     * @param prefs The SharedPreferences instance
     * @param keys List of preference keys to remove
     */
    fun removePreferences(
        prefs: SharedPreferences,
        keys: List<String>
    ) {
        prefs.edit().apply {
            keys.forEach { key ->
                remove(key)
            }
            apply()
        }
    }
}