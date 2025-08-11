# 🎸 Guitar & Bass Practice App - Android Kotlin

A comprehensive native Android application built with Kotlin for guitar, bass, and stringed instrument practice. Features interactive fretboard visualization, music theory integration, AI-assisted exercise generation, and offline capabilities.

## 🚀 Features

### Core Features
- **Exercise Library**: Create, store, and modify musical exercises with rich metadata
- **Interactive Fretboard Visualizer**: Real-time note overlays and progression tracking
- **Multi-Format Notation**: Tab, staff, chord chart, and fretboard notation rendering
- **Exercise Engine**: Timing-based playback with highlighting and optional audio
- **User Profiles**: Multiple user support with instrument preferences and skill levels
- **Advanced Customization**: Deep exercise modification with theory constraints
- **AI Exercise Generation**: Natural language exercise creation using AI APIs
- **Offline Mode**: Core features work without internet connection
- **Secure Storage**: Encrypted SQLite database using SQLCipher

### Accessibility & Design
- **High-contrast, color-blind friendly UI** with multiple theme options
- **Dark/Light theme support** with system integration
- **Scalable fonts** and accessibility features
- **Touch-friendly controls** optimized for mobile interaction
- **Professional Material 3 design** with smooth animations

### Supported Instruments
- Guitar (6-string standard tuning)
- Bass (4-string standard tuning)
- Ukulele (4-string standard tuning)
- Mandolin (8-string standard tuning)
- Banjo (5-string standard tuning)

## 🏗️ Architecture

The app follows **Clean Architecture** principles with clear separation of concerns:

```
presentation/          # UI layer (Jetpack Compose)
├── ui/
│   ├── screens/      # Screen composables
│   ├── components/   # Reusable UI components
│   └── theme/        # Material 3 theming
└── viewmodel/        # ViewModels for state management

domain/               # Business logic layer
├── model/           # Domain entities
├── repository/      # Repository interfaces
└── usecase/         # Use cases and business logic

data/                # Data layer
├── database/        # Room database with SQLCipher
├── repository/      # Repository implementations
├── api/            # REST API for AI services
└── model/          # Data entities

di/                  # Dependency injection (Hilt)
util/               # Utilities and extensions
```

## 🛠️ Tech Stack

### Core Technologies
- **Kotlin**: Native Android development
- **Jetpack Compose**: Modern declarative UI toolkit
- **Material 3**: Google's latest design system
- **Hilt**: Dependency injection
- **Room + SQLCipher**: Encrypted local database
- **Coroutines + Flow**: Reactive programming
- **Navigation Compose**: Type-safe navigation

### Key Libraries
- **Room**: `androidx.room:room-runtime:2.6.1`
- **SQLCipher**: `net.zetetic:android-database-sqlcipher:4.5.4`
- **Hilt**: `com.google.dagger:hilt-android:2.48.1`
- **Retrofit**: `com.squareup.retrofit2:retrofit:2.9.0`
- **Protocol Buffers**: `com.google.protobuf:protobuf-kotlin-lite:3.24.4`
- **Compose BOM**: `androidx.compose:compose-bom:2023.10.01`

### Graphics & Audio
- **Canvas API**: Custom fretboard and notation rendering
- **Compose Graphics**: Smooth animations and visualizations
- **Future**: Oboe/Superpowered SDK for low-latency audio

## 📱 Getting Started

### Prerequisites
- Android Studio Flamingo or later
- Android SDK API level 24+ (Android 7.0)
- Kotlin 1.9.10+
- JDK 8+

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd guitar-bass-practice-app/android-kotlin
   ```

2. **Open in Android Studio**
   - Import the project
   - Let Gradle sync automatically

3. **Configure API Keys** (Optional for AI features)
   - Open `NetworkModule.kt`
   - Replace `your-api-key-here` with your OpenAI/Claude API key
   - Update base URL if using different AI service

4. **Build and Run**
   ```bash
   ./gradlew assembleDebug
   ./gradlew installDebug
   ```

### Running Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# All tests
./gradlew check
```

