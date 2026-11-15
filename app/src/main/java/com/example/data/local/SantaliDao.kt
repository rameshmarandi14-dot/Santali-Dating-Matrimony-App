package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import com.example.data.model.ClanPreferencesEntity
import com.example.data.model.LocalAccountEntity
import com.example.data.model.MessageEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.VerificationRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SantaliDao {

    @Query("SELECT * FROM profiles WHERE isCurrentUser = 1 LIMIT 1")
    fun observeCurrentUser(): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE isCurrentUser = 1 LIMIT 1")
    suspend fun getCurrentUser(): ProfileEntity?

    @Query("SELECT * FROM profiles WHERE isCurrentUser = 0 AND isBlocked = 0 ORDER BY lastUpdatedMillis DESC")
    fun observeCommunityProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE isCurrentUser = 0 AND isBlocked = 1 ORDER BY fullName ASC")
    fun observeBlockedProfiles(): Flow<List<ProfileEntity>>

    @Query("SELECT * FROM profiles WHERE id = :profileId LIMIT 1")
    fun observeProfileById(profileId: String): Flow<ProfileEntity?>

    @Query("SELECT * FROM profiles WHERE id = :profileId LIMIT 1")
    suspend fun getProfileById(profileId: String): ProfileEntity?

    @Query("SELECT COUNT(*) FROM profiles")
    suspend fun getProfileCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<ProfileEntity>)

    @Upsert
    suspend fun upsertProfile(profile: ProfileEntity)

    @Update
    suspend fun updateProfile(profile: ProfileEntity)

    @Query("UPDATE profiles SET swipeStatus = :status, lastUpdatedMillis = :timestamp WHERE id = :profileId")
    suspend fun updateSwipeStatus(profileId: String, status: String, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE profiles SET isBlocked = :blocked WHERE id = :profileId")
    suspend fun setBlocked(profileId: String, blocked: Boolean)

    @Query("UPDATE profiles SET swipeStatus = 'NONE' WHERE isCurrentUser = 0 AND swipeStatus = 'PASSED'")
    suspend fun resetPassedProfiles()

    // Local Account Credentials
    @Query("SELECT * FROM local_accounts WHERE profileId = :profileId LIMIT 1")
    suspend fun getAccountByProfileId(profileId: String = "current_user"): LocalAccountEntity?

    @Query("SELECT * FROM local_accounts WHERE LOWER(identifier) = LOWER(:identifier) LIMIT 1")
    suspend fun findAccountByIdentifier(identifier: String): LocalAccountEntity?

    @Upsert
    suspend fun upsertAccount(account: LocalAccountEntity)

    @Query("DELETE FROM local_accounts")
    suspend fun clearAccounts()

    // Verification Records
    @Query("SELECT * FROM verification_records WHERE profileId = :profileId LIMIT 1")
    fun observeVerificationRecord(profileId: String = "current_user"): Flow<VerificationRecordEntity?>

    @Query("SELECT * FROM verification_records WHERE profileId = :profileId LIMIT 1")
    suspend fun getVerificationRecord(profileId: String = "current_user"): VerificationRecordEntity?

    @Upsert
    suspend fun upsertVerificationRecord(record: VerificationRecordEntity)

    @Query("DELETE FROM verification_records")
    suspend fun clearVerificationRecords()

    // Clan Preferences
    @Query("SELECT * FROM clan_preferences WHERE profileId = :profileId LIMIT 1")
    fun observeClanPreferences(profileId: String = "current_user"): Flow<ClanPreferencesEntity?>

    @Query("SELECT * FROM clan_preferences WHERE profileId = :profileId LIMIT 1")
    suspend fun getClanPreferences(profileId: String = "current_user"): ClanPreferencesEntity?

    @Upsert
    suspend fun upsertClanPreferences(preferences: ClanPreferencesEntity)

    @Query("DELETE FROM clan_preferences")
    suspend fun clearClanPreferences()

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE matchProfileId = :matchProfileId ORDER BY timestampMillis ASC")
    fun observeMessagesForMatch(matchProfileId: String): Flow<List<MessageEntity>>

    @Query("SELECT * FROM chat_messages ORDER BY timestampMillis DESC")
    fun observeAllMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    @Query("DELETE FROM chat_messages")
    suspend fun clearAllMessages()

    @Query("DELETE FROM profiles")
    suspend fun clearAllProfiles()
}
