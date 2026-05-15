# ನಲ್ಲ ನುಡಿ — Nalla Nudi 📖

**Bridge Dictionary for Technical Terms | ತಾಂತ್ರಿಕ ಪದಗಳ ಸೇತುವೆ ನಿಘಂಟು**

> *"Language is our ladder, knowledge is our light."*
> *"ಭಾಷೆ ನಮ್ಮ ಏಣಿ, ಜ್ಞಾನ ನಮ್ಮ ಬೆಳಕು."*

---

## 📱 About the App

**Nalla Nudi** is an offline Android dictionary app built for **Kannada-medium students** transitioning to English-medium higher education. Students already know the concepts — they just struggle with the English technical vocabulary. This app bridges that gap.

Search any English technical term and instantly get:
- ✅ The Kannada meaning in **Kannada script**
- ✅ A **simple Kannada explanation** using everyday language
- ✅ A **real-world Karnataka example**
- ✅ A **pronunciation guide** + tap-to-hear audio
- ✅ **100% offline** — works in villages with no internet

---
## ✨ Features

| Feature | Description |
|---|---|
| 🔍 **Instant Search** | Search any English term — results appear under 200ms |
| 🏷️ **Subject Filters** | Filter by Science, Math, Commerce, or English |
| 📖 **Word of the Day** | A featured term shown every session on Home screen |
| 🔊 **Pronunciation (TTS)** | Tap to hear correct English pronunciation |
| 📚 **Term Detail** | Kannada script, explanation, example, and hint |
| ⭐ **My List** | Save difficult words for personal revision |
| 🃏 **Flashcard Mode** | Tap-to-flip animated cards — English front, Kannada back |
| 🎯 **Quiz Mode** | 10 MCQ questions with instant Kannada feedback and score |
| 📵 **100% Offline** | No internet needed after install |

---

## 🛠️ Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| **Kotlin** | 1.9.22 | Primary language |
| **Jetpack Compose** | BOM 2024.02.00 | Declarative UI |
| **Material Design 3** | Latest | UI components |
| **Room Database** | 2.6.1 | Offline SQLite storage |
| **Navigation Compose** | 2.7.7 | Screen navigation |
| **ViewModel** | 2.7.0 | State management |
| **Kotlin Coroutines** | 1.7.3 | Async operations |
| **StateFlow / Flow** | Built-in | Reactive data streams |
| **Gson** | 2.10.1 | JSON glossary parsing |
| **KSP** | 1.9.22-1.0.17 | Room annotation processing |
| **Android TTS** | System | English pronunciation |
| **Min SDK** | API 24 (Android 7.0) | 99.2% device coverage |
| **Target SDK** | API 34 (Android 14) | Latest features |

---

## 🏗️ Architecture

This project follows **MVVM (Model-View-ViewModel)** — Google's recommended Android architecture.

```
Composable UI (Screens)
        ↕ collectAsState()
DictionaryViewModel (StateFlow)
        ↕ suspend functions
DictionaryRepository (single source of truth)
        ↕
Room Database ←── assets/glossary.json
        +
Android TTS (pronunciation)
```

### Package Structure

```
com.nallanudi.app/
├── data/
│   ├── model/
│   │   ├── Term.kt               ← Room Entity + Subject enum
│   │   └── SavedTerm.kt          ← My List bookmark entity
│   ├── db/
│   │   ├── TermDao.kt            ← Search, filter, WOTD queries
│   │   ├── SavedTermDao.kt       ← Save/remove/list queries
│   │   └── AppDatabase.kt        ← Singleton DB + JSON seeding
│   └── repository/
│       └── DictionaryRepository.kt
├── viewmodel/
│   └── DictionaryViewModel.kt    ← All StateFlow + TTS + Quiz logic
├── theme/
│   └── Theme.kt                  ← Saffron brand colors
├── ui/
│   ├── home/HomeScreen.kt
│   ├── search/
│   │   ├── SearchScreen.kt
│   │   └── TermDetailScreen.kt
│   ├── mylist/MyListScreen.kt
│   ├── flashcard/FlashcardScreen.kt
│   └── quiz/QuizScreen.kt
├── MainActivity.kt               ← NavHost + BottomNavBar
└── assets/
    └── glossary.json             ← 100 pre-loaded terms
```

---

## 📂 Database Design

### `terms` table (100 rows — pre-seeded from glossary.json)

| Column | Type | Description |
|---|---|---|
| `id` | INT PK AUTO | Auto-generated key |
| `englishTerm` | TEXT | English technical term |
| `kannadaScript` | TEXT | Kannada script (ದ್ಯುತಿಸಂಶ್ಲೇಷಣೆ) |
| `kannadaTerm` | TEXT | Latin transliteration |
| `kannadaExplanation` | TEXT | Simple Kannada explanation |
| `kannadaExample` | TEXT | Real-world Karnataka example |
| `subject` | TEXT | SCIENCE / MATH / COMMERCE / ENGLISH |
| `pronunciationHint` | TEXT | Phonetic guide (fo-to-SIN-the-sis) |
| `isFeatured` | BOOLEAN | Word of the Day flag |

