# WorkHome

WorkHome is a Kotlin Android family chores and rewards app built with Jetpack Compose, Material 3, Firebase Authentication, and Cloud Firestore.

## Features

- Email/password sign-in and invited-account creation
- Shared active chores list with per-user completion tracking
- Admin-only chore template management with one-tap activation into active chores
- Admin-only family user invitation management
- Rewards leaderboard with admin reset + reward history archiving
- Firestore security rules for role-based access

## Architecture

- **UI:** Jetpack Compose + Material 3
- **Navigation:** Navigation Compose
- **State:** MVVM with `ViewModel`, Kotlin coroutines, and `Flow`
- **Backend:** Firebase Authentication + Cloud Firestore
- **Collections:**
  - `users/{uid}` → `name`, `email`, `role`, `authUid`, `currentRewardTotal`
  - `choreTemplates/{templateId}` → reusable admin-managed `title`, `reward`, `createdBy`
  - `chores/{choreId}` → active chore entry with `title`/`description`, `reward`, `createdBy`, `active`
  - `rewardHistory/{historyId}` → `periodId`, `resetAt`, `resetBy`, `totals`
  - `completions/{completionId}` → `userId`, `choreId`, `periodId`, `reward`, `completedAt`
  - `pendingUsers/{normalizedEmail}` → helper collection for admin-created invites before a user claims an account

## Important Firebase limitation

Creating or deleting **other users' Firebase Authentication accounts** securely requires a trusted backend such as the Firebase Admin SDK or a Cloud Function. This Android app **does not** embed privileged Firebase Admin credentials.

Instead, the scaffold uses this safe workflow:

1. An admin creates a pending family member profile inside the app.
2. The family member uses **Create invited account** on the login screen with the same email.
3. After the first successful sign-in, the app claims the matching pending profile and creates `users/{uid}`.
4. Passwords remain managed by Firebase Authentication and are never stored in Firestore.

For the very first admin, create the Firebase Auth account manually and add the matching Firestore user document manually.

## Firebase setup (Spark plan)

1. Create a free Firebase project at <https://console.firebase.google.com/>.
2. In **Build → Authentication**, enable **Email/Password**.
3. In **Build → Firestore Database**, create a Firestore database in production mode.
4. In **Project settings → General**, register a new Android app with package name:
   - `com.bdysvik.workhome`
5. Download `google-services.json`.
6. Copy it into:
   - `app/google-services.json`
7. Publish the included Firestore rules:
   - Firebase Console → Firestore Database → Rules → paste `firestore.rules`
   - or use the Firebase CLI if you prefer.

## Create the first admin

1. In Firebase Authentication, create an email/password user for the admin.
2. Copy that user's **UID**.
3. In Firestore, create document `users/<UID>` with fields:

```json
{
  "name": "Parent Admin",
  "email": "admin@example.com",
  "role": "admin",
  "authUid": "<UID>",
  "currentRewardTotal": 0
}
```

4. Sign into the app with that admin account.
5. Use **Users** to add invited family members.

## Build and run

```bash
./gradlew testDebugUnitTest
./gradlew assembleDebug
```

Then open the project in Android Studio or install the generated debug APK on an emulator/device.

## Notes

- `app/google-services.json.example` is a placeholder only.
- Real `app/google-services.json` is ignored by Git to keep Firebase secrets out of source control.
- Chore duplicate-prevention is enforced per user per calendar month via `completions` documents, which fits the intended monthly reward reset flow.
