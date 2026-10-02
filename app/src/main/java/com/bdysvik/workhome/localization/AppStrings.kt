package com.bdysvik.workhome.localization

import androidx.compose.runtime.compositionLocalOf

interface AppStrings {
    // App info & common
    val appName: String
    val language: String
    val selectLanguage: String
    val english: String
    val norwegian: String
    val cancel: String
    val save: String
    val delete: String
    val edit: String
    val working: String
    val signOut: String

    // Login screen
    val signIn: String
    val createInvitedAccount: String
    val email: String
    val password: String
    val alreadyHaveAccount: String
    val needToClaimInvite: String

    // Setup & missing profile
    val setupRequiredTitle: String
    val setupRequiredBody: String
    val profileNotFoundTitle: String
    val profileNotFoundDefaultBody: String

    // Navigation
    val navChores: String
    val navAddChore: String
    val navMinutes: String
    val navUsers: String

    // Chores screen
    val totalMinutes: String
    val daysLeft: String
    val activeChores: String
    val noActiveChores: String
    val completedChores: String
    val noCompletedChores: String
    val choreTemplates: String
    val newTemplate: String
    val editTemplate: String
    val templateTitle: String
    val rewardMinutes: String
    val recurrence: String
    val recurrenceManual: String
    val recurrenceDaily: String
    val recurrenceTwoDays: String
    val recurrenceThreeDays: String
    val recurrenceWeekly: String
    fun recurrenceEveryNDays(days: Int): String
    val activate: String
    val complete: String
    val completed: String
    val awaitingApproval: String
    val approve: String
    val reject: String
    val you: String
    val resetAssignment: String

    // Create chore screen
    val createChoreTitle: String
    val choreTitleLabel: String
    val descriptionOptional: String
    val assignee: String
    val unassigned: String
    val markAsCompleted: String
    val createChoreButton: String

    // Rewards screen
    val rewardsTitle: String
    val minuteBalance: String

    // Users screen
    val familyMembers: String
    val pendingInvites: String
    val inviteUser: String
    val name: String
    val role: String
    val roleAdmin: String
    val roleMember: String
    val sendInvite: String
}

object EnglishStrings : AppStrings {
    override val appName = "WorkHome"
    override val language = "Language"
    override val selectLanguage = "Select language"
    override val english = "English"
    override val norwegian = "Norwegian"
    override val cancel = "Cancel"
    override val save = "Save"
    override val delete = "Delete"
    override val edit = "Edit"
    override val working = "Working..."
    override val signOut = "Sign out"

    override val signIn = "Sign in"
    override val createInvitedAccount = "Create invited account"
    override val email = "Email"
    override val password = "Password"
    override val alreadyHaveAccount = "Already have an account? Sign in"
    override val needToClaimInvite = "Need to claim an invite? Create account"

    override val setupRequiredTitle = "Firebase setup required"
    override val setupRequiredBody = "Copy your Firebase app config into app/google-services.json, then rebuild the app."
    override val profileNotFoundTitle = "Profile not found"
    override val profileNotFoundDefaultBody = "Your Firebase account is signed in, but no matching Firestore family profile exists yet. Ask an admin to add an invite for your email, or manually create the first admin profile in Firestore."

    override val navChores = "Chores"
    override val navAddChore = "Add chore"
    override val navMinutes = "Minutes"
    override val navUsers = "Users"

    override val totalMinutes = "Total minutes"
    override val daysLeft = "Days left"
    override val activeChores = "Active chores"
    override val noActiveChores = "No active chores right now"
    override val completedChores = "Completed chores"
    override val noCompletedChores = "No completed chores this month"
    override val choreTemplates = "Chore templates"
    override val newTemplate = "New template"
    override val editTemplate = "Edit template"
    override val templateTitle = "Template title"
    override val rewardMinutes = "Reward (minutes)"
    override val recurrence = "Recurrence"
    override val recurrenceManual = "Manual"
    override val recurrenceDaily = "Every day"
    override val recurrenceTwoDays = "Every 2 days"
    override val recurrenceThreeDays = "Every 3 days"
    override val recurrenceWeekly = "Every week"
    override fun recurrenceEveryNDays(days: Int) = "Every $days days"
    override val activate = "Activate"
    override val complete = "Complete"
    override val completed = "Completed"
    override val awaitingApproval = "Awaiting approval"
    override val approve = "Approve"
    override val reject = "Reject"
    override val you = "you"
    override val resetAssignment = "Reset assignment"

