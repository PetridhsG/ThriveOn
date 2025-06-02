package gr.aueb.thriveon.domain.model

import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey

class PrivateTask : RealmObject {
    @PrimaryKey
    var id: Long = 0
    var taskTitle: String = ""
    var dueOn: String? = null
}
