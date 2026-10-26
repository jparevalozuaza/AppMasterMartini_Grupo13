package cl.duoc.mastermartini.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cl.duoc.mastermartini.data.dao.RecetaDao
import cl.duoc.mastermartini.data.entity.RecetaEntity

@Database(
    entities = [RecetaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recetaDao(): RecetaDao

    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "martini_database"
                ).build()

                INSTANCE = instance
                instance
            }
        }
    }
}
