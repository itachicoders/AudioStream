# AudioStreamClient

# Example Java TCP Server
```java
import java.io.*;
import java.net.*;

public class AudioRelayServer {

    public static void main(String[] args) {
        final int PORT = 50005;
        System.out.println("Сервер запущен на порту " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Ожидаем отправителя...");
            Socket senderSocket = serverSocket.accept();
            System.out.println("Отправитель подключен: " + senderSocket.getInetAddress());

            System.out.println("Ожидаем получателя...");
            Socket receiverSocket = serverSocket.accept();
            System.out.println("Получатель подключен: " + receiverSocket.getInetAddress());

            InputStream senderInput = senderSocket.getInputStream();
            OutputStream receiverOutput = receiverSocket.getOutputStream();

            byte[] buffer = new byte[4096];
            int bytesRead;

            System.out.println("Передача аудио началась...");
            while ((bytesRead = senderInput.read(buffer)) != -1) {
                receiverOutput.write(buffer, 0, bytesRead);
            }

            System.out.println("Передача завершена.");

            senderSocket.close();
            receiverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
```
