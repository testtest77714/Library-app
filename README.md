# Libraries — Android app

A minimal, working recreation of Memento Database's core idea:
create **Libraries**, define their **Fields** (schema), then fill in
**Entries** that follow that schema. Built with Kotlin, Jetpack
Compose, and Room (local SQLite storage — fully offline).

## What it does

- Create/delete libraries, with a teal Memento-style "My libraries" screen
- Every library starts with a "Name" text field (used as its title)
- Add fields of type Text, Number, Date, or Checkbox
- Pick which field is the "title" field shown in entry lists/cards
- Reorder fields with up/down controls in the schema editor
- Add, edit, and delete entries with a form generated from the schema
- Required fields are validated before saving
- Toggle a library between **Cards** (2-column grid) and **List** view
- A right-side drawer on the library screen echoes the real app's menu
  (Sort, Filters, Automations, Recycle Bin, etc.) — see "Visual-only
  items" below for what's a placeholder there
- Everything persists locally via Room/SQLite

## Visual-only items (shown but not functional yet)

To match the real app's shell without overstating what's built,
these appear in the UI but don't do anything yet — tapping most of
them is disabled on purpose:
- The library list's overflow menu (Add Group, Add Dashboard, Open
  by URL, Shortcuts) — only **Add Library** works
- The library row's ⋮ menu (Protection, Upload to Cloud, Link to
  Google Sheets, Scripts, etc.) — only **Edit** and **Delete** work
- The library drawer's Preset, Sort, Filters, Automations, Favorites,
  History, Recycle Bin, Charts, Drafts, Prefilled entries, Import/
  export, Files, and Settings rows — **View** (Cards/List) is the
  only one wired up
- The schema editor's MAIN, AGGREGATION, AUTOFILL, and NOTES tabs —
  only **FIELDS** is implemented
- The field-type picker only offers Text, Number, Date, and
  Checkbox — the real app has 35+ types (see the "Where to go next"
  section)

## How to open it

1. Install [Android Studio](https://developer.android.com/studio) (Koala or newer recommended).
2. Open Android Studio → **Open** → select this project's root folder (the one with `settings.gradle.kts`).
3. Let Gradle sync (first sync downloads dependencies — needs internet).
4. Press **Run ▶** with an emulator or a physical device connected (USB debugging enabled).

No manual configuration needed — minSdk 26 (Android 8.0+), targetSdk 34.

## Get an installable APK without Android Studio

This project includes a GitHub Actions workflow
(`.github/workflows/build-apk.yml`) that builds a debug APK in the
cloud automatically — useful if you don't have Android Studio set
up locally.

1. Create a new repository on GitHub (public or private, either works).
2. Push this project to it:
   ```
   cd LibraryApp
   git init
   git add .
   git commit -m "Initial commit"
   git branch -M main
   git remote add origin https://github.com/<your-username>/<repo-name>.git
   git push -u origin main
   ```
3. On GitHub, open your repo → **Actions** tab. A workflow run
   should already be in progress (triggered by the push). If not,
   click **Build APK** in the left sidebar → **Run workflow**.
4. Once it finishes (green checkmark, a couple minutes), click into
   the run → scroll to **Artifacts** → download **app-debug-apk**.
   It's a zip containing `app-debug.apk`.
5. Transfer that APK to your phone (email it to yourself, upload to
   Google Drive, etc.), tap it to install. You'll need to allow
   "install unknown apps" for whichever app you open it with —
   Android will prompt you the first time.

This APK is signed with Android's default debug key, which is fine
for installing on your own device but not for publishing to the
Play Store.

## Or build it yourself locally
Once you have Android Studio: **Build → Build Bundle(s)/APK(s) →
Build APK(s)**. The APK lands in
`app/build/outputs/apk/debug/app-debug.apk`.

## Project structure

```
app/src/main/java/com/example/libraryapp/
├── MainActivity.kt              entry point
├── data/
│   ├── Library.kt                Library entity
│   ├── Field.kt                  Field entity + FieldType enum
│   ├── Entry.kt                  Entry + EntryValue entities
│   ├── LibraryDao.kt             Room queries
│   ├── Converters.kt             Room type converters
│   ├── AppDatabase.kt            Room database singleton
│   └── LibraryRepository.kt      repository layer
└── ui/
    ├── LibraryViewModel.kt       app state + actions
    ├── AppNavigation.kt          navigation graph
    ├── theme/Theme.kt            teal Material 3 theme
    └── screens/
        ├── LibraryListScreen.kt   "My Libraries" home screen
        ├── LibraryDetailScreen.kt entries inside one library (Cards/List + drawer)
        ├── SchemaEditorScreen.kt  tabbed field editor (Fields tab implemented)
        └── EntryFormScreen.kt     create/edit an entry
```

## Where to go next

This covers "Phase 1" of a fuller build: a dependable local
database with schema-driven forms, now with a shell that matches
the real app's navigation. Natural next additions, in rough order:
- **True drag-and-drop reordering** for fields (currently up/down buttons)
- **More field types** — Rich Text, Integer, Currency, Time,
  Single/Multiple-choice, Tags, Rating, Location, Link to entry
- **Search** that actually filters entries (currently UI-only)
- **Sort and filters** within a library
- **Relationships** — link entries between libraries
- **Calculated fields**
- **Image/file fields** with device camera/gallery picker
- **CSV import/export**
- **Recycle Bin** — soft-delete instead of permanent delete

Each of these can be added incrementally on top of this
foundation without restructuring what's here.
