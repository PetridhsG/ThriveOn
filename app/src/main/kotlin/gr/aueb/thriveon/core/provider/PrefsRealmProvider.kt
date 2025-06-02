package gr.aueb.thriveon.core.provider

import gr.aueb.thriveon.core.controllers.encryptedSharedPrefs.PrefsController
import gr.aueb.thriveon.core.controllers.encryptedSharedPrefs.model.PrefKey
import gr.aueb.thriveon.core.utils.decodeFromPemBase64String
import gr.aueb.thriveon.core.utils.encodeToPemBase64String

interface PrefsRealmProvider {
    /**
     * Stores the given realm [key] to the encrypted shared preferences.
     *
     * @param key The realm key to store.
     */
    fun setRealmKey(key: ByteArray)

    /**
     * Retrieves the realm key from the encrypted shared preferences.
     *
     * @return Returns the key as a [ByteArray] if it was found, otherwise `null` is returned.
     */
    fun getRealmKey(): ByteArray?
}

class PrefsRealmProviderImpl(
    private val prefsController: PrefsController,
) : PrefsRealmProvider {
    override fun setRealmKey(key: ByteArray) {
        prefsController.setString(PrefKey.RealmKey, key.encodeToPemBase64String())
    }

    override fun getRealmKey(): ByteArray? {
        val realmKey = prefsController.getString(PrefKey.RealmKey, "")
        return realmKey.takeIf {
            it.isNotBlank()
        }?.decodeFromPemBase64String()
    }
}