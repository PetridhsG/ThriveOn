package gr.aueb.thriveon.core.controllers.encryptedSharedPrefs.model

sealed class PrefKey(
    val keyValue: String
) {
    data object RealmKey : PrefKey(keyValue = "prefs_realm_key")
}
