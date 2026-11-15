package com.example.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

object SantaliConstants {
    val ALL_PARSI_CLANS = listOf(
        "Murmu",
        "Soren",
        "Hansda",
        "Marandi",
        "Kisku",
        "Tudu",
        "Baskey",
        "Besra",
        "Hembram",
        "Pauria",
        "Chore",
        "Bedea"
    )

    val STATES = listOf(
        "All Regions",
        "Jharkhand",
        "Odisha",
        "West Bengal",
        "Assam",
        "Bihar"
    )

    val EDUCATION_OPTIONS = listOf(
        "Any",
        "Graduate",
        "Postgraduate",
        "Engineer",
        "Medical / Healthcare",
        "Teacher / Academic",
        "Government Service"
    )

    val GENDERS = listOf("Male", "Female", "Other")

    val INTEREST_OPTIONS = listOf(
        "Santali Music",
        "Sohrai Art",
        "Sarhul",
        "Culture",
        "Literature",
        "Teaching",
        "Trekking",
        "Dance",
        "Cooking",
        "Technology",
        "Sports",
        "Nature"
    )

    val ICEBREAKERS = listOf(
        "Johar! Nice to connect with you.",
        "Which district or village are you from?",
        "Would you like to chat about family & interests?",
        "Let's connect over a quick call sometime!"
    )

    val GOV_ID_TYPES = listOf(
        "Aadhaar Card",
        "PAN Card",
        "Voter ID",
        "Driving License"
    )
}

@Entity(
    tableName = "profiles",
    indices = [
        Index(value = ["parsiClan"]),
        Index(value = ["swipeStatus"]),
        Index(value = ["isCurrentUser"])
    ]
)
data class ProfileEntity(
    @PrimaryKey val id: String,
    val fullName: String,
    val age: Int,
    val gender: String,
    val parsiClan: String,
    val location: String,
    val state: String,
    val height: String,
    val educationProfession: String,
    val educationCategory: String,
    val aboutMe: String,
    val interestsCsv: String,
    val photoResName: String,
    val photoUri: String? = null,
    val galleryUrisCsv: String = "",
    val isVerified: Boolean = false,
    val isOnline: Boolean = false,
    val isCurrentUser: Boolean = false,
    val likesCurrentUser: Boolean = false,
    val swipeStatus: String = SwipeStatus.NONE,
    val isBlocked: Boolean = false,
    val lastUpdatedMillis: Long = System.currentTimeMillis()
) {
    val interestsList: List<String>
        get() = interestsCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() }

    val galleryUris: List<String>
        get() = galleryUrisCsv.split("|").map { it.trim() }.filter { it.isNotEmpty() }
}

@Entity(
    tableName = "local_accounts",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["identifier"], unique = true)]
)
data class LocalAccountEntity(
    @PrimaryKey val profileId: String = "current_user",
    val identifier: String,
    val passwordHash: String,
    val lastLoginMillis: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "verification_records",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class VerificationRecordEntity(
    @PrimaryKey val profileId: String = "current_user",
    val documentType: String,
    val frontImageUri: String,
    val backImageUri: String,
    val selfieUri: String,
    val status: String = VerificationStatus.VERIFIED,
    val submittedAtMillis: Long = System.currentTimeMillis()
)

object VerificationStatus {
    const val UNVERIFIED = "UNVERIFIED"
    const val PENDING = "PENDING"
    const val VERIFIED = "VERIFIED"
}

@Entity(
    tableName = "clan_preferences",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["profileId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ClanPreferencesEntity(
    @PrimaryKey val profileId: String = "current_user",
    val excludeMyClan: Boolean = true,
    val preferredClans: List<String> = emptyList(),
    val minAge: Int = 20,
    val maxAge: Int = 34,
    val selectedState: String = "All Regions",
    val selectedEducation: String = "Any",
    val verifiedOnly: Boolean = false,
    val hideFromSameClan: Boolean = true,
    val photoPrivacyVerifiedOnly: Boolean = false,
    val allowCalls: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

object SwipeStatus {
    const val NONE = "NONE"
    const val LIKED = "LIKED"
    const val PASSED = "PASSED"
    const val MATCHED = "MATCHED"
}

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ProfileEntity::class,
            parentColumns = ["id"],
            childColumns = ["matchProfileId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["matchProfileId"]),
        Index(value = ["timestampMillis"])
    ]
)
data class MessageEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val matchProfileId: String,
    val senderIsMe: Boolean,
    val text: String,
    val timestampMillis: Long = System.currentTimeMillis()
)

data class PremiumPlan(
    val id: String,
    val durationLabel: String,
    val badgeLabel: String? = null,
    val subtitleLabel: String? = null,
    val priceInr: Int,
    val perks: List<String>
)

object PremiumPlans {
    val ALL = listOf(
        PremiumPlan(
            id = "1_month",
            durationLabel = "1 Month",
            badgeLabel = null,
            subtitleLabel = null,
            priceInr = 199,
            perks = listOf(
                "Unlimited Swipes & Likes",
                "Rewind last profile"
            )
        ),
        PremiumPlan(
            id = "6_months",
            durationLabel = "6 Months",
            badgeLabel = "POPULAR",
            subtitleLabel = "Popular",
            priceInr = 599,
            perks = listOf(
                "Unlimited Swipes & Likes",
                "See Who Liked You",
                "Rewind last profile"
            )
        ),
        PremiumPlan(
            id = "12_months",
            durationLabel = "12 Months",
            badgeLabel = "BEST VALUE",
            subtitleLabel = "Best Value",
            priceInr = 899,
            perks = listOf(
                "All perks above",
                "Parsi/Clan Filter",
                "Boost Profile Visibility"
            )
        )
    )
}

data class AppPreferences(
    val isLoggedIn: Boolean = false,
    val excludeMyClan: Boolean = true,
    val preferredClans: Set<String> = emptySet(),
    val minAge: Int = 20,
    val maxAge: Int = 34,
    val selectedState: String = "All Regions",
    val selectedEducation: String = "Any",
    val verifiedOnly: Boolean = false,
    val hideFromSameClan: Boolean = true,
    val photoPrivacyVerifiedOnly: Boolean = false,
    val allowCalls: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val isPremium: Boolean = false,
    val premiumPlanId: String? = null,
    val boostProfileVisibility: Boolean = false
)

sealed interface SwipeOutcome {
    data class Matched(val profile: ProfileEntity) : SwipeOutcome
    data class Liked(val profile: ProfileEntity) : SwipeOutcome
    data class Passed(val profile: ProfileEntity) : SwipeOutcome
}
