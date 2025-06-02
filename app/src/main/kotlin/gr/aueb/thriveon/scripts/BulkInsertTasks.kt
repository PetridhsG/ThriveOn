package gr.aueb.thriveon.scripts

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

fun resetAndAddTasks() {
    val db = FirebaseFirestore.getInstance()
    val taskCollection = db.collection("tasks")

    taskCollection.get()
        .addOnSuccessListener { snapshot ->
            val batch = db.batch()
            for (document in snapshot.documents) {
                batch.delete(document.reference)
            }

            batch.commit()
                .addOnSuccessListener {
                    println("All old tasks deleted.")
                    addTasks(taskCollection)
                }
                .addOnFailureListener { e ->
                    println("Failed to delete tasks: $e")
                }
        }
        .addOnFailureListener { e ->
            println("Failed to fetch tasks for deletion: $e")
        }
}

private fun addTasks(taskCollection: CollectionReference) {
    val learningTasks = listOf(
        "Read 10+ pages of a book",
        "Watch a 15+ minute educational video",
        "Listen to a 10+ minute podcast",
        "Learn 10 foreign words",
        "Complete a lesson in a learning app",
        "Read an article on a topic you are curious about",
        "Research and learn one new fact",
        "Review notes from a book or course",
        "Research a recent news story",
        "Try a short brain teaser or logic puzzle"
    )
    for (title in learningTasks) {
        val sharedCategoryIcon = "\uD83D\uDCD6" // 📖
        val docRef = taskCollection.document()
        val uid = docRef.id

        val task = mapOf(
            "uid" to uid,
            "title" to title,
            "category_title" to "Learning & Growth",
            "category_icon" to sharedCategoryIcon,
            "default_picture" to "https://firebasestorage.googleapis.com/v0/b/thriveon-a9f13.firebasestorage.app/o/default_images%2Flearning_growth.webp?alt=media&token=8718dd8a-7c6f-4f40-9af1-94512a00d057",
            "milestones" to mapOf(
                "5" to mapOf("title" to "Curious Learner", "badge" to "\uD83E\uDD14"),    // 🤔
                "15" to mapOf("title" to "Growth Enthusiast", "badge" to "\uD83D\uDCDA"),  // 📚
                "25" to mapOf("title" to "Knowledge Hunter", "badge" to "\uD83C\uDFCf"),   // 🎏
                "40" to mapOf("title" to "Thought Explorer", "badge" to "\uD83E\uDDAD"),   // 🧭
                "60" to mapOf("title" to "Enlightened Mind", "badge" to "\uD83E\uDDE0")    // 🧠
            )
        )

        docRef.set(task)
            .addOnSuccessListener {
                println("Added task: $title")
            }
            .addOnFailureListener { e ->
                println("Failed to add task $title: $e")
            }
    }

    val physicalTasks = listOf(
        "Do 30+ minutes of cardio",
        "Drink at least 2lt. of water",
        "Walk 4000+ steps",
        "Prepare a healthy meal",
        "Take a 10-minute walk",
        "Practice good posture",
        "Take the stairs instead of the elevator",
        "Avoid caffeine after 2 PM",
        "Hold a plank for 45 seconds",
        "Try 5 minutes of deep breathing exercises"
    )
    for (title in physicalTasks) {
        val sharedCategoryIcon = "\uD83C\uDFC3"
        val docRef = taskCollection.document()
        val uid = docRef.id

        val task = mapOf(
            "uid" to uid,
            "title" to title,
            "category_title" to "Physical Exercise & Health",
            "category_icon" to sharedCategoryIcon,
            "default_picture" to "https://firebasestorage.googleapis.com/v0/b/thriveon-a9f13.firebasestorage.app/o/default_images%2Fphysical_exercise_health.webp?alt=media&token=6597834d-5da0-4f6e-9488-121661506d31",
            "milestones" to mapOf(
                "5" to mapOf("title" to "Aspiring Beginner", "badge" to "\uD83C\uDFC3"),        // 🏃
                "15" to mapOf("title" to "Strong Contender", "badge" to "\uD83D\uDCAA"),       // 💪
                "25" to mapOf("title" to "Dedicated Athlete", "badge" to "\uD83C\uDFCB\uFE0F"), // 🏋️‍♀️
                "40" to mapOf("title" to "Endurance Champion", "badge" to "\uD83C\uDFF5"),     // 🏵️
                "60" to mapOf("title" to "Fitness Legend", "badge" to "\uD83C\uDFC6")          // 🏆
            )
        )

        docRef.set(task)
            .addOnSuccessListener {
                println("Added task: $title")
            }
            .addOnFailureListener { e ->
                println("Failed to add task $title: $e")
            }
    }

    val adventuresTasks = listOf(
        "Visit a place you’ve never been to before",
        "Visit a local landmark or historical spot",
        "Discover a hidden gem (scenic street, quiet park etc.)",
        "Visit a museum, gallery, or cultural space",
        "Have a picnic in a park",
        "Find and visit a local coffee shop you haven’t tried",
        "Go to a nearby beach or lake for a walk",
        "Try a new ethnic restaurant",
        "Watch the sunset/sunrise from a rooftop",
        "Take a different route on your daily walk"
    )
    for (title in adventuresTasks) {
        val sharedCategoryIcon = "\uD83C\uDFC3"
        val docRef = taskCollection.document()
        val uid = docRef.id

        val task = mapOf(
            "uid" to uid,
            "title" to title,
            "category_title" to "Adventure & Discoveries",
            "category_icon" to sharedCategoryIcon,
            "default_picture" to "https://firebasestorage.googleapis.com/v0/b/thriveon-a9f13.firebasestorage.app/o/default_images%2Fadventures_discoveries.webp?alt=media&token=4b0474f3-e63b-4804-b4ef-62745dd12089",
            "milestones" to mapOf(
                "5" to mapOf("title" to "Street Explorer", "badge" to "\uD83D\uDEB6"),     // 🚶
                "15" to mapOf("title" to "City Scout", "badge" to "\uD83C\uDFD9"),         // 🏙️
                "25" to mapOf("title" to "Cultural Seeker", "badge" to "\uD83C\uDFDB"),    // 🏛️
                "40" to mapOf("title" to "World Adventurer", "badge" to "\uD83C\uDF0E"),   // 🌎
                "60" to mapOf("title" to "Epic Voyager", "badge" to "\uD83D\uDDFA")        // 🗺️

            )
        )

        docRef.set(task)
            .addOnSuccessListener {
                println("Added task: $title")
            }
            .addOnFailureListener { e ->
                println("Failed to add task $title: $e")
            }
    }

    val cookingTasks = listOf(
        "Swap a snack for a healthier option",
        "Plan your meals for the upcoming week",
        "Eat 5 different fruits or vegetables today",
        "Avoid processed foods for a whole day",
        "Cook a dish with whole, seasonal ingredients",
        "Bake a dessert",
        "Blend a fresh smoothie",
        "Experiment with seasoning/spices",
        "Cook using only ingredients you already have at home",
        "Swap one meal for a vegetarian or vegan option"
    )
    for (title in cookingTasks) {
        val sharedCategoryIcon = "\uD83E\uDD57" // 🥗
        val docRef = taskCollection.document()
        val uid = docRef.id

        val task = mapOf(
            "uid" to uid,
            "title" to title,
            "category_title" to "Cooking & Nutrition",
            "category_icon" to sharedCategoryIcon,
            "default_picture" to "https://firebasestorage.googleapis.com/v0/b/thriveon-a9f13.firebasestorage.app/o/default_images%2Fcooking_nutrition.webp?alt=media&token=52ccdcf6-2f28-4813-b450-b0d98633f215",
            "milestones" to mapOf(
                "5" to mapOf("title" to "Flavor Finder", "badge" to "\uD83C\uDF74"),      // 🍴
                "15" to mapOf("title" to "Nutrition Pro", "badge" to "\uD83C\uDF7D\uFE0F"), // 🍽️
                "25" to mapOf("title" to "Culinary Artist", "badge" to "\uD83C\uDFA8"),   // 🎨
                "40" to mapOf("title" to "Gastronomy Guru", "badge" to "\uD83C\uDF72"),   // 🍲
                "60" to mapOf("title" to "Meal Mastermind", "badge" to "\uD83C\uDF7E")  // 🍾
            )
        )

        docRef.set(task)
            .addOnSuccessListener {
                println("Added Cooking & Nutrition task: $title")
            }
            .addOnFailureListener { e ->
                println("Failed to add Cooking & Nutrition task $title: $e")
            }
    }

    val volunteeringTasks = listOf(
        "Donate clothes, food, or essentials",
        "Write a kind note or message to someone",
        "Perform a random act of kindness today",
        "Offer help to a neighbor, friend, or stranger",
        "Share a meal with someone in need",
        "Donate money to a charity",
        "Volunteer at a local shelter or community center",
        "Leave a positive review on a local business",
        "Help an elderly person",
        "Listen to someone in need"

    )
    for (title in volunteeringTasks) {
        val sharedCategoryIcon = "\uD83D\uDC9C" // 💜
        val docRef = taskCollection.document()
        val uid = docRef.id

        val task = mapOf(
            "uid" to uid,
            "title" to title,
            "category_title" to "Volunteering & Good Deeds",
            "category_icon" to sharedCategoryIcon,
            "default_picture" to "https://firebasestorage.googleapis.com/v0/b/thriveon-a9f13.firebasestorage.app/o/default_images%2Fvolunteering%20_good_deeds.webp?alt=media&token=5c9d0d24-206f-4998-83c1-e4c669fd38a8",
            "milestones" to mapOf(
                "5" to mapOf("title" to "Caring Beginner", "badge" to "\uD83D\uDD4A"),       // 🕊️
                "15" to mapOf("title" to "Helping Hand", "badge" to "\uD83D\uDC96"),          // 💖
                "25" to mapOf("title" to "Community Ally", "badge" to "\uD83D\uDE4F"),         // 🙏
                "40" to mapOf("title" to "Generosity Warrior", "badge" to "\uD83E\uDD1D"),    // 🤝
                "60" to mapOf("title" to "Light of the Community", "badge" to "\uD83C\uDF1F") // 🌟
            )
        )

        docRef.set(task)
            .addOnSuccessListener {
                println("Added Volunteering & Good Deeds task: $title")
            }
            .addOnFailureListener { e ->
                println("Failed to add Volunteering & Good Deeds task $title: $e")
            }
    }

    val creativityTasks = listOf(
        "Draw or sketch something from your surroundings",
        "Write a poem",
        "Play or compose a piece of music",
        "Make a DIY craft",
        "Doodle while listening to music",
        "Take a series of photos showing a day in your life",
        "Listen to a new music album",
        "Make a playlist ",
        "Turn your name into a graffiti",
        "Recreate a famous painting or movie scene"
    )
    for (title in creativityTasks) {
        val sharedCategoryIcon = "\uD83C\uDFA8" // 🎨
        val docRef = taskCollection.document()
        val uid = docRef.id

        val task = mapOf(
            "uid" to uid,
            "title" to title,
            "category_title" to "Expression & Creativity",
            "category_icon" to sharedCategoryIcon,
            "default_picture" to "https://firebasestorage.googleapis.com/v0/b/thriveon-a9f13.firebasestorage.app/o/default_images%2Fexpression_creativity.webp?alt=media&token=e70f69eb-3fa8-4ccb-8fe5-0332a90f6f66",
            "milestones" to mapOf(
                "5" to mapOf("title" to "Creative Spark", "badge" to "\uD83D\uDD8D"),  // 🖍️
                "15" to mapOf("title" to "Artistic Mind", "badge" to "\uD83C\uDFA8️"), // 🎨
                "25" to mapOf("title" to "Inspired Lyricist", "badge" to "\uD83C\uDFB5"), // 🎵
                "40" to mapOf("title" to "Expression Master", "badge" to "\uD83D\uDCF8"), // 📸
                "60" to mapOf("title" to "Stage Architect", "badge" to "\uD83C\uDFAD") // 🎭
            )
        )

        docRef.set(task)
            .addOnSuccessListener {
                println("Added Expression & Creativity task: $title")
            }
            .addOnFailureListener { e ->
                println("Failed to add Expression & Creativity task $title: $e")
            }
    }

    val cleaningTasks = listOf(
        "Clean or organize your room",
        "Clean out your fridge",
        "Recycle or donate unused items",
        "Tidy up your digital space (email, files, phone)",
        "Wipe surfaces and refresh your living space",
        "Organize your wardrobe",
        "Deep-clean one neglected area of your house",
        "Backup important files",
        "Unsubscribe from 10 email lists",
        "Detangle your cables"
    )
    for (title in cleaningTasks) {
        val sharedCategoryIcon = "\uD83D\uDEBF" // 🛁
        val docRef = taskCollection.document()
        val uid = docRef.id

        val task = mapOf(
            "uid" to uid,
            "title" to title,
            "category_title" to "Cleaning & Space Organization",
            "category_icon" to sharedCategoryIcon,
            "default_picture" to "https://firebasestorage.googleapis.com/v0/b/thriveon-a9f13.firebasestorage.app/o/default_images%2Fcleaning_space_organization.webp?alt=media&token=d2c55567-4f8c-41ab-8d6d-c9d21b7cd161",
            "milestones" to mapOf(
                "5" to mapOf("title" to "Tidiness Rookie", "badge" to "\uD83E\uDDF9"),         // 🧹
                "15" to mapOf("title" to "Balance Bringer", "badge" to "\u2696\uFE0F"),     // ⚖️
                "25" to mapOf("title" to "Flow Creator", "badge" to "\uD83C\uDF0A"),      // 🌊
                "40" to mapOf("title" to "Lifestyle Aligner", "badge" to "\uD83D\uDD2E"), // 🔮
                "60" to mapOf("title" to "Zen Master", "badge" to "\uD83E\uDDD8") // 🧘
            )
        )

        docRef.set(task)
            .addOnSuccessListener {
                println("Added Cleaning & Space Organization task: $title")
            }
            .addOnFailureListener { e ->
                println("Failed to add Cleaning & Space Organization task $title: $e")
            }
    }

    val sustainabilityTasks = listOf(
        "Avoid single-use plastics for the day",
        "Walk, bike, or use public transportation",
        "Recycle your trash",
        "Reduce energy use (lights, devices, etc.) for an hour",
        "Pick up litter or help clean a public area",
        "Plant a tree, herb, or native flower",
        "Repair a broken item instead of replacing it",
        "Air-dry clothes instead of using a dryer",
        "Sew a torn piece of clothing",
        "Plan meals to avoid food waste"
    )
    for (title in sustainabilityTasks) {
        val sharedCategoryIcon = "\uD83C\uDF3F" // 🌿
        val docRef = taskCollection.document()
        val uid = docRef.id

        val task = mapOf(
            "uid" to uid,
            "title" to title,
            "category_title" to "Sustainability & Environment",
            "category_icon" to sharedCategoryIcon,
            "default_picture" to "https://firebasestorage.googleapis.com/v0/b/thriveon-a9f13.firebasestorage.app/o/default_images%2Fsustainability_environment.webp?alt=media&token=fb0ea369-a50d-4388-8d1d-588dece6a836",
            "milestones" to mapOf(
                "5" to mapOf("title" to "Seed Planter", "badge" to "\uD83C\uDF3F"),           // 🌿
                "15" to mapOf("title" to "Energy Conserver", "badge" to "\uD83D\uDD0B"), // 🔋
                "25" to mapOf("title" to "Nature Ally", "badge" to "\uD83C\uDF31"),       // 🌱
                "40" to mapOf("title" to "Planet Protector", "badge" to "\u267B\uFE0F"), // ♻️
                "60" to mapOf("title" to "Earth Guardian", "badge" to "\uD83C\\uDF32")        // 🌲
            )
        )

        docRef.set(task)
            .addOnSuccessListener {
                println("Added Sustainability & Environment task: $title")
            }
            .addOnFailureListener { e ->
                println("Failed to add Sustainability & Environment task $title: $e")
            }
    }

    val focusTasks = listOf(
        "Complete a 30-minute deep focus session",
        "Create a to-do list and check off at least 3 tasks",
        "Finish a task you've been procrastinating",
        "Plan your day or week for better clarity",
        "Review your goals before starting work",
        "Set a power hour for your most important task",
        "Turn off notifications for 1+ hours",
        "Chew gum while working",
        "Cold shower before work",
        "Open only 3 essential browser tabs"
    )
    for (title in focusTasks) {
        val sharedCategoryIcon = "\uD83D\uDCCB" // 📋
        val docRef = taskCollection.document()
        val uid = docRef.id

        val task = mapOf(
            "uid" to uid,
            "title" to title,
            "category_title" to "Focus & Work Efficiency",
            "category_icon" to sharedCategoryIcon,
            "default_picture" to "https://firebasestorage.googleapis.com/v0/b/thriveon-a9f13.firebasestorage.app/o/default_images%2Ffocus_work_efficiency.webp?alt=media&token=5ede574a-75f3-46bc-985e-93a15eb3b173",
            "milestones" to mapOf(
                "5" to mapOf("title" to "Efficiency Starter", "badge" to "\u23F3"),             // ⏳
                "15" to mapOf("title" to "Task Achiever", "badge" to "\u26A1"),               // ⚡
                "25" to mapOf("title" to "Focus Expert", "badge" to "\uD83D\uDCDA"),    // 📚
                "40" to mapOf("title" to "Time Strategist", "badge" to "\uD83D\uDD52"),  // 🕒
                "60" to mapOf("title" to "Productivity Master", "badge" to "\uD83D\uDCC8")   // 📈
            )
        )

        docRef.set(task)
            .addOnSuccessListener {
                println("Added Focus & Work Efficiency task: $title")
            }
            .addOnFailureListener { e ->
                println("Failed to add Focus & Work Efficiency task $title: $e")
            }
    }
}
