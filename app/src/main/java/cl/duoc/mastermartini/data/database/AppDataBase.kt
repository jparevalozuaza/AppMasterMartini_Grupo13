
package cl.duoc.mastermartini.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cl.duoc.mastermartini.data.dao.FavoritoDao
import cl.duoc.mastermartini.data.dao.RecetaDao
import cl.duoc.mastermartini.data.dao.UsuarioDao
import cl.duoc.mastermartini.data.entity.FavoritoEntity
import cl.duoc.mastermartini.data.entity.RecetaEntity
import cl.duoc.mastermartini.data.entity.UsuarioEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import cl.duoc.mastermartini.data.dao.ProgresoDao
import cl.duoc.mastermartini.data.entity.ProgresoEntity

@Database(
    entities = [
        RecetaEntity::class,
        FavoritoEntity::class,
        UsuarioEntity::class,
        ProgresoEntity::class
    ],
    version = 4,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun recetaDao(): RecetaDao
    abstract fun favoritoDao(): FavoritoDao
    abstract fun usuarioDao(): UsuarioDao

    abstract fun progresoDao(): ProgresoDao

    companion object {

        /*
         * Base de datos versión 1:
         * se agregan las tablas de favoritos y usuarios.
         */
        private val MIGRATION_1_3 = object : Migration(1, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                crearTablaFavoritos(db)
                crearTablaUsuarios(db)
            }
        }

        /*
         * Base de datos versión 2:
         * se crean únicamente las tablas que falten.
         * Esto permite integrar las dos variantes de la versión 2.
         */
        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                crearTablaFavoritos(db)
                crearTablaUsuarios(db)
            }
        }

        private fun crearTablaFavoritos(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS favoritos (
                    contenidoId INTEGER NOT NULL,
                    PRIMARY KEY(contenidoId)
                )
                """.trimIndent()
            )
        }

        private fun crearTablaUsuarios(db: SupportSQLiteDatabase) {
            db.execSQL(
                """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                    nombre TEXT NOT NULL,
                    email TEXT NOT NULL,
                    telefono TEXT NOT NULL,
                    rut TEXT NOT NULL,
                    tipoNegocio TEXT NOT NULL,
                    tamanoNegocio TEXT NOT NULL,
                    direccion TEXT NOT NULL,
                    intereses TEXT NOT NULL,
                    fotoPath TEXT,
                    claveHash TEXT NOT NULL
                )
                """.trimIndent()
            )

            db.execSQL(
                """
                CREATE UNIQUE INDEX IF NOT EXISTS index_usuarios_email
                ON usuarios (email)
                """.trimIndent()
            )
        }
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
            CREATE TABLE IF NOT EXISTS progreso (
                contenidoId INTEGER NOT NULL,
                completado INTEGER NOT NULL,
                PRIMARY KEY(contenidoId)
            )
            """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {

                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "martini_database"
                )
                    .addMigrations(
                        MIGRATION_1_3,
                        MIGRATION_2_3,
                        MIGRATION_3_4
                    )
                    .addCallback(object : RoomDatabase.Callback() {

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            CoroutineScope(Dispatchers.IO).launch {
                                INSTANCE?.recetaDao()?.insertarTodas(
                                    recetasIniciales()
                                )
                            }
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }

        private fun recetasIniciales(): List<RecetaEntity> {
            return listOf(
                RecetaEntity(
                    titulo = "Brownie de chocolate",
                    descripcion = "Brownie húmedo con intenso sabor a chocolate.",
                    categoria = "Pastelería",
                    tiempoPreparacion = 45,
                    ingredientes = "Chocolate, mantequilla, azúcar, huevos y harina.",
                    preparacion = "Derretir el chocolate con la mantequilla. Mezclar con el azúcar y los huevos, incorporar la harina y hornear hasta que el centro quede húmedo."
                ),
                RecetaEntity(
                    titulo = "Galletas con chips de chocolate",
                    descripcion = "Galletas caseras crujientes por fuera y suaves por dentro.",
                    categoria = "Pastelería",
                    tiempoPreparacion = 30,
                    ingredientes = "Harina, mantequilla, azúcar, huevo y chips de chocolate.",
                    preparacion = "Mezclar los ingredientes, agregar los chips de chocolate, formar las galletas y hornear hasta que los bordes estén dorados."
                ),
                RecetaEntity(
                    titulo = "Trufas de chocolate",
                    descripcion = "Pequeñas trufas ideales para compartir o vender.",
                    categoria = "Chocolatería",
                    tiempoPreparacion = 25,
                    ingredientes = "Chocolate, crema de leche y cacao en polvo.",
                    preparacion = "Calentar la crema, mezclarla con el chocolate picado, refrigerar la mezcla, formar bolitas y cubrirlas con cacao."
                ),
                RecetaEntity(
                    titulo = "Pan de molde casero",
                    descripcion = "Pan suave para preparar sándwiches.",
                    categoria = "Panadería",
                    tiempoPreparacion = 120,
                    ingredientes = "Harina, agua, levadura, sal, azúcar y aceite.",
                    preparacion = "Mezclar los ingredientes, amasar, dejar fermentar, colocar en un molde y hornear hasta que el pan esté dorado."
                ),
                RecetaEntity(
                    titulo = "Helado de chocolate",
                    descripcion = "Helado cremoso con sabor a chocolate.",
                    categoria = "Heladería",
                    tiempoPreparacion = 30,
                    ingredientes = "Leche, crema, azúcar y chocolate.",
                    preparacion = "Calentar la leche con el azúcar y el chocolate, dejar enfriar, incorporar la crema y llevar a congelación, mezclando periódicamente."
                )
            )
        }
    }
}
