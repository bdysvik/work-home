package com.bdysvik.workhome.localization

interface AppStrings {
    // App info & common
    val appName: String
    val language: String
    val selectLanguage: String
    val english: String
    val norwegian: String
    val cancel: String
    val confirm: String
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
    val navTemplates: String
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
    val creating: String

    // Rewards & Goals
    val rewardsTitle: String
    val minuteBalance: String
    val setGoalTitle: String
    val targetMinutesGoal: String
    val totalRewardEligible: String
    val rewardEligiblePlaceholder: String
    val accumulatedRewardLabel: String
    fun accumulatedReward(accumulated: Long, eligible: Long): String

    // Templates & Chores
    val saveTemplateHeader: String
    val editTemplateHeader: String
    val templateTitleLabel: String
    val templateRewardLabel: String
    val autoCreationSchedule: String
    val saveTemplateButton: String
    val updateTemplateButton: String
    val cancelEdit: String
    val savingTemplate: String
    val activating: String
    val deleting: String
    fun tasksCount(count: Int): String
    fun rewardMinutesFormat(minutes: Long): String
    val assignedToYou: String
    fun assignedToUser(name: String): String
    val assigned: String
    val approveChore: String
    val approving: String
    val assignToMe: String
    val assigning: String
    val completedThisMonth: String
    val completeForMe: String
    val completing: String
    val resettingAssignment: String
    val deleteChore: String
    val deleteChoreTitle: String
    val deleteTemplateTitle: String
    fun totalRewardFormat(reward: Long): String

    // Create Chore Screen
    val choreTitlePlaceholder: String
    val choreRewardPlaceholder: String
    val assignToOptional: String
    val markCompletedImmediately: String
    val setToCompleteAwaitsApproval: String

    // Users Screen & Invites
    val inviteFamilyMember: String
    val addInvite: String
    val savingInvite: String
    val passwordWorkflowNote: String
    val activeUsers: String
    fun roleFormat(role: String): String
    fun goalFormat(goal: Long): String
    val goalNotSet: String
    val removeProfile: String
    val removeInvite: String
    val removeProfileTitle: String
    fun removeProfileBody(email: String): String
    val removeInviteTitle: String
    fun removeInviteBody(email: String): String

    // Rewards Screen & Admin Controls
    val adminControls: String
    val resetDescription: String
    val resetMinutes: String
    val resettingMinutes: String
    fun totalMinutesFormat(text: String): String
    fun daysSinceLastChoreFormat(daysText: String): String
    val resetConfirmTitle: String
    val resetConfirmBody: String

    // Set Goal Dialog
    fun setGoalForUser(name: String): String
    val leaveEmptyToClear: String

    // Users screen
    val familyMembers: String
    val pendingInvites: String
    val inviteUser: String
    val name: String
    val role: String
    val roleAdmin: String
    val roleMember: String
    val sendInvite: String

    companion object {
        fun forLanguage(language: AppLanguage): AppStrings = getStrings(language)
    }
}

object EnglishStrings : AppStrings {
    override val appName = "WorkHome"
    override val language = "Language"
    override val selectLanguage = "Select language"
    override val english = "English"
    override val norwegian = "Norwegian"
    override val cancel = "Cancel"
    override val confirm = "Confirm"
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
    override val navTemplates = "Templates"
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
    override val creating = "Creating..."

    override val rewardsTitle = "Minutes"
    override val minuteBalance = "Minute balance"
    override val setGoalTitle = "Set goal and reward"
    override val targetMinutesGoal = "Target minutes goal"
    override val totalRewardEligible = "Total reward eligible"
    override val rewardEligiblePlaceholder = "e.g. 500"
    override val accumulatedRewardLabel = "Accumulated reward"
    override fun accumulatedReward(accumulated: Long, eligible: Long) = "$accumulatedRewardLabel: $accumulated / $eligible"

