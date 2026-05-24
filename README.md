# Internet Kafe Yönetim Sistemi 🚀

Bu proje, bir internet kafenin günlük operasyonlarını yönetmek için tasarlanmış kapsamlı bir masaüstü uygulamasıdır. Müşteri yönetimi, bilgisayar oturum takibi, ödeme işlemleri ve ortam sıcaklığı izleme gibi temel işlevleri tek bir merkezde toplar.

## 🌟 Özellikler

- **💻 Bilgisayar Yönetimi:** Masaüstü bilgisayarların durumlarını (Boş/Dolu/Arızalı) gerçek zamanlı takip etme.
- **👥 Müşteri Yönetimi:** Müşteri kayıtları, bakiye yükleme ve harcama geçmişi takibi.
- **⏱️ Oturum ve Ödeme Sistemi:** Bilgisayarlarda oturum başlatma, süre takibi yapma ve oturum sonunda otomatik ücret hesaplama.
- **🌡️ Arduino ile Ortam Takibi:** Arduino sıcaklık sensörleri entegrasyonu sayesinde kafe içerisindeki anlık sıcaklığı sistem üzerinden izleyebilme.
- **📊 Raporlama:** Gelir raporları, müşteri aktivite raporları ve sistem kullanım istatistiklerini görüntüleme.

## 🛠️ Kullanılan Teknolojiler

- **Java (Swing):** Masaüstü grafik kullanıcı arayüzü (GUI).
- **SQLite:** Veritabanı yönetimi (yerel depolama).
- **jSerialComm:** Java üzerinden Arduino ile seri port haberleşmesi.
- **Arduino:** Ortam sıcaklık verilerini okuyan donanım entegrasyonu.

## 📂 Proje Yapısı

```text
InternetKafe/
├── baslat.bat                # Projeyi başlatmak için toplu iş dosyası
├── internetkafe.db           # SQLite veritabanı dosyası
├── TemperatureSensors.ino    # Arduino sıcaklık sensörü kaynak kodu
├── src/                      # Java kaynak kodları
│   └── internetkafe/         # Ana paket (GUI, Model, Servisler)
├── lib/                      # Gerekli kütüphaneler (SQLite, jSerialComm, vb.)
└── out/                      # Derlenmiş sınıf (.class) dosyaları
```

## 🚀 Kurulum ve Çalıştırma

### Gereksinimler
- **Java Runtime Environment (JRE) / JDK 8 veya üzeri**
- (İsteğe bağlı) Ortam sıcaklık ölçümü için **Arduino donanımı** ve sensörler.

### Adımlar
1. Projeyi bilgisayarınıza klonlayın:
   ```bash
   git clone https://github.com/emirgul-web/InternetKafe.git
   ```
2. Proje dizinine gidin.
3. Uygulamayı başlatmak için `baslat.bat` dosyasına çift tıklayın veya komut satırından çalıştırın:
   ```cmd
   baslat.bat
   ```

### Arduino Kurulumu (İsteğe Bağlı)
Eğer sıcaklık takibini donanım ile test etmek isterseniz:
1. `TemperatureSensors.ino` dosyasını Arduino IDE ile açın.
2. Kodları Arduino kartınıza yükleyin.
3. Uygulama içerisinden seri port bağlantısını başlatarak sıcaklık okumalarını arayüzde görüntüleyin.

## 📸 Ekran Görüntüleri

*(Projenize ait ekran görüntülerini buraya ekleyebilirsiniz)*

---
**Geliştirici:** [@emirgul-web](https://github.com/emirgul-web)