### `saved_terms` table (user's My List)

| Column | Type | Description |
|---|---|---|
| `termId` | INT PK | References `terms.id` |
| `savedAt` | LONG | Save timestamp (millis) |

---

## 📚 Glossary Coverage

| Subject | Count | Sample Terms |
|---|---|---|
| 🔬 Science | ~40 | Photosynthesis, Gravity, Osmosis, Chromosome, Enzyme, Vaccine, Chlorophyll... |
| 📐 Math | ~30 | Trigonometry, Algebra, Probability, Logarithm, Matrix, Integer, Ratio... |
| 📊 Commerce | ~20 | Depreciation, Revenue, Inflation, Asset, Liability, Tax, Budget, Dividend... |
| 🔤 English | ~10 | Noun, Verb, Adjective, Tense, Synonym, Metaphor, Comprehension... |

---

## 🚀 Getting Started

### Prerequisites

- Android Studio (latest stable)
- JDK 17
- Android device or emulator (API 24+)

### Setup

```bash
# 1. Clone the repository
git clone https://github.com/your-username/NallaNudi.git

# 2. Open in Android Studio
# File → Open → select the NallaNudi folder

# 3. Sync Gradle
# File → Sync Project with Gradle Files

# 4. Run
# Press the green ▶ Run button
```

> On first launch, the app automatically seeds all 100 terms from `assets/glossary.json` into the Room database. No internet required.

---

## 🎯 Performance

| Metric | Target | Status |
|---|---|---|
| Search speed | < 200ms | ✅ Achieved |
| Offline operation | 100% | ✅ Achieved |
| Device coverage | 99.2% (API 24+) | ✅ Achieved |
| Flashcard animation | Smooth 400ms flip | ✅ Achieved |
| Quiz feedback | Instant | ✅ Achieved |
| My List persistence | Survives restart | ✅ Achieved |

---

## 🌍 Impact Goals

- **Equitable Education** — Removes the language barrier for rural Karnataka students in STEM
- **Skill Readiness** — Prepares students for technical exams, interviews, and competitive tests
- **Linguistic Pride** — Uses Kannada as a *ladder* to learn global technical vocabulary
- **Digital Inclusion** — Works 100% offline on any Android phone from 2016 onwards

---

## 🔮 Planned Features

- [ ] Expand glossary from 100 to 500+ terms (PUC 1 & 2 syllabus)
- [ ] Gemini AI integration — generate Kannada explanations for any English term
- [ ] Kannada TTS — read Kannada explanations aloud
- [ ] Quiz timer — countdown per question for exam simulation
- [ ] Progress tracking — daily streak, subject mastery percentage
- [ ] Telugu, Tamil, Marathi versions using same architecture
- [ ] Google Play Store release

---

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

1. **Add more terms** — Edit `app/src/main/assets/glossary.json` following the existing format
2. **Improve Kannada explanations** — Make them simpler and more relatable for rural students
3. **Report bugs** — Open an issue with steps to reproduce
4. **Suggest features** — Open an issue with the `enhancement` label

### Term JSON Format

```json
{
  "id": 101,
  "englishTerm": "Respiration",
  "kannadaScript": "ಉಸಿರಾಟ",
  "kannadaTerm": "Usiraata",
  "kannadaExplanation": "ಜೀವಿಗಳು ಆಮ್ಲಜನಕ ತೆಗೆದುಕೊಂಡು ಶಕ್ತಿ ಉತ್ಪಾದಿಸುವ ಪ್ರಕ್ರಿಯೆ.",
  "kannadaExample": "ನಾವು ಓಡಿದ ನಂತರ ಬೇಗ ಉಸಿರಾಡುತ್ತೇವೆ.",
  "subject": "SCIENCE",
  "pronunciationHint": "res-pih-RAY-shun",
  "isFeatured": false
}
```

---

## 📄 License

```
MIT License

Copyright (c) 2026 Dayasagar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 👨‍💻 Developer

**Dayasagar**
Dept. of Computer Science & Engineering
JSSATEB, Bengaluru | 2022–2026

Internship under **MindMatrix (MindMatrixEd)**
Mentor: Mr. Tirumal Mutalikdesai

---

<div align="center">

**ನಲ್ಲ ನುಡಿ — ಭಾಷೆ ನಮ್ಮ ಏಣಿ, ಜ್ಞಾನ ನಮ್ಮ ಬೆಳಕು**

*Nalla Nudi — Language is our ladder, knowledge is our light.*

![Android](https://img.shields.io/badge/Android-API%2024%2B-green?logo=android)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9.22-purple?logo=kotlin)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202024.02-blue?logo=jetpackcompose)
![Offline](https://img.shields.io/badge/Offline-100%25-orange)
![License](https://img.shields.io/badge/License-MIT-yellow)

</div>
