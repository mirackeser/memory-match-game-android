# 🧠 Memory Match (Hafıza Oyunu) - Android

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Android Platform" />
  <img src="https://img.shields.io/badge/Language-Java_17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 17" />
  <img src="https://img.shields.io/badge/Target_SDK-34_(Android_14)-blue?style=for-the-badge&logo=android" alt="Target SDK 34" />
  <img src="https://img.shields.io/badge/Min_SDK-24_(Android_7.0)-lightgrey?style=for-the-badge" alt="Min SDK 24" />
  <img src="https://img.shields.io/badge/Firebase-Firestore_%26_FCM-FFCA28?style=for-the-badge&logo=firebase&logoColor=black" alt="Firebase" />
  <img src="https://img.shields.io/badge/License-MIT-green?style=for-the-badge" alt="MIT License" />
</p>

<p align="center">
  Modern, akıcı ve yüksek performanslı bir <b>Android Eşleştirme / Hafıza Oyunu</b>. 
  <br />
  3D kart çevirme animasyonları, dinamik zorluk seviyeleri, çoklu tema motoru, Firestore destekli küresel skor tablosu ve Firebase Cloud Messaging (FCM) bildirim altyapısı ile donatılmıştır.
</p>

---

## 📌 Proje Genel Bakışı (Overview)

**Hafıza Oyunu**, nesne yönelimli programlama (OOP) ve temiz kod (Clean Code) prensipleri benimsenerek Java ile native olarak geliştirilmiş bir Android mobil uygulamasıdır. Kullanıcıların görsel hafızasını ve odaklanma yeteneğini test ederken, anlık skor hesaplama formülü ve dinamik liderlik tablosu ile rekabetçi bir kullanıcı deneyimi sunar.

### 🌟 Öne Çıkan Yetenekler

- 🎴 **Akıcı 3D Çevirme Animasyonları:** Android ObjectAnimator & Property Animation API kullanılarak pürüzsüz kart dönüş efektleri.
- 📐 **Dinamik Izgara & Zorluk Seçenekleri:** 
  - **Kolay:** 4x4 (8 Çift - Hızlı Isınma)
  - **Orta:** 6x6 (18 Çift - Standart Zorluk)
  - **Zor:** 8x8 (32 Çift - Üst Düzey Hafıza Mücadelesi)
- 🎨 **6 Farklı Zengin Tema Motoru:** Emojiler, Hayvanlar, Sayılar, Yiyecekler, Spor ve Bayraklar.
- ⚡ **Durum Koruma (Orientation Resilience):** Ekran yatay/dikey çevrildiğinde oyun akışını ve süreyi kaybetmeyen `Serializable` durum yönetimi.
- ☁️ **Gerçek Zamanlı Bulut Liderlik Tablosu:** Google Cloud Firestore entegrasyonu ile en yüksek skorların küresel takibi.
- 🔔 **Push Bildirim Desteği:** Firebase Cloud Messaging (FCM) altyapısı ile anlık etkileşim bildirimleri.
- 🔊 **Ses ve Haptik Geri Bildirim:** Eşleşme, hata, kart çevirme ve zafer anlarında dinamik ses efektleri (Açma/Kapatma ayarı mevcuttur).

---

## 🏛️ Mimari ve Tasarım Desenleri

Uygulama, bileşenlerin sorumluluklarının ayrıştırıldığı modüler ve sürdürülebilir bir mimari yapı üzerine inşa edilmiştir:

- **Adapter & ViewHolder Deseni:** `CardAdapter` ve `ScoreAdapter`, bellek optimizasyonu sağlayarak binlerce öğelik listelerde bile 60+ FPS pürüzsüz kaydırma ve render performansı sunar.
- **State Management:** Oyunun o anki tüm durumu (`GameState`), ekran yön değişikliklerinde (`onSaveInstanceState` / `onRestoreInstanceState`) serialize edilerek veri kaybı olmadan korunur.
- **Singleton & Manager Mimarisi:**
  - `FirebaseManager`: Firestore veritabanı okuma/yazma ve veri transfer nesnelerini (DTO) yönetir.
  - `SoundManager`: `SoundPool` havuzu ile ses gecikmelerini minimize eder ve bellek sızıntısını (memory leak) engeller.
  - `PreferenceManager`: Kullanıcı ayarlarını (ses, tema tercihi, son skorlar) `SharedPreferences` üzerinde kalıcı hale getirir.

---

## 🧮 Skorlama Algoritması

Skor sistemi; hız, hafıza hassasiyeti ve seçilen zorluk derecesini ödüllendiren matematiksel bir katsayı modeli kullanır:

$$\text{Toplam Skor} = \Big( \text{Temel Puan} + \text{Zaman Bonusu} + \text{Hamle Bonusu} \Big) \times \text{Zorluk Çarpanı}$$

| Bileşen | Formül / Değer | Açıklama |
| :--- | :--- | :--- |
| **Temel Puan** | $\text{Eşleşen Çift} \times 100$ | Başarılı her eşleşme için sabit taban puan |
| **Zaman Bonusu** | $\max(0, 500 - \text{saniye} \times 2)$ | Ne kadar hızlı bitirilirse o kadar yüksek bonus |
| **Hamle Bonusu** | $\max(0, (\text{Minimum Hamle} \times 3 - \text{Hamle}) \times 10)$ | Hata yapmadan doğru kartları bulmayı ödüllendirir |
| **Zorluk Çarpanı** | 4x4: $\times 1.0$ \| 6x6: $\times 1.5$ \| 8x8: $\times 2.0$ | Yüksek zorluk derecelerinde üstel ödül katsayısı |