    override val saveTemplateHeader = "Save chore template"
    override val editTemplateHeader = "Edit chore template"
    override val templateTitleLabel = "Title"
    override val templateRewardLabel = "Minutes"
    override val autoCreationSchedule = "Auto-creation schedule"
    override val saveTemplateButton = "Save template"
    override val updateTemplateButton = "Update template"
    override val cancelEdit = "Cancel edit"
    override val savingTemplate = "Saving template..."
    override val activating = "Activating..."
    override val deleting = "Deleting..."
    override fun tasksCount(count: Int) = "$count ${if (count == 1) "task" else "tasks"}"
    override fun rewardMinutesFormat(minutes: Long) = "Minutes: $minutes"
    override val assignedToYou = "Assigned to you"
    override fun assignedToUser(name: String) = "Assigned to $name"
    override val assigned = "Assigned"
    override val approveChore = "Approve chore"
    override val approving = "Approving..."
    override val assignToMe = "Assign to me"
    override val assigning = "Assigning..."
    override val completedThisMonth = "Completed for this month"
    override val completeForMe = "Complete for me"
    override val completing = "Completing..."
    override val resettingAssignment = "Resetting..."
    override val deleteChore = "Delete chore"
    override val deleteChoreTitle = "Delete chore?"
    override val deleteTemplateTitle = "Delete template?"
    override fun totalRewardFormat(reward: Long) = "Total reward: $reward"

    override val choreTitlePlaceholder = "e.g. Wash dishes"
    override val choreRewardPlaceholder = "e.g. 15"
    override val assignToOptional = "Assign to (optional):"
    override val markCompletedImmediately = "Mark as completed immediately"
    override val setToCompleteAwaitsApproval = "Set to complete (awaits admin approval)"

    override val inviteFamilyMember = "Invite family member"
    override val addInvite = "Add invite"
    override val savingInvite = "Saving..."
    override val passwordWorkflowNote = "Password workflow: invite the user here, then have them use Create invited account on the login screen. Passwords stay in Firebase Auth and are never stored in Firestore."
    override val activeUsers = "Active users"
    override fun roleFormat(role: String) = "Role: $role"
    override fun goalFormat(goal: Long) = "Goal: $goal min"
    override val goalNotSet = "Goal: Not set"
    override val removeProfile = "Remove profile"
    override val removeInvite = "Remove invite"
    override val removeProfileTitle = "Remove user profile?"
    override fun removeProfileBody(email: String) = "This deletes the Firestore profile for $email. Delete the Firebase Auth user separately from the Firebase Console or a trusted backend if needed."
    override val removeInviteTitle = "Remove invite?"
    override fun removeInviteBody(email: String) = "This removes the pending invite for $email."

    override val adminControls = "Admin controls"
    override val resetDescription = "Reset archives the current totals into history before setting every user total back to zero."
    override val resetMinutes = "Reset minutes"
    override val resettingMinutes = "Resetting..."
    override fun totalMinutesFormat(text: String) = "Total minutes: $text"
    override fun daysSinceLastChoreFormat(daysText: String) = "Days since last completed chore: $daysText"
    override val resetConfirmTitle = "Reset all minutes?"
    override val resetConfirmBody = "This archives the current totals and zeroes out every family member's running minutes."

    override fun setGoalForUser(name: String) = "Set goal and reward for $name:"
    override val leaveEmptyToClear = "Leave empty to clear"

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
    override val confirm = "Bekreft"
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
    override val navTemplates = "Maler"
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
    override val creating = "Oppretter..."

    override val rewardsTitle = "Minutter"
    override val minuteBalance = "Minuttbalanse"
    override val setGoalTitle = "Sett mål og belønning"
    override val targetMinutesGoal = "Mål i minutter"
    override val totalRewardEligible = "Maksimal belønning tilgjengelig"
    override val rewardEligiblePlaceholder = "f.eks. 500"
    override val accumulatedRewardLabel = "Opptjent belønning"
    override fun accumulatedReward(accumulated: Long, eligible: Long) = "$accumulatedRewardLabel: $accumulated / $eligible"