    override val createChoreTitle = "Create chore"
    override val choreTitleLabel = "Chore title"
    override val descriptionOptional = "Description (optional)"
    override val assignee = "Assignee"
    override val unassigned = "Unassigned"
    override val markAsCompleted = "Mark as completed"
    override val createChoreButton = "Create"

    override val rewardsTitle = "Minutes"
    override val minuteBalance = "Minute balance"

    override val familyMembers = "Family members"
    override val pendingInvites = "Pending invites"
    override val inviteUser = "Invite user"
    override val name = "Name"
    override val role = "Role"
    override val roleAdmin = "Admin"
    override val roleMember = "Member"
    override val sendInvite = "Send invite"
}

object NorwegianStrings : AppStrings {
    override val appName = "WorkHome"
    override val language = "Språk"
    override val selectLanguage = "Velg språk"
    override val english = "English"
    override val norwegian = "Norsk"
    override val cancel = "Avbryt"
    override val save = "Lagre"
    override val delete = "Slett"
    override val edit = "Rediger"
    override val working = "Vennligst vent..."
    override val signOut = "Logg ut"

    override val signIn = "Logg inn"
    override val createInvitedAccount = "Opprett invitert konto"
    override val email = "E-post"
    override val password = "Passord"
    override val alreadyHaveAccount = "Har du allerede en konto? Logg inn"
    override val needToClaimInvite = "Mottatt en invitasjon? Opprett konto"

    override val setupRequiredTitle = "Firebase-oppsett kreves"
    override val setupRequiredBody = "Kopier Firebase-appkonfigurasjonen til app/google-services.json, og bygg appen på nytt."
    override val profileNotFoundTitle = "Profil ikke funnet"
    override val profileNotFoundDefaultBody = "Firebase-kontoen din er logget inn, men det finnes ingen tilsvarende Firestore-familieprofil ennå. Be en administrator om en invitasjon for din e-post, eller opprett den første administratorprofilen manuelt i Firestore."

    override val navChores = "Gjøremål"
    override val navAddChore = "Legg til"
    override val navMinutes = "Minutter"
    override val navUsers = "Brukere"

    override val totalMinutes = "Totalt minutter"
    override val daysLeft = "Dager igjen"
    override val activeChores = "Aktive gjøremål"
    override val noActiveChores = "Ingen aktive gjøremål akkurat nå"
    override val completedChores = "Fullførte gjøremål"
    override val noCompletedChores = "Ingen fullførte gjøremål denne måneden"
    override val choreTemplates = "Gjøremålsmaler"
    override val newTemplate = "Ny mal"
    override val editTemplate = "Rediger mal"
    override val templateTitle = "Tittel på mal"
    override val rewardMinutes = "Belønning (minutter)"
    override val recurrence = "Gjentakelse"
    override val recurrenceManual = "Manuell"
    override val recurrenceDaily = "Hver dag"
    override val recurrenceTwoDays = "Hver 2. dag"
    override val recurrenceThreeDays = "Hver 3. dag"
    override val recurrenceWeekly = "Hver uke"
    override fun recurrenceEveryNDays(days: Int) = "Hver $days. dag"
    override val activate = "Aktiver"
    override val complete = "Fullfør"
    override val completed = "Fullført"
    override val awaitingApproval = "Venter på godkjenning"
    override val approve = "Godkjenn"
    override val reject = "Avvis"
    override val you = "deg"
    override val resetAssignment = "Fjern tildeling"

    override val createChoreTitle = "Opprett gjøremål"
    override val choreTitleLabel = "Tittel på gjøremål"
    override val descriptionOptional = "Beskrivelse (valgfritt)"
    override val assignee = "Tildelt"
    override val unassigned = "Ikke tildelt"
    override val markAsCompleted = "Marker som fullført"
    override val createChoreButton = "Opprett"

    override val rewardsTitle = "Minutter"
    override val minuteBalance = "Minuttbalanse"

    override val familyMembers = "Familiemedlemmer"
    override val pendingInvites = "Ventende invitasjoner"
    override val inviteUser = "Inviter bruker"
    override val name = "Navn"
    override val role = "Rolle"
    override val roleAdmin = "Administrator"
    override val roleMember = "Medlem"
    override val sendInvite = "Send invitasjon"
}

fun getStrings(language: AppLanguage): AppStrings = when (language) {
    AppLanguage.ENGLISH -> EnglishStrings
    AppLanguage.NORWEGIAN -> NorwegianStrings
}

val LocalAppLanguage = compositionLocalOf { AppLanguage.DEFAULT }
val LocalAppStrings = compositionLocalOf { EnglishStrings as AppStrings }
