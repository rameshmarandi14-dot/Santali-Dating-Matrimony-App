package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.ClanPreferencesEntity
import com.example.data.model.LocalAccountEntity
import com.example.data.model.MessageEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SwipeStatus
import com.example.data.model.VerificationRecordEntity
import java.security.MessageDigest

@Database(
    entities = [
        ProfileEntity::class,
        LocalAccountEntity::class,
        VerificationRecordEntity::class,
        ClanPreferencesEntity::class,
        MessageEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(SantaliTypeConverters::class)
abstract class SantaliDatabase : RoomDatabase() {
    abstract fun santaliDao(): SantaliDao

    companion object {
        @Volatile
        private var INSTANCE: SantaliDatabase? = null

        fun getInstance(context: Context): SantaliDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SantaliDatabase::class.java,
                    "santali_juri.db"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

object SantaliSeedData {
    fun hashPassword(raw: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun initialAccount(): LocalAccountEntity {
        return LocalAccountEntity(
            profileId = "current_user",
            identifier = "+91 98765 43210",
            passwordHash = hashPassword("santali123")
        )
    }

    fun initialClanPreferences(): ClanPreferencesEntity {
        return ClanPreferencesEntity(
            profileId = "current_user",
            excludeMyClan = true,
            preferredClans = emptyList(),
            minAge = 20,
            maxAge = 34,
            selectedState = "All Regions",
            selectedEducation = "Any",
            verifiedOnly = false,
            hideFromSameClan = true,
            photoPrivacyVerifiedOnly = false,
            allowCalls = true,
            notificationsEnabled = true
        )
    }
    fun initialProfiles(): List<ProfileEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            ProfileEntity(
                id = "current_user",
                fullName = "Rohan Soren",
                age = 26,
                gender = "Male",
                parsiClan = "Soren",
                location = "Ranchi, Jharkhand",
                state = "Jharkhand",
                height = "5'9\"",
                educationProfession = "B.Tech • Software Engineer",
                educationCategory = "Engineer",
                aboutMe = "Proud of Santali heritage, looking for a caring and understanding partner.",
                interestsCsv = "Santali Music,Culture,Technology,Trekking",
                photoResName = "portrait_rohan",
                isVerified = false,
                isOnline = true,
                isCurrentUser = true,
                likesCurrentUser = false,
                swipeStatus = SwipeStatus.NONE,
                lastUpdatedMillis = now
            ),
            ProfileEntity(
                id = "moni_hembram",
                fullName = "Moni Hembram",
                age = 24,
                gender = "Female",
                parsiClan = "Hembram",
                location = "Ranchi, Jharkhand",
                state = "Jharkhand",
                height = "5'4\"",
                educationProfession = "M.Sc • Primary School Teacher",
                educationCategory = "Teacher / Academic",
                aboutMe = "Warm, family-oriented, and passionate about Santali folk songs, Sohrai painting, and community teaching.",
                interestsCsv = "Santali Music,Sohrai Art,Culture,Teaching",
                photoResName = "portrait_moni",
                isVerified = true,
                isOnline = true,
                likesCurrentUser = true,
                swipeStatus = SwipeStatus.NONE,
                lastUpdatedMillis = now - 1000L
            ),
            ProfileEntity(
                id = "anjali_marandi",
                fullName = "Anjali Marandi",
                age = 25,
                gender = "Female",
                parsiClan = "Marandi",
                location = "Bankura, West Bengal",
                state = "West Bengal",
                height = "5'5\"",
                educationProfession = "B.Sc Nursing • Staff Nurse",
                educationCategory = "Medical / Healthcare",
                aboutMe = "Dedicated healthcare professional who loves Sarhul celebrations, reading Santali literature, and cooking traditional dishes.",
                interestsCsv = "Sarhul,Literature,Cooking,Nature",
                photoResName = "portrait_sita",
                isVerified = true,
                isOnline = false,
                likesCurrentUser = true,
                swipeStatus = SwipeStatus.NONE,
                lastUpdatedMillis = now - 2000L
            ),
            ProfileEntity(
                id = "sunita_besra",
                fullName = "Sunita Besra",
                age = 23,
                gender = "Female",
                parsiClan = "Besra",
                location = "Baripada, Odisha",
                state = "Odisha",
                height = "5'3\"",
                educationProfession = "MBA • Banking Associate",
                educationCategory = "Postgraduate",
                aboutMe = "Cheerful and grounded. Enjoy traditional dance, exploring hills around Simlipal, and spending weekends with family.",
                interestsCsv = "Dance,Trekking,Culture,Nature",
                photoResName = "portrait_baha",
                isVerified = true,
                isOnline = true,
                likesCurrentUser = false,
                swipeStatus = SwipeStatus.NONE,
                lastUpdatedMillis = now - 3000L
            ),
            ProfileEntity(
                id = "biren_murmu",
                fullName = "Biren Murmu",
                age = 27,
                gender = "Male",
                parsiClan = "Murmu",
                location = "Dumka, Jharkhand",
                state = "Jharkhand",
                height = "5'10\"",
                educationProfession = "B.A • Civil Services Aspirant",
                educationCategory = "Government Service",
                aboutMe = "Active in Santali youth cultural forums and passionate about Ol Chiki literature and sports.",
                interestsCsv = "Literature,Sports,Culture,Sarhul",
                photoResName = "portrait_somai",
                isVerified = true,
                isOnline = true,
                likesCurrentUser = false,
                swipeStatus = SwipeStatus.NONE,
                lastUpdatedMillis = now - 4000L
            ),
            ProfileEntity(
                id = "kajal_kisku",
                fullName = "Kajal Kisku",
                age = 24,
                gender = "Female",
                parsiClan = "Kisku",
                location = "Purulia, West Bengal",
                state = "West Bengal",
                height = "5'4\"",
                educationProfession = "M.A • Ol Chiki Educator",
                educationCategory = "Teacher / Academic",
                aboutMe = "Teaching Ol Chiki script to young students. Looking for a respectful, kind-hearted companion.",
                interestsCsv = "Literature,Teaching,Santali Music,Sohrai Art",
                photoResName = "portrait_rani",
                isVerified = false,
                isOnline = false,
                likesCurrentUser = true,
                swipeStatus = SwipeStatus.NONE,
                lastUpdatedMillis = now - 5000L
            ),
            // Incoming Likes (Likes You tab)
            ProfileEntity(
                id = "baha_tudu",
                fullName = "Baha Tudu",
                age = 25,
                gender = "Female",
                parsiClan = "Tudu",
                location = "Chaibasa, Jharkhand",
                state = "Jharkhand",
                height = "5'4\"",
                educationProfession = "B.Com • State Govt. Clerk",
                educationCategory = "Government Service",
                aboutMe = "Simple, joyful, and deeply rooted in Santali values. Love Sarhul blossoms and peaceful village mornings.",
                interestsCsv = "Sarhul,Dance,Cooking,Culture",
                photoResName = "portrait_baha",
                isVerified = true,
                isOnline = true,
                likesCurrentUser = true,
                swipeStatus = SwipeStatus.NONE,
                lastUpdatedMillis = now - 6000L
            ),
            ProfileEntity(
                id = "priya_soren",
                fullName = "Priya Soren",
                age = 24,
                gender = "Female",
                parsiClan = "Soren",
                location = "Bokaro, Jharkhand",
                state = "Jharkhand",
                height = "5'5\"",
                educationProfession = "B.Tech • Data Analyst",
                educationCategory = "Engineer",
                aboutMe = "Tech enthusiast who enjoys music, photography, and community gatherings.",
                interestsCsv = "Technology,Santali Music,Trekking",
                photoResName = "portrait_moni",
                isVerified = true,
                isOnline = false,
                likesCurrentUser = false,
                swipeStatus = SwipeStatus.NONE,
                lastUpdatedMillis = now - 7000L
            ),
            // Existing Matches
            ProfileEntity(
                id = "lila_murmu",
                fullName = "Lila Murmu",
                age = 24,
                gender = "Female",
                parsiClan = "Murmu",
                location = "Dumka, Jharkhand",
                state = "Jharkhand",
                height = "5'5\"",
                educationProfession = "B.Ed • High School Teacher",
                educationCategory = "Teacher / Academic",
                aboutMe = "Teacher from Dumka who loves traditional Santali music, Sohrai wall art, and meaningful conversations.",
                interestsCsv = "Santali Music,Sohrai Art,Teaching,Nature",
                photoResName = "portrait_lila",
                isVerified = true,
                isOnline = true,
                likesCurrentUser = true,
                swipeStatus = SwipeStatus.MATCHED,
                lastUpdatedMillis = now - 8000L
            ),
            ProfileEntity(
                id = "sita_hansda",
                fullName = "Sita Hansda",
                age = 23,
                gender = "Female",
                parsiClan = "Hansda",
                location = "Jamshedpur, Jharkhand",
                state = "Jharkhand",
                height = "5'4\"",
                educationProfession = "B.Sc • Lab Technologist",
                educationCategory = "Medical / Healthcare",
                aboutMe = "Living in Jamshedpur, close to family. Enjoy reading, cooking, and community festivals.",
                interestsCsv = "Cooking,Culture,Sarhul,Literature",
                photoResName = "portrait_sita",
                isVerified = true,
                isOnline = true,
                likesCurrentUser = true,
                swipeStatus = SwipeStatus.MATCHED,
                lastUpdatedMillis = now - 9000L
            ),
            ProfileEntity(
                id = "rani_baskey",
                fullName = "Rani Baskey",
                age = 25,
                gender = "Female",
                parsiClan = "Baskey",
                location = "Mayurbhanj, Odisha",
                state = "Odisha",
                height = "5'5\"",
                educationProfession = "M.Com • Finance Executive",
                educationCategory = "Postgraduate",
                aboutMe = "From Mayurbhanj. Believe in mutual respect, simplicity, and building a supportive home together.",
                interestsCsv = "Dance,Nature,Cooking,Culture",
                photoResName = "portrait_rani",
                isVerified = true,
                isOnline = false,
                likesCurrentUser = true,
                swipeStatus = SwipeStatus.MATCHED,
                lastUpdatedMillis = now - 10000L
            ),
            ProfileEntity(
                id = "somai_kisku",
                fullName = "Somai Kisku",
                age = 26,
                gender = "Male",
                parsiClan = "Kisku",
                location = "Kokrajhar, Assam",
                state = "Assam",
                height = "5'9\"",
                educationProfession = "B.E • Civil Engineer",
                educationCategory = "Engineer",
                aboutMe = "Civil engineer working on community infrastructure. Love football, folk flute, and travel.",
                interestsCsv = "Sports,Santali Music,Trekking,Technology",
                photoResName = "portrait_somai",
                isVerified = true,
                isOnline = true,
                likesCurrentUser = true,
                swipeStatus = SwipeStatus.MATCHED,
                lastUpdatedMillis = now - 11000L
            )
        )
    }

    fun initialMessages(): List<MessageEntity> {
        val now = System.currentTimeMillis()
        return listOf(
            MessageEntity(
                matchProfileId = "lila_murmu",
                senderIsMe = false,
                text = "Johar Rohan! Glad we matched. How is life in Ranchi?",
                timestampMillis = now - 3_600_000L
            ),
            MessageEntity(
                matchProfileId = "lila_murmu",
                senderIsMe = true,
                text = "Johar Lila! Everything is going well here. I saw you teach in Dumka!",
                timestampMillis = now - 3_200_000L
            ),
            MessageEntity(
                matchProfileId = "lila_murmu",
                senderIsMe = false,
                text = "Yes! Teaching high school students and helping with Sohrai art workshops on weekends.",
                timestampMillis = now - 1_800_000L
            ),
            MessageEntity(
                matchProfileId = "sita_hansda",
                senderIsMe = false,
                text = "Johar! Nice to connect with you on Santali Juri.",
                timestampMillis = now - 7_200_000L
            ),
            MessageEntity(
                matchProfileId = "rani_baskey",
                senderIsMe = false,
                text = "Hello Rohan, greetings from Mayurbhanj!",
                timestampMillis = now - 18_000_000L
            )
        )
    }
}
