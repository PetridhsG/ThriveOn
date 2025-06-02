package gr.aueb.thriveon.domain.interactors

import gr.aueb.thriveon.domain.model.PrivateTask
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.LocalDate
import kotlin.coroutines.CoroutineContext

interface PrivateTaskInteractor {
    fun getPrivateTasks(): Flow<List<PrivateTask>>
    suspend fun addPrivateTask(task: PrivateTask)
    suspend fun editPrivateTask(privateTaskId: Long, newTitle: String, newDueDate: LocalDate?)
    suspend fun deletePrivateTask(privateTaskId: Long)
    suspend fun deleteExpiredTasks()
    suspend fun deleteAllPrivateTasks()
}

class PrivateTaskInteractorImpl(
    private val realmConfiguration: RealmConfiguration,
    private val ioDispatcher: CoroutineContext,
) : PrivateTaskInteractor {
    private val realm: Realm
        get() = Realm.open(realmConfiguration)

    override fun getPrivateTasks(): Flow<List<PrivateTask>> {
        return realm.query(PrivateTask::class)
            .asFlow()
            .map { it.list }
    }

    override suspend fun addPrivateTask(task: PrivateTask) {
        withContext(ioDispatcher) {
            realm.write {
                val currentMaxId = query(PrivateTask::class).max("id", Long::class).find() ?: 0L
                val nextId = currentMaxId + 1

                copyToRealm(PrivateTask().apply {
                    id = nextId
                    taskTitle = task.taskTitle
                    dueOn = task.dueOn
                })
            }
        }
    }

    override suspend fun editPrivateTask(
        privateTaskId: Long,
        newTitle: String,
        newDueDate: LocalDate?,
    ) {
        withContext(ioDispatcher) {
            realm.write {
                val task = query(PrivateTask::class, "id == $0", privateTaskId).first().find()
                task?.let {
                    it.taskTitle = newTitle
                    it.dueOn = newDueDate?.toString()
                }
            }
        }
    }

    override suspend fun deletePrivateTask(privateTaskId: Long) {
        withContext(ioDispatcher) {
            realm.write {
                val task = query(PrivateTask::class, "id == $0", privateTaskId).first().find()
                task?.let { delete(it) }
            }
        }
    }

    override suspend fun deleteExpiredTasks() {
        withContext(ioDispatcher) {
            val today = LocalDate.now()
            realm.write {
                val expiredTasks = query(PrivateTask::class)
                    .find()
                    .filter {
                        it.dueOn?.let { date ->
                            LocalDate.parse(date).isBefore(today)
                        } == true
                    }

                expiredTasks.forEach { delete(it) }
            }
        }
    }

    override suspend fun deleteAllPrivateTasks() {
        withContext(ioDispatcher) {
            realm.write {
                val allTasks = query(PrivateTask::class).find()
                delete(allTasks)
            }
        }
    }
}
