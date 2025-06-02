package gr.aueb.thriveon.core.controllers.encryptedSharedPrefs

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import gr.aueb.thriveon.core.controllers.encryptedSharedPrefs.model.PrefKey
import gr.aueb.thriveon.core.resources.ResourceProvider

interface PrefsController {
    fun contains(prefKey: PrefKey): Boolean
    fun getString(key: PrefKey, defaultValue: String): String
    fun setString(key: PrefKey, value: String)
    fun removeString(key: PrefKey)
    fun clearAll()
}

class PrefsControllerImpl(
    private val resourceProvider: ResourceProvider
) : PrefsController {
    private val sharedPrefs: SharedPreferences
        get() = getEncryptedSharedPreferences()

    override fun contains(prefKey: PrefKey): Boolean {
        return sharedPrefs.contains(prefKey.keyValue)
    }

    override fun getString(key: PrefKey, defaultValue: String): String {
        return sharedPrefs.getString(key.keyValue, defaultValue) ?: defaultValue
    }

    override fun setString(key: PrefKey, value: String) {
        sharedPrefs.edit { putString(key.keyValue, value) }
    }

    override fun removeString(key: PrefKey) {
        sharedPrefs.edit { remove(key.keyValue) }
    }

    override fun clearAll() {
        sharedPrefs.edit { clear() }
    }

    private fun getEncryptedSharedPreferences(): SharedPreferences {
        return EncryptedSharedPreferences.create(
            "secure_data",
            MasterKeys.getOrCreate(
                MasterKeys.AES256_GCM_SPEC
            ),
            resourceProvider.provideContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }
}
