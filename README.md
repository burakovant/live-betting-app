# Live Betting Application

---

## Proje Tanımı

Live Betting Application, online bahis platformlarının temel ihtiyaçlarını karşılamak için geliştirilmiş bir canlı bahis yönetim uygulamasıdır. Uygulama, belirli miktarda örnek maç ve bu maçlar için oran (odds) bilgilerini yönetebilmekte ve çeşitli canlı bahis durumlarını simüle eden bir altyapı sunmaktadır.

Bu uygulama, **Spring Boot** framework'ü kullanılarak uygulama geliştirme hızını artıran modern bir mimari ile inşa edilmiştir. Temel olarak aşağıdaki özellikleri sunar:

- Çeşitli liglerden otomatik olarak rastgele maç üretimi.
- Her maça bağlı, güncel oran (odds) yönetimi.
- Oranların dinamik olarak güncellenmesini sağlayan yapı.
- Test edilebilirlik için hataların ve edge-case senaryoların handle edilmesi.
- H2 veritabanı kullanılarak hafif ve esnek veri yönetimi.

---

## Kullanılan Teknolojiler

### Backend
- **Java 17**: Modern ve güçlü uygulama geliştirme için kullanılıyor.
- **Spring Boot 3.4.2**: Hızlı uygulama geliştirme ve düzen kolaylığı sağlıyor.

### Veritabanı
- **H2 Database**: Hafif test senaryoları ve lokal geliştirme için.

### Çerçeve/Destek Kütüphaneleri
- **Spring Data JPA**: ORM işlemleri (entity yönetimi) için.
- **Spring Scheduler**: Zamanlanmış görevler için API sağlayıcı.

---

## Proje Mimarisi

1. **Entity'ler (Varlıklar)**:
    - **`Match`**: Her maç bilgisi bu entity ile temsil edilir. Lig, ev sahibi takım, deplasman takım bilgilerini içerir.
    - **`Odds`**: Her maçla ilişkilendirilmiş oran bilgilerini içeren entity. (İç saha galibiyeti (_Home Win_), beraberlik (_Draw_), dış saha galibiyeti (_Away Win_)).

2. **Repository'ler**:
    - **`MatchRepository`**: `Match` entity'si ile ilgili database operasyonlarını yönetir.
    - **`OddsRepository`**: `Odds` entity'sine CRUD operasyonları sağlar.

3. **Uygulama Başlatıcı** (`CommandLineRunner`):
    - Uygulama başlarken örnek ligler ve takımlarla rastgele maçlar oluşturur.
    - Bu maçlara ait oranlar otomatik olarak atanır.

4. **Zamanlanmış Görevler**:
    - `@EnableScheduling` anotasyonu ile zamanlayıcı işlemler aktif edilir ve oranları güncelleyebilecek altyapının temelleri sağlanır.

---

## Başlangıç Talimatları

### Uygulamanın Çalıştırılması

1. **Kodun Çekilmesi**:
   GitHub'daki repo klonlanarak kod bilgisayara alınır:
   ```bash
   git clone https://github.com/burakovant/live-betting-app.git
   cd live-betting-app
   ```

2. **Dependency'lerin İndirilmesi**:
   Maven kullanarak tüm bağımlılıkları indirin:
   ```bash
   mvn clean install
   ```

3. **Uygulamanın Ayağa Kaldırılması**:
   Uygulamayı çalıştırmak için aşağıdaki komutu kullanın:
   ```bash
   mvn spring-boot:run
   ```

4. **H2 Konsoluna Erişim**:
   Varsayılan olarak H2 konsol admin paneline erişebilirsiniz:
   ```text
   URL: http://localhost:8080/h2-console
   JDBC URL: jdbc:h2:mem:testdb
   User: db_admin
   Password: password
   ```

---

## Test Senaryoları

Uygulama, hem happy-path (ideal senaryolar) hem de corner-case (köşe durumlar) için test edilmiştir.

### 1. Happy-Path Senaryoları
- Rastgele üretilen bir maçın oranlarının doğru şekilde atanması.
- Veritabanındaki `Match` ve `Odds` ilişkisi üzerinden bağlantılı verilerin doğru çekilmesi.
- Tüm endpoint'lerden beklenen verilerin başarıyla dönmesi.

### 2. Corner-Case Senaryoları
- 'Odds' verilerinin geçmişinin eskiden yeniye doğru düzgün sıralandığının kontrolü.
- Bir maça maksimum 500 çoklu kupon yapılabildiğinin kontrolü.
- Kupon oynama isteğinin 2 saniyeyi geçtiği durumda hata fırlatılıp işlemlerin rollback edildiğinin kontrolü.


---

## Gelecekteki Geliştirmeler

1. **Canlı Oran Yayını**:
   Maçlar esnasında oranların gerçek zamanlı yayımlanmasını sağlayan bir WebSocket mimarisi.

2. **Kullanıcı Etkileşimi**:
   Kullanıcıların favori takımlara bahis yapması ve sanal hesap yönetimi eklenebilir.

3. **Dış API Entegrasyonu**:
   Gerçek lig bilgilerini bir spor servisi API'sinden alarak simülasyonu zenginleştirmek.


---

## Yazarlar ve Lisans

Bu proje, **Burak Sezin Ovant** tarafından geliştirilmiş ve açık kaynaklı olarak yayınlanmıştır. Lisans, MIT Lisans formatındadır.