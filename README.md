# 📚 Honari - Unde poveștile prind viață

<div align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-1.9.0-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack%20Compose-1.5.0-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Min%20SDK-24-brightgreen?style=for-the-badge" />
</div>

<div align="center">
  <h3>🌙 O aplicație de lectură care înțelege sufletul tău literar</h3>
  <p>Descoperă, citește și conectează-te cu alți pasionați de cărți într-o experiență unică și personalizată</p>
</div>

---

## ✨ Despre Honari

**Honari** este mai mult decât o simplă aplicație de citit - este un companion literar care te înțelege. Creată cu pasiune pentru cititorii care văd în cărți mai mult decât simple pagini, Honari transformă experiența de lectură într-o călătorie personală și socială.

### 🎯 De ce Honari?

- **Personalizare bazată pe stări sufletești** - Alege cărți în funcție de cum te simți: melancolic, visător, nostalgic sau contemplativ
- **Comunitate autentică** - Conectează-te cu cititori care împărtășesc aceleași gusturi literare
- **Design minimalist și elegant** - Interfață curată care pune accent pe conținut, nu pe distrageri
- **Tracking inteligent** - Urmărește-ți progresul și descoperă pattern-uri în obiceiurile tale de citit

## 🚀 Funcționalități principale

### 📖 Descoperă
- **Recomandări bazate pe mood** - Găsește cartea perfectă pentru starea ta de spirit
- **Trending în timp real** - Vezi ce citesc alții în comunitate
- **Citate zilnice** - Inspirație literară la fiecare deschidere a aplicației

### 📚 Biblioteca personală
- **Organizare inteligentă** - Sortează cărțile după status: În curs de citire, Citite, De citit
- **Statistici detaliate** - Vezi câte cărți ai citit, rating mediu, ore petrecute citind
- **Progres vizual** - Bare de progres animate pentru fiecare carte în curs de citire
- **Tag-uri personalizate** - Organizează-ți biblioteca după propriile criterii

### 👥 Cercuri literare
- **Comunități tematice** - Alătură-te cercurilor care se potrivesc cu gusturile tale
- **Discuții în timp real** - Participă la conversații despre cărțile preferate
- **Evenimente virtuale** - Cluburi de carte online și sesiuni de citit împreună
- **Matching inteligent** - Găsește cercuri cu procent de compatibilitate

### 📊 Profil & Progres
- **ADN-ul tău literar** - Vizualizează preferințele tale de gen într-un grafic interactiv
- **Milestone-uri** - Deblochează insigne pentru realizările tale de cititor
- **Statistici anuale** - Vezi câte cărți ai citit, ore petrecute, gen preferat
- **Activitate recentă** - Timeline cu toate acțiunile tale în aplicație

### 📖 Experiență de citire
- **Sesiuni de citire** - Cronometrează-ți timpul de lectură și rămâi concentrat
- **Capturi de citate** - Salvează pasajele preferate cu un singur tap
- **Obiective zilnice** - Setează și urmărește ținte de pagini, timp sau cărți
- **Estimări inteligente** - Vezi cât timp îți va lua să termini o carte

## 🛠️ Tehnologii utilizate

### Frontend
- **Jetpack Compose** - UI toolkit modern pentru Android
- **Material Design 3** - Sistem de design actualizat și fluid
- **Coil** - Încărcare optimizată de imagini
- **Compose Navigation** - Navigare type-safe între ecrane

### Arhitectură și Backend
- **MVVM + Clean Architecture** - Separare clară a responsabilităților
- **Kotlin Coroutines & Flow** - Programare asincronă reactivă
- **Dagger Hilt** - Dependency injection simplificat
- **Firebase**:
  - Authentication - Autentificare securizată
  - Firestore - Bază de date NoSQL în timp real
  - Storage - Stocare imagini de profil și coperți
  - Analytics - Înțelegerea comportamentului utilizatorilor

### Biblioteci adiționale
```kotlin
dependencies {
    // UI & Design
    implementation("androidx.compose.material3:material3:1.2.0")
    implementation("androidx.compose.material:material-icons-extended:1.5.4")
    implementation("io.coil-kt:coil-compose:2.5.0")
    
    // Arhitectură
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0")
    implementation("com.google.dagger:hilt-android:2.48")
    
    // Firebase
    implementation("com.google.firebase:firebase-auth-ktx:22.3.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.10.0")
    
    // Utilități
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.32.0")
}
```

