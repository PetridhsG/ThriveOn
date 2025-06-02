package gr.aueb.thriveon.core.controllers

import gr.aueb.thriveon.core.provider.PrefsRealmProvider
import io.realm.kotlin.Realm
import java.security.SecureRandom

interface RealmKeyController {
    /**
     * Retrieves the realm key if it exists, otherwise it is created, saved, and returned.
     *
     * @return Returns the realm key used for decrypting the database.
     */
    fun retrieveOrGenerateRealmKey(): ByteArray
}

class RealmKeyControllerImpl(
    private val prefsRealmProvider: PrefsRealmProvider
) : RealmKeyController {

    override fun retrieveOrGenerateRealmKey(): ByteArray {
        val storedKey = prefsRealmProvider.getRealmKey()
        if (storedKey != null) {
            return storedKey
        }

        val key = ByteArray(Realm.ENCRYPTION_KEY_LENGTH)
        SecureRandom().nextBytes(key)

        prefsRealmProvider.setRealmKey(key)
        return key
    }
}
