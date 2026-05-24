package internetkafe.servis;

import com.fazecast.jSerialComm.SerialPort;
import com.fazecast.jSerialComm.SerialPortDataListener;
import com.fazecast.jSerialComm.SerialPortEvent;

public class ArduinoOkuyucu {

    private SerialPort port;
    private volatile double sonSicaklik = 22.0;
    private volatile boolean bagli = false;

    public void baglan(String portAdi) {
        try {
            port = SerialPort.getCommPort(portAdi);
            port.setBaudRate(9600);
            port.setNumDataBits(8);
            port.setNumStopBits(1);
            port.setParity(SerialPort.NO_PARITY);

            if (port.openPort()) {
                bagli = true;
                System.out.println("✅ Arduino bağlantısı kuruldu: " + portAdi);
                port.addDataListener(new SerialPortDataListener() {
                    @Override
                    public int getListeningEvents() {
                        return SerialPort.LISTENING_EVENT_DATA_AVAILABLE;
                    }

                    @Override
                    public void serialEvent(SerialPortEvent event) {
                        if (event.getEventType() == SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                            oku();
                        }
                    }
                });
            } else {
                System.err.println("❌ Port açılamadı: " + portAdi);
            }
        } catch (Exception e) {
            System.err.println("Arduino bağlantı hatası: " + e.getMessage());
        }
    }

    private void oku() {
        try {
            byte[] tampon = new byte[port.bytesAvailable()];
            int okunan = port.readBytes(tampon, tampon.length);
            if (okunan > 0) {
                String ham = new String(tampon, 0, okunan);
                String[] satirlar = ham.split("\\r?\\n");
                for (String satir : satirlar) {
                    satir = satir.trim();
                    if (!satir.isEmpty()) {
                        try {
                            sonSicaklik = Double.parseDouble(satir);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Okuma hatası: " + e.getMessage());
        }
    }

    public double getSonSicaklik() {
        return sonSicaklik;
    }

    public boolean isBagli() {
        return bagli;
    }

    public void kapat() {
        if (port != null && port.isOpen()) {
            port.closePort();
            bagli = false;
            System.out.println("Arduino bağlantısı kapatıldı.");
        }
    }
}