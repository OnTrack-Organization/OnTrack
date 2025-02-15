package de.ashman.ontrack.db

import dev.gitlive.firebase.firestore.FirebaseFirestore

interface UserService {
}

class UserServiceImpl(
    firestore: FirebaseFirestore,
) : UserService {

}