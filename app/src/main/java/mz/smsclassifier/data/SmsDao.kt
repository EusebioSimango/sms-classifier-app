package mz.smsclassifier.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SmsDao {
    @Query("SELECT * FROM sms ORDER BY receivedAtMillis DESC")
    fun observeAll(): Flow<List<SmsEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: SmsEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(entities: List<SmsEntity>)

    @Query(
        "SELECT * FROM sms WHERE outgoing = 0 AND (modelVersion IS NULL OR modelVersion != :modelVersion) " +
            "ORDER BY receivedAtMillis DESC LIMIT :limit",
    )
    suspend fun pendingClassification(modelVersion: String, limit: Int): List<SmsEntity>

    @Query(
        "UPDATE sms SET category = :category, confidence = :confidence, " +
            "modelVersion = :modelVersion WHERE id = :id",
    )
    suspend fun updateClassification(id: Long, category: String, confidence: Float?, modelVersion: String)
}
