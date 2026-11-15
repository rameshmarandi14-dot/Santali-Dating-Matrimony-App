package com.example.data.repository

import com.example.data.local.SantaliDao
import com.example.data.local.SantaliSeedData
import com.example.data.local.UserPreferencesDataStore
import com.example.data.model.AppPreferences
import com.example.data.model.ClanPreferencesEntity
import com.example.data.model.LocalAccountEntity
import com.example.data.model.MessageEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SwipeOutcome
import com.example.data.model.SwipeStatus
import com.example.data.model.VerificationRecordEntity
import com.example.data.model.VerificationStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class MatrimonyRepository(
    private val dao: SantaliDao,
    private val prefsStore: UserPreferencesDataStore
) {
    private val seedMutex = Mutex()

    val preferencesFlow: Flow<AppPreferences> = combine(
        prefsStore.preferencesFlow,
        dao.observeClanPreferences()
    ) { sessionPrefs, clanPrefs ->
        val effectiveClanPrefs = clanPrefs ?: SantaliSeedData.initialClanPreferences()
        AppPreferences(
            isLoggedIn = sessionPrefs.isLoggedIn,
            excludeMyClan = effectiveClanPrefs.excludeMyClan,
            preferredClans = effectiveClanPrefs.preferredClans.toSet(),
            minAge = effectiveClanPrefs.minAge,
            maxAge = effectiveClanPrefs.maxAge,
            selectedState = effectiveClanPrefs.selectedState,
            selectedEducation = effectiveClanPrefs.selectedEducation,
            verifiedOnly = effectiveClanPrefs.verifiedOnly,
            hideFromSameClan = effectiveClanPrefs.hideFromSameClan,
            photoPrivacyVerifiedOnly = effectiveClanPrefs.photoPrivacyVerifiedOnly,
            allowCalls = effectiveClanPrefs.allowCalls,
            notificationsEnabled = effectiveClanPrefs.notificationsEnabled,
            isPremium = sessionPrefs.isPremium,
            premiumPlanId = sessionPrefs.premiumPlanId,
            boostProfileVisibility = sessionPrefs.boostProfileVisibility
        )
    }

    val currentUserFlow: Flow<ProfileEntity?> = dao.observeCurrentUser()
    val verificationRecordFlow: Flow<VerificationRecordEntity?> = dao.observeVerificationRecord()
    val communityProfilesFlow: Flow<List<ProfileEntity>> = dao.observeCommunityProfiles()
    val blockedProfilesFlow: Flow<List<ProfileEntity>> = dao.observeBlockedProfiles()
    val allMessagesFlow: Flow<List<MessageEntity>> = dao.observeAllMessages()

    suspend fun ensureSeeded() {
        seedMutex.withLock {
            if (dao.getProfileCount() == 0) {
                dao.insertProfiles(SantaliSeedData.initialProfiles())
                dao.upsertAccount(SantaliSeedData.initialAccount())
                dao.upsertClanPreferences(SantaliSeedData.initialClanPreferences())
                dao.insertMessages(SantaliSeedData.initialMessages())
            } else {
                if (dao.getClanPreferences() == null) {
                    dao.upsertClanPreferences(SantaliSeedData.initialClanPreferences())
                }
                if (dao.getAccountByProfileId() == null) {
                    dao.upsertAccount(SantaliSeedData.initialAccount())
                }
            }
        }
    }

    fun observeProfile(profileId: String): Flow<ProfileEntity?> = dao.observeProfileById(profileId)

    fun observeMessages(matchProfileId: String): Flow<List<MessageEntity>> =
        dao.observeMessagesForMatch(matchProfileId)

    suspend fun signInDemoUser() {
        ensureSeeded()
        prefsStore.setLoggedIn(true)
    }

    suspend fun loginWithCredentials(identifier: String, password: String): Result<Unit> {
        ensureSeeded()
        val normalized = identifier.trim()
        val existingAccount = dao.findAccountByIdentifier(normalized)
            ?: dao.getAccountByProfileId()

        if (existingAccount == null) {
            return Result.failure(IllegalArgumentException("No account found. Please register first."))
        }
        if (!existingAccount.identifier.equals(normalized, ignoreCase = true)) {
            return Result.failure(IllegalArgumentException("Account not found for \"$normalized\"."))
        }
        val inputHash = SantaliSeedData.hashPassword(password)
        if (existingAccount.passwordHash != inputHash) {
            return Result.failure(IllegalArgumentException("Incorrect password. Please try again."))
        }

        dao.upsertAccount(existingAccount.copy(lastLoginMillis = System.currentTimeMillis()))
        prefsStore.setLoggedIn(true)
        return Result.success(Unit)
    }

    suspend fun createOrUpdateCurrentUser(
        fullName: String,
        age: Int,
        gender: String,
        parsiClan: String,
        location: String,
        state: String,
        height: String,
        educationProfession: String,
        educationCategory: String,
        aboutMe: String,
        interests: List<String>,
        photoUri: String?,
        galleryUris: List<String>,
        markLoggedIn: Boolean = true,
        accountIdentifier: String? = null,
        accountPassword: String? = null
    ) {
        ensureSeeded()
        val existing = dao.getCurrentUser()
        val updated = (existing ?: SantaliSeedData.initialProfiles().first()).copy(
            id = "current_user",
            fullName = fullName.trim(),
            age = age,
            gender = gender,
            parsiClan = parsiClan,
            location = location.trim(),
            state = state,
            height = height.trim().ifEmpty { "5'7\"" },
            educationProfession = educationProfession.trim().ifEmpty { "Graduate" },
            educationCategory = educationCategory,
            aboutMe = aboutMe.trim().ifEmpty { "Proud of Santali heritage, looking for a supportive life partner." },
            interestsCsv = interests.joinToString(","),
            photoUri = photoUri ?: existing?.photoUri,
            galleryUrisCsv = galleryUris.joinToString("|"),
            isCurrentUser = true,
            lastUpdatedMillis = System.currentTimeMillis()
        )
        dao.upsertProfile(updated)
        if (!accountIdentifier.isNullOrBlank() && !accountPassword.isNullOrBlank()) {
            dao.upsertAccount(
                LocalAccountEntity(
                    profileId = "current_user",
                    identifier = accountIdentifier.trim(),
                    passwordHash = SantaliSeedData.hashPassword(accountPassword)
                )
            )
        }
        if (dao.getClanPreferences() == null) {
            dao.upsertClanPreferences(SantaliSeedData.initialClanPreferences())
        }
        if (markLoggedIn) {
            prefsStore.setLoggedIn(true)
        }
    }

    suspend fun submitIdVerification(
        documentType: String,
        frontImageUri: String,
        backImageUri: String,
        selfieUri: String
    ) {
        ensureSeeded()
        val record = VerificationRecordEntity(
            profileId = "current_user",
            documentType = documentType,
            frontImageUri = frontImageUri,
            backImageUri = backImageUri,
            selfieUri = selfieUri,
            status = VerificationStatus.VERIFIED,
            submittedAtMillis = System.currentTimeMillis()
        )
        dao.upsertVerificationRecord(record)
        val current = dao.getCurrentUser()
        if (current != null) {
            dao.upsertProfile(current.copy(isVerified = true, lastUpdatedMillis = System.currentTimeMillis()))
        }
    }

    suspend fun verifyCurrentUserProfile() {
        val current = dao.getCurrentUser() ?: return
        dao.upsertProfile(current.copy(isVerified = true))
    }

    suspend fun acceptProfile(profile: ProfileEntity): SwipeOutcome {
        val newStatus = if (profile.likesCurrentUser) SwipeStatus.MATCHED else SwipeStatus.LIKED
        dao.updateSwipeStatus(profile.id, newStatus)
        return if (newStatus == SwipeStatus.MATCHED) {
            SwipeOutcome.Matched(profile.copy(swipeStatus = SwipeStatus.MATCHED))
        } else {
            SwipeOutcome.Liked(profile.copy(swipeStatus = SwipeStatus.LIKED))
        }
    }

    suspend fun rejectProfile(profile: ProfileEntity): SwipeOutcome {
        dao.updateSwipeStatus(profile.id, SwipeStatus.PASSED)
        return SwipeOutcome.Passed(profile.copy(swipeStatus = SwipeStatus.PASSED))
    }

    suspend fun rewindProfile(profileId: String) {
        dao.updateSwipeStatus(profileId, SwipeStatus.NONE)
    }

    suspend fun resetPassedProfiles() {
        dao.resetPassedProfiles()
    }

    suspend fun sendMessage(matchProfileId: String, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) return
        dao.insertMessage(
            MessageEntity(
                matchProfileId = matchProfileId,
                senderIsMe = true,
                text = trimmed,
                timestampMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun blockProfile(profileId: String) {
        dao.setBlocked(profileId, true)
    }

    suspend fun unblockProfile(profileId: String) {
        dao.setBlocked(profileId, false)
    }

    suspend fun updateFilters(
        excludeMyClan: Boolean,
        preferredClans: Set<String>,
        minAge: Int,
        maxAge: Int,
        selectedState: String,
        selectedEducation: String,
        verifiedOnly: Boolean
    ) {
        val current = dao.getClanPreferences() ?: SantaliSeedData.initialClanPreferences()
        dao.upsertClanPreferences(
            current.copy(
                excludeMyClan = excludeMyClan,
                preferredClans = preferredClans.toList().sorted(),
                minAge = minAge,
                maxAge = maxAge,
                selectedState = selectedState,
                selectedEducation = selectedEducation,
                verifiedOnly = verifiedOnly,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun resetFilters() {
        val current = dao.getClanPreferences() ?: SantaliSeedData.initialClanPreferences()
        dao.upsertClanPreferences(
            current.copy(
                excludeMyClan = true,
                preferredClans = emptyList(),
                minAge = 20,
                maxAge = 34,
                selectedState = "All Regions",
                selectedEducation = "Any",
                verifiedOnly = false,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun setHideFromSameClan(value: Boolean) {
        val current = dao.getClanPreferences() ?: SantaliSeedData.initialClanPreferences()
        dao.upsertClanPreferences(
            current.copy(
                hideFromSameClan = value,
                excludeMyClan = value,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun setPhotoPrivacyVerifiedOnly(value: Boolean) {
        val current = dao.getClanPreferences() ?: SantaliSeedData.initialClanPreferences()
        dao.upsertClanPreferences(
            current.copy(
                photoPrivacyVerifiedOnly = value,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun setAllowCalls(value: Boolean) {
        val current = dao.getClanPreferences() ?: SantaliSeedData.initialClanPreferences()
        dao.upsertClanPreferences(
            current.copy(
                allowCalls = value,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun setNotificationsEnabled(value: Boolean) {
        val current = dao.getClanPreferences() ?: SantaliSeedData.initialClanPreferences()
        dao.upsertClanPreferences(
            current.copy(
                notificationsEnabled = value,
                updatedAtMillis = System.currentTimeMillis()
            )
        )
    }

    suspend fun activatePremiumPlan(planId: String) {
        prefsStore.activatePremium(planId)
    }

    suspend fun logout() {
        prefsStore.setLoggedIn(false)
    }

    suspend fun deleteAndResetAccount() {
        dao.clearAllMessages()
        dao.clearVerificationRecords()
        dao.clearClanPreferences()
        dao.clearAccounts()
        dao.clearAllProfiles()
        prefsStore.clearAll()
        dao.insertProfiles(SantaliSeedData.initialProfiles())
        dao.upsertAccount(SantaliSeedData.initialAccount())
        dao.upsertClanPreferences(SantaliSeedData.initialClanPreferences())
        dao.insertMessages(SantaliSeedData.initialMessages())
    }
}