## 🎯 Usage Guide

### Creating Your First Exercise

1. **Set Up Profile**
   - Tap the "+" button in the profile section
   - Enter your name, select instrument and skill level
   - Your profile is automatically activated

2. **Create Custom Exercise**
   - Navigate to the "Create" tab
   - Select your instrument and difficulty
   - Set fret range and musical constraints
   - Choose scales, keys, or chord progressions
   - Tap "Create Exercise"

3. **Use AI Generation**
   - Navigate to the "AI" tab
   - Enter a natural language prompt like:
     - "Create a C major scale exercise"
     - "Generate blues scale practice in E"
     - "Make a chord progression in G major"
   - Tap "Generate with AI"

4. **Practice Exercise**
   - Select any exercise from your library
   - Use playback controls to start/pause/stop
   - Watch the fretboard highlights sync with notation
   - Adjust BPM, enable loop, or toggle metronome

### Advanced Features

#### Exercise Customization
- **Fret Range**: Limit exercises to specific fret positions
- **String Selection**: Focus on particular strings
- **Theory Constraints**: Filter by keys, scales, and chords
- **Playback Settings**: Adjust tempo, loop, and metronome

#### AI Exercise Generation
- **Context-Aware**: AI understands your instrument and skill level
- **Natural Language**: Describe exercises in plain English
- **Theory Integration**: AI respects musical theory constraints
- **Instant Creation**: Generated exercises are immediately available

#### Accessibility Options
- **Theme Selection**: Light, dark, or system-based themes
- **Color Schemes**: Default, color-blind friendly, or high contrast
- **Font Scaling**: Adjust text size for better readability
- **Reduced Motion**: Minimize animations for sensitive users

## 🗄️ Database Schema

The app uses Room with SQLCipher for encrypted local storage:

### Core Tables
- **exercises**: Musical exercise data with metadata
- **user_profiles**: User information and preferences  
- **exercise_progress**: Practice tracking and statistics

### Key Features
- **Encrypted**: All data secured with SQLCipher
- **Offline-First**: Full functionality without network
- **Backup Compatible**: Structured for future cloud sync

## 🔧 Configuration

### Build Variants
- **debug**: Development build with logging
- **release**: Production build with optimizations

### Customization Options
- **AI Provider**: Swap OpenAI for Claude or other LLM APIs
- **Database**: Modify schema for additional metadata
- **Instruments**: Add new instrument types and tunings
- **Themes**: Create custom color schemes

## 🚧 Future Enhancements

### Phase 2 Features
- **Real-time Audio Input**: Pitch and timing analysis
- **Backing Tracks**: Play along with generated accompaniment
- **Cloud Sync**: Cross-device exercise synchronization
- **Social Features**: Share exercises with community
- **Advanced Analytics**: Detailed practice statistics

### Phase 3 Features
- **Tone Generation**: Built-in amp/effect simulation
- **MIDI Integration**: External keyboard/controller support
- **Augmented Reality**: AR fretboard overlay via camera
- **Machine Learning**: Personalized practice recommendations

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](CONTRIBUTING.md) for details.

### Development Workflow
1. Fork the repository
2. Create feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open Pull Request

### Code Standards
- Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use meaningful commit messages
- Include unit tests for new features
- Update documentation as needed

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🙏 Acknowledgments

- **Material 3 Design**: Google's comprehensive design system
- **JetPack Compose**: Revolutionary declarative UI toolkit
- **Music Theory**: Classical and modern pedagogical approaches
- **Accessibility**: WCAG guidelines for inclusive design
- **Open Source Community**: Countless libraries and tools

## 📞 Support

For support and questions:
- Create an issue on GitHub
- Check the [FAQ](docs/FAQ.md)
- Review [troubleshooting guide](docs/TROUBLESHOOTING.md)

---

**Built with ❤️ for musicians by musicians**