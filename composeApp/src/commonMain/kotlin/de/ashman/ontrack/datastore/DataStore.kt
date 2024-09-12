//package de.ashman.ontrack.datastore
//
//import androidx.datastore.core.DataStore
//
//// TODO use DataStore in the future maybe
//class SessionManager() {
//
//    fun createDataStore(producePath: () -> String): DataStore<Preferences> =
//        PreferenceDataStoreFactory.createWithPath(
//            produceFile = { producePath().toPath() }
//        )
//
//    internal const val dataStoreFileName = "dice.preferences_pb"
//
//    fun saveUserId(userId: String) {
//    }
//
//    fun getUserId(): String? {
//    }
//}
