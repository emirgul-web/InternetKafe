// Sensör pinleri
#define PIN_EN_YAKIN   A0
#define PIN_ORTA_1     A1
#define PIN_ORTA_2     A2
#define PIN_UZAK_1     A3
#define PIN_UZAK_2     A4
#define PIN_EN_UZAK    A5

// Ağırlık katsayıları
const int K_EN_YAKIN = 1;
const int K_ORTA     = 2;
const int K_UZAK     = 3;
const int K_EN_UZAK  = 4;
const int TOPLAM_KATSAYI = 15;  // 1+2+2+3+3+4 ,6 tane sensör kullanacağım.

// LM35 çevrimi (10 mV/°C, 5V referans)
float analogOkutSicaklik(int pin) {
  int ham = analogRead(pin);
  float voltaj = ham * (5.0 / 1023.0);
  return voltaj * 100.0;
}

void setup() {
  Serial.begin(9600);
}

void loop() {
  float sicaklik[6];

  // Gerçek sensörlerden oku
  sicaklik[0] = analogOkutSicaklik(PIN_EN_YAKIN);
  sicaklik[1] = analogOkutSicaklik(PIN_ORTA_1);
  sicaklik[2] = analogOkutSicaklik(PIN_ORTA_2);
  sicaklik[3] = analogOkutSicaklik(PIN_UZAK_1);
  sicaklik[4] = analogOkutSicaklik(PIN_UZAK_2);
  sicaklik[5] = analogOkutSicaklik(PIN_EN_UZAK);

  // Ağırlıklı ortalama hesabı
  float agirlikliOrtalama = (
      sicaklik[0] * K_EN_YAKIN +
      sicaklik[1] * K_ORTA +
      sicaklik[2] * K_ORTA +
      sicaklik[3] * K_UZAK +
      sicaklik[4] * K_UZAK +
      sicaklik[5] * K_EN_UZAK
  ) / TOPLAM_KATSAYI;

  // Bir ondalık basamakla seri porta yaz
  Serial.println(agirlikliOrtalama, 1);

  delay(1000); // 1 saniyede bir güncelle
}