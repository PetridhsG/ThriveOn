package gr.aueb.thriveon.di

import gr.aueb.thriveon.core.controllers.RealmKeyController
import gr.aueb.thriveon.domain.model.PrivateTask
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.types.TypedRealmObject
import org.koin.dsl.module
import kotlin.reflect.KClass

val databaseModule = module {
    single<Set<KClass<out TypedRealmObject>>> {
        setOf(
            PrivateTask::class
        )
    }

    single {
        val realmKeyController: RealmKeyController = get()
        val encryptionKey = realmKeyController.retrieveOrGenerateRealmKey()

        RealmConfiguration.Builder(get())
            .encryptionKey(encryptionKey)
            .deleteRealmIfMigrationNeeded()
            .name("realm_db_1")
            .schemaVersion(1L)
            .build()
    }
}
