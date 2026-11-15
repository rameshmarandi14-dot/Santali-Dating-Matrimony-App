package com.example.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AppPreferences
import com.example.data.model.MessageEntity
import com.example.data.model.ProfileEntity
import com.example.data.model.SwipeOutcome
import com.example.data.model.SwipeStatus
import com.example.data.model.VerificationRecordEntity
import com.example.data.repository.MatrimonyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MatrimonyUiState(
    val isLoading: Boolean = true,
    val preferences: AppPreferences = AppPreferences(),
    val currentUser: ProfileEntity? = null,
    val verificationRecord: VerificationRecordEntity? = null,
    val discoveryDeck: List<ProfileEntity> = emptyList(),
    val matches: List<ProfileEntity> = emptyList(),
    val incomingLikes: List<ProfileEntity> = emptyList(),
    val blockedProfiles: List<ProfileEntity> = emptyList(),
    val lastMessagesByMatch: Map<String, MessageEntity> = emptyMap(),
    val canRewind: Boolean = false
)

class MatrimonyViewModel(
    private val repository: MatrimonyRepository
) : ViewModel() {

    private val swipeHistory = MutableStateFlow<List<String>>(emptyList())

    private val _matchCelebration = MutableStateFlow<ProfileEntity?>(null)
    val matchCelebration: StateFlow<ProfileEntity?> = _matchCelebration.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureSeeded()
        }
    }

    val uiState: StateFlow<MatrimonyUiState> = combine(
        repository.preferencesFlow,
        repository.currentUserFlow,
        repository.verificationRecordFlow,
        repository.communityProfilesFlow,
        repository.blockedProfilesFlow,
        repository.allMessagesFlow,
        swipeHistory
    ) { values ->
        val prefs = values[0] as AppPreferences
        val currentUser = values[1] as ProfileEntity?
        val verification = values[2] as VerificationRecordEntity?
        @Suppress("UNCHECKED_CAST")
        val community = values[3] as List<ProfileEntity>
        @Suppress("UNCHECKED_CAST")
        val blocked = values[4] as List<ProfileEntity>
        @Suppress("UNCHECKED_CAST")
        val messages = values[5] as List<MessageEntity>
        @Suppress("UNCHECKED_CAST")
        val history = values[6] as List<String>

        val myClan = currentUser?.parsiClan.orEmpty()

        val discoveryDeck = community.filter { profile ->
            profile.swipeStatus == SwipeStatus.NONE &&
                profile.age in prefs.minAge..prefs.maxAge &&
                (!prefs.excludeMyClan || myClan.isEmpty() || !profile.parsiClan.equals(myClan, ignoreCase = true)) &&
                (prefs.preferredClans.isEmpty() || profile.parsiClan in prefs.preferredClans) &&
                (prefs.selectedState == "All Regions" || profile.state.equals(prefs.selectedState, ignoreCase = true)) &&
                (prefs.selectedEducation == "Any" || profile.educationCategory.equals(prefs.selectedEducation, ignoreCase = true)) &&
                (!prefs.verifiedOnly || profile.isVerified)
        }

        val matches = community.filter { it.swipeStatus == SwipeStatus.MATCHED }
        val incomingLikes = community.filter { it.likesCurrentUser && it.swipeStatus == SwipeStatus.NONE }

        val lastMsgMap = messages.groupBy { it.matchProfileId }
            .mapValues { (_, list) -> list.maxBy { it.timestampMillis } }

        MatrimonyUiState(
            isLoading = false,
            preferences = prefs,
            currentUser = currentUser,
            verificationRecord = verification,
            discoveryDeck = discoveryDeck,
            matches = matches,
            incomingLikes = incomingLikes,
            blockedProfiles = blocked,
            lastMessagesByMatch = lastMsgMap,
            canRewind = history.isNotEmpty()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = MatrimonyUiState()
    )

    fun observeChatMessages(matchProfileId: String): Flow<List<MessageEntity>> =
        repository.observeMessages(matchProfileId)

    fun observeProfile(profileId: String): Flow<ProfileEntity?> =
        repository.observeProfile(profileId)

    fun signInDemo() {
        viewModelScope.launch {
            _authError.value = null
            repository.signInDemoUser()
        }
    }

    fun loginWithCredentials(identifier: String, password: String) {
        viewModelScope.launch {
            _authError.value = null
            val result = repository.loginWithCredentials(identifier, password)
            result.onFailure { err ->
                _authError.value = err.message ?: "Login failed"
            }
        }
    }

    fun clearAuthError() {
        _authError.value = null
    }

    fun createProfileAndEnter(
        fullName: String,
        age: Int,
        gender: String,
        parsiClan: String,
        location: String,
        state: String,
        educationProfession: String,
        educationCategory: String,
        photoUri: String?,
        accountIdentifier: String?,
        accountPassword: String?
    ) {
        viewModelScope.launch {
            _authError.value = null
            repository.createOrUpdateCurrentUser(
                fullName = fullName,
                age = age,
                gender = gender,
                parsiClan = parsiClan,
                location = location,
                state = state,
                height = "5'7\"",
                educationProfession = educationProfession,
                educationCategory = educationCategory,
                aboutMe = "Proud of Santali heritage, looking for a caring and understanding partner.",
                interests = listOf("Santali Music", "Culture", "Sarhul"),
                photoUri = photoUri,
                galleryUris = emptyList(),
                markLoggedIn = true,
                accountIdentifier = accountIdentifier,
                accountPassword = accountPassword
            )
        }
    }

    fun saveBiodataChanges(
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
        galleryUris: List<String>
    ) {
        viewModelScope.launch {
            repository.createOrUpdateCurrentUser(
                fullName = fullName,
                age = age,
                gender = gender,
                parsiClan = parsiClan,
                location = location,
                state = state,
                height = height,
                educationProfession = educationProfession,
                educationCategory = educationCategory,
                aboutMe = aboutMe,
                interests = interests,
                photoUri = photoUri,
                galleryUris = galleryUris,
                markLoggedIn = false
            )
            _statusMessage.value = "Profile & Biodata updated"
        }
    }

    fun submitIdVerification(
        documentType: String,
        frontImageUri: String,
        backImageUri: String,
        selfieUri: String
    ) {
        viewModelScope.launch {
            repository.submitIdVerification(
                documentType = documentType,
                frontImageUri = frontImageUri,
                backImageUri = backImageUri,
                selfieUri = selfieUri
            )
            _statusMessage.value = "Blue Tick Verified Badge activated!"
        }
    }

    fun onAcceptProfile(profile: ProfileEntity) {
        viewModelScope.launch {
            swipeHistory.value = swipeHistory.value + profile.id
            when (val outcome = repository.acceptProfile(profile)) {
                is SwipeOutcome.Matched -> _matchCelebration.value = outcome.profile
                is SwipeOutcome.Liked -> _statusMessage.value = "Interest sent to ${profile.fullName}"
                is SwipeOutcome.Passed -> Unit
            }
        }
    }

    fun onRejectProfile(profile: ProfileEntity) {
        viewModelScope.launch {
            swipeHistory.value = swipeHistory.value + profile.id
            repository.rejectProfile(profile)
        }
    }

    fun onRewindLastSwipe() {
        val lastId = swipeHistory.value.lastOrNull() ?: return
        viewModelScope.launch {
            swipeHistory.value = swipeHistory.value.dropLast(1)
            repository.rewindProfile(lastId)
        }
    }

    fun resetPassedProfiles() {
        viewModelScope.launch {
            repository.resetPassedProfiles()
            swipeHistory.value = emptyList()
            _statusMessage.value = "Discovery deck refreshed"
        }
    }

    fun dismissMatchCelebration() {
        _matchCelebration.value = null
    }

    fun clearStatusMessage() {
        _statusMessage.value = null
    }

    fun sendMessage(matchProfileId: String, text: String) {
        viewModelScope.launch {
            repository.sendMessage(matchProfileId, text)
        }
    }

    fun blockProfile(profileId: String, name: String) {
        viewModelScope.launch {
            repository.blockProfile(profileId)
            _statusMessage.value = "$name has been blocked"
        }
    }

    fun unblockProfile(profileId: String) {
        viewModelScope.launch {
            repository.unblockProfile(profileId)
            _statusMessage.value = "User unblocked"
        }
    }

    fun applyFilters(
        excludeMyClan: Boolean,
        preferredClans: Set<String>,
        minAge: Int,
        maxAge: Int,
        selectedState: String,
        selectedEducation: String,
        verifiedOnly: Boolean
    ) {
        viewModelScope.launch {
            repository.updateFilters(
                excludeMyClan = excludeMyClan,
                preferredClans = preferredClans,
                minAge = minAge,
                maxAge = maxAge,
                selectedState = selectedState,
                selectedEducation = selectedEducation,
                verifiedOnly = verifiedOnly
            )
            _statusMessage.value = "Filters applied"
        }
    }

    fun resetFilters() {
        viewModelScope.launch {
            repository.resetFilters()
            _statusMessage.value = "Filters reset"
        }
    }

    fun setHideFromSameClan(value: Boolean) {
        viewModelScope.launch { repository.setHideFromSameClan(value) }
    }

    fun setPhotoPrivacyVerifiedOnly(value: Boolean) {
        viewModelScope.launch { repository.setPhotoPrivacyVerifiedOnly(value) }
    }

    fun setAllowCalls(value: Boolean) {
        viewModelScope.launch { repository.setAllowCalls(value) }
    }

    fun setNotificationsEnabled(value: Boolean) {
        viewModelScope.launch { repository.setNotificationsEnabled(value) }
    }

    fun verifyCurrentUser() {
        viewModelScope.launch {
            repository.verifyCurrentUserProfile()
            _statusMessage.value = "Community Verified Badge activated!"
        }
    }

    fun activatePremiumPlan(planId: String, planLabel: String) {
        viewModelScope.launch {
            repository.activatePremiumPlan(planId)
            _statusMessage.value = "Santali Juri $planLabel Premium activated!"
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            swipeHistory.value = emptyList()
            repository.deleteAndResetAccount()
        }
    }

    class Factory(private val repository: MatrimonyRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MatrimonyViewModel::class.java)) {
                return MatrimonyViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