---

## 📂 Proje Dizin Yapısı

```text
app/src/main/
├── java/com/example/hafizaoyunu/
│   ├── MainActivity.java                # Karşılama ve menü akışı
│   ├── GameActivity.java                # Oyun motoru ve ızgara yaşam döngüsü
│   ├── LeaderboardActivity.java         # Firestore skor tablosu ekranı
│   ├── SettingsActivity.java            # Kullanıcı tercihleri ve ayar yönetimi
│   ├── GridSpacingItemDecoration.java   # Izgara düzeni mesafe hesaplayıcı
│   ├── adapter/
│   │   ├── CardAdapter.java             # Izgara kart bağlayıcı ve animatör
│   │   └── ScoreAdapter.java            # Skor listesi ViewHolder adaptörü
│   ├── model/
│   │   ├── Card.java                    # Kart veri modeli ve durumu
│   │   ├── GameState.java               # Yaşam döngüsü durum koruma nesnesi
│   │   ├── Difficulty.java              # Zorluk seviyesi enumerasyonu
│   │   ├── Theme.java                   # Tema ve sembol haritalaması
│   │   └── HighScore.java               # Firestore DTO skor modeli
│   ├── firebase/
│   │   ├── FirebaseManager.java         # Firestore DAO ve CRUD operasyonları
│   │   └── MyFirebaseMessagingService.java # Arka plan bildirim dinleyicisi
│   └── utils/
│       ├── SoundManager.java            # Düşük gecikmeli ses motoru (SoundPool)
│       ├── PreferenceManager.java       # SharedPreferences yöneticisi
│       └── GameUtils.java               # Yardımcı algoritmalar ve matematik
└── res/
    ├── anim/                            # Sayfa ve kart geçiş animasyonları
    ├── animator/                        # 3D Flip rotasyon XML animatörleri
    ├── layout/                          # Optimize edilmiş XML UI tasarımları
    └── values/                          # Renk paletleri, tipografi ve temalar
```

---

## 🛠️ Teknoloji Yığını ve Gereksinimler

- **Geliştirme Ortamı:** Android Studio Hedgehog | Iguana | Jellyfish veya üzeri
- **Minimum Android Sürümü:** Android 7.0 (API 24, Nougat)
- **Hedef Android Sürümü:** Android 14 (API 34, UpsideDownCake)
- **Derleme Aracı:** Gradle 8.x
- **Kullanılan Kütüphaneler:**
  - AndroidX Core, AppCompat & Material Components
  - Firebase Firestore (Cloud Database)
  - Firebase Cloud Messaging (Push Notifications)

---

## 🚀 Kurulum ve Çalıştırma Kılavuzu

### 1. Repoyu Klonlayın
```bash
git clone https://github.com/<KULLANICI_ADINIZ>/<REPO_ADINIZ>.git
cd <REPO_ADINIZ>
```

### 2. Firebase Yapılandırması (Opsiyonel ama Önerilen)
Uygulama Firebase olmadan da lokal olarak kusursuz çalışır; ancak küresel skor tablosu ve bildirimler için:
1. [Firebase Console](https://console.firebase.google.com/)'a gidin ve yeni bir proje oluşturun.
2. Paket adı olarak `com.example.hafizaoyunu` girip Android uygulaması ekleyin.
3. İndirdiğiniz `google-services.json` dosyasını `app/` dizinine yerleştirin (örnek şablon için [`app/google-services.json.example`](file:///Users/mirackeser/Downloads/HafizaOyunu/app/google-services.json.example) dosyasına göz atabilirsiniz).
4. Firebase Console üzerinden **Cloud Firestore** veritabanını test modunda başlatın.

### 3. Derleme ve Çalıştırma
1. **Android Studio**'yu açıp `File > Open` ile proje dizinini seçin.
2. Gradle senkronizasyonunun (`Gradle Sync`) tamamlanmasını bekleyin.
3. Bir emülatör seçin veya USB Hata Ayıklama modundaki fiziksel Android cihazınızı bağlayın.
4. `Run (Shift + F10)` butonuna tıklayın.

---

## 🔒 Güvenlik ve Gizlilik Prensipleri

- Hassas veriler, SDK dizin yolları ve yerel derleme yapılandırmaları `local.properties` ve `.gitignore` ile kaynak kontrolünün dışında tutulmaktadır.
- Firebase güvenlik kuralları (`Firestore Security Rules`), istemci tarafında veri doğrulama yapılarak kötü niyetli veri girişlerini engelleyecek şekilde tasarlanmalıdır.

---

## 🤝 Katkıda Bulunma (Contributing)

Her türlü katkıya, hata bildirimine ve özellik talebine açığız:

1. Bu depoyu Fork edin (`Fork`).
2. Özellik dalınızı oluşturun (`git checkout -b feature/YeniOzellik`).
3. Değişikliklerinizi commit edin (`git commit -m 'feat: Yeni bir kart animasyonu eklendi'`).
4. Dalınıza push edin (`git push origin feature/YeniOzellik`).
5. Bir **Pull Request (PR)** açın.

---

## 📄 Lisans

Bu proje [MIT Lisansı](LICENSE) kapsamında açık kaynak olarak lisanslanmıştır. Detaylar için `LICENSE` dosyasına göz atabilirsiniz.
