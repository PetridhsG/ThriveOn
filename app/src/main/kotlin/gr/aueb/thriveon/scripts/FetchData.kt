package gr.aueb.thriveon.scripts

import com.google.firebase.firestore.FirebaseFirestore

fun fetchAllDocuments(){
    fetchAllUsers()
    fetchAllAllUsers()
    fetchAllTasks()
    fetchAllPosts()
}

fun fetchAllUsers() {
    val db = FirebaseFirestore.getInstance()

    db.collection("users")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                println("User Document ID: ${document.id} => ${document.data}")
            }
        }
        .addOnFailureListener { exception ->
            println("Error getting Users: $exception")
        }
}

fun fetchAllAllUsers() {
    val db = FirebaseFirestore.getInstance()

    db.collection("all_users")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                println("All_users Document ID: ${document.id} => ${document.data}")
            }
        }
        .addOnFailureListener { exception ->
            println("Error getting All_users: $exception")
        }
}

fun fetchAllTasks() {
    val db = FirebaseFirestore.getInstance()

    db.collection("tasks")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                println("Task Document ID: ${document.id} => ${document.data}")
            }
        }
        .addOnFailureListener { exception ->
            println("Error getting Tasks: $exception")
        }
}

fun fetchAllPosts() {
    val db = FirebaseFirestore.getInstance()

    db.collection("posts")
        .get()
        .addOnSuccessListener { result ->
            for (document in result) {
                println("Post Document ID: ${document.id} => ${document.data}")
            }
        }
        .addOnFailureListener { exception ->
            println("Error getting Posts: $exception")
        }
}