## 📱 Capturi de ecran

<div align="center">
<table>
  <tr>
    <td><img src="screenshots/discover.png" width="200"/></td>
    <td><img src="screenshots/library.png" width="200"/></td>
    <td><img src="screenshots/reading.png" width="200"/></td>
    <td><img src="screenshots/profile.png" width="200"/></td>
  </tr>
  <tr>
    <td align="center">Descoperă</td>
    <td align="center">Biblioteca</td>
    <td align="center">Citire</td>
    <td align="center">Profil</td>
  </tr>
</table>
</div>

## 🏗️ Structura proiectului

```
com.honari.app/
├── data/                      # Layer de date
│   ├── repository/           # Implementări repository
│   ├── remote/              # Firebase data sources
│   └── local/               # Cache local (în dezvoltare)
├── domain/                   # Business logic
│   ├── model/               # Modele de date
│   ├── repository/          # Interfețe repository
│   └── usecase/             # Use case-uri
├── presentation/             # UI Layer
│   ├── screens/             # Ecrane Compose
│   │   ├── auth/           # Autentificare
│   │   ├── discover/       # Descoperă cărți
│   │   ├── library/        # Biblioteca personală
│   │   ├── reading/        # Experiență citire
│   │   ├── circles/        # Cercuri literare
│   │   └── profile/        # Profil utilizator
│   ├── components/          # Componente reutilizabile
│   ├── navigation/          # Configurare navigație
│   └── theme/              # Teme și stiluri
└── di/                      # Dependency Injection
```

## 🚀 Instalare și rulare

### Cerințe preliminare
- Android Studio Hedgehog sau mai nou
- JDK 17 sau mai nou
- Android SDK 24+
- Un cont Firebase pentru servicii backend

### Pași de instalare

1. **Clonează repository-ul**
```bash
git clone https://github.com/BiancaDuca123/Honari.git
cd Honari
```

2. **Configurează Firebase**
   - Creează un proiect nou în [Firebase Console](https://console.firebase.google.com)
   - Descarcă `google-services.json`
   - Plasează fișierul în `app/google-services.json`
   - Activează Authentication, Firestore și Storage

3. **Configurează variabilele de mediu**
```kotlin
// În local.properties
MAPS_API_KEY=your_maps_api_key_here
```

4. **Sincronizează și rulează**
   - Deschide proiectul în Android Studio
   - Sincronizează Gradle
   - Rulează pe emulator sau dispozitiv fizic

## 🤝 Cum să contribui

Contribuțiile sunt binevenite! Iată cum poți ajuta:

1. **Fork** acest repository
2. Creează un **branch** pentru feature-ul tău (`git checkout -b feature/AmazingFeature`)
3. **Commit** modificările tale (`git commit -m 'Add some AmazingFeature'`)
4. **Push** pe branch (`git push origin feature/AmazingFeature`)
5. Deschide un **Pull Request**

### 📋 Ghid de stil pentru cod
- Folosește Kotlin style guide oficial
- Păstrează funcțiile Composable mici și focused
- Documentează funcțiile publice
- Scrie teste pentru logica de business

## 🗺️ Roadmap

### În dezvoltare 🚧
- [ ] Sistem de notificări pentru activitatea din cercuri
- [ ] Cititor de cărți integrat cu suport EPUB
- [ ] Provocări de lectură săptămânale/lunare
- [ ] Integrare cu Goodreads API
- [ ] Statistici avansate cu grafice interactive

### Viitor 🔮
- [ ] Recomandări AI bazate pe istoric
- [ ] Audiobook player integrat
- [ ] Marketplace pentru cărți second-hand
- [ ] Evenimente fizice pentru comunitate
- [ ] Versiune iOS

## 📄 Licență

Acest proiect este licențiat sub Licența MIT - vezi fișierul [LICENSE](LICENSE) pentru detalii.

## 👩‍💻 Autor

**Bianca Duca**
- GitHub: [@BiancaDuca123](https://github.com/BiancaDuca123)

## 🙏 Mulțumiri

- Comunitatea Jetpack Compose pentru documentație excelentă
- Firebase pentru serviciile backend gratuite
- Toți beta testerii care au oferit feedback valoros
- Designerii de pe Unsplash pentru imaginile placeholder

---

<div align="center">
  <p>Făcut cu ❤️ pentru iubitorii de cărți din România și din întreaga lume</p>
  <p><strong>Honari</strong> - Unde fiecare pagină contează 📚</p>