    override val saveTemplateHeader = "Lagre gjøremålsmal"
    override val editTemplateHeader = "Rediger gjøremålsmal"
    override val templateTitleLabel = "Tittel"
    override val templateRewardLabel = "Minutter"
    override val autoCreationSchedule = "Automatisk opprettelse"
    override val saveTemplateButton = "Lagre mal"
    override val updateTemplateButton = "Oppdater mal"
    override val cancelEdit = "Avbryt redigering"
    override val savingTemplate = "Lagrer mal..."
    override val activating = "Aktiverer..."
    override val deleting = "Sletter..."
    override fun tasksCount(count: Int) = "$count ${if (count == 1) "oppgave" else "oppgaver"}"
    override fun rewardMinutesFormat(minutes: Long) = "Minutter: $minutes"
    override val assignedToYou = "Tildelt deg"
    override fun assignedToUser(name: String) = "Tildelt $name"
    override val assigned = "Tildelt"
    override val approveChore = "Godkjenn gjøremål"
    override val approving = "Godkjenner..."
    override val assignToMe = "Tildel til meg"
    override val assigning = "Tildeler..."
    override val completedThisMonth = "Fullført denne måneden"
    override val completeForMe = "Fullfør for meg"
    override val completing = "Fullfører..."
    override val resettingAssignment = "Fjerner tildeling..."
    override val deleteChore = "Slett gjøremål"
    override val deleteChoreTitle = "Slett gjøremål?"
    override val deleteTemplateTitle = "Slett mal?"
    override fun totalRewardFormat(reward: Long) = "Totalt belønning: $reward"

    override val choreTitlePlaceholder = "f.eks. Vaske opp"
    override val choreRewardPlaceholder = "f.eks. 15"
    override val assignToOptional = "Tildel til (valgfritt):"
    override val markCompletedImmediately = "Marker som fullført umiddelbart"
    override val setToCompleteAwaitsApproval = "Sett som fullført (krever godkjenning)"

    override val inviteFamilyMember = "Inviter familiemedlem"
    override val addInvite = "Legg til invitasjon"
    override val savingInvite = "Lagrer..."
    override val passwordWorkflowNote = "Passord-arbeidsflyt: inviter brukeren her, og be dem opprette konto fra innloggingsskjermen. Passord lagres kun i Firebase Auth."
    override val activeUsers = "Aktive brukere"
    override fun roleFormat(role: String) = "Rolle: ${if (role == "admin") "Administrator" else "Medlem"}"
    override fun goalFormat(goal: Long) = "Mål: $goal min"
    override val goalNotSet = "Mål: Ikke satt"
    override val removeProfile = "Fjern profil"
    override val removeInvite = "Fjern invitasjon"
    override val removeProfileTitle = "Fjern brukerprofil?"
    override fun removeProfileBody(email: String) = "Dette sletter Firestore-profilen for $email. Slett Firebase Auth-brukeren separat fra Firebase Console om nødvendig."
    override val removeInviteTitle = "Fjern invitasjon?"
    override fun removeInviteBody(email: String) = "Dette fjerner den ventende invitasjonen for $email."

    override val adminControls = "Admin-kontroller"
    override val resetDescription = "Nullstilling arkiverer nåværende totaler i historikken før alle brukersummer settes til null."
    override val resetMinutes = "Nullstill minutter"
    override val resettingMinutes = "Nullstiller..."
    override fun totalMinutesFormat(text: String) = "Totalt minutter: $text"
    override fun daysSinceLastChoreFormat(daysText: String) = "Dager siden siste fullførte gjøremål: $daysText"
    override val resetConfirmTitle = "Nullstill alle minutter?"
    override val resetConfirmBody = "Dette arkiverer gjeldende summer og setter alle familiemedlemmers minutter til null."

    override fun setGoalForUser(name: String) = "Sett mål og belønning for $name:"
    override val leaveEmptyToClear = "La stå tom for å fjerne"

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
