# Web Push Demo – Spring Boot + Nginx + mkcert

This repository shows the **minimal, production‑safe** way to send browser Web Push notifications from a Java/Spring Boot backend to Chrome/Firefox.

---

## 0  Prerequisites

* **Ubuntu 20.04 +** (tested on 24.04) — any Linux will work
* **JDK 17 или 21**
  `sdk install java 21-tem` / `sudo apt install openjdk-17-jdk`
* **Gradle 8** (обёртка уже в проекте) или Maven 3.9
* **Node 18+** – только чтобы один раз сгенерировать VAPID‑ключи
  `nvm install 18`
* **mkcert** + **Nginx** (для локального HTTPS)
* Порт `8080` свободен для Spring, `443` — для Nginx

---

## 1  TLS для локальной разработки

### 1.1  Установить mkcert и Nginx

```
sudo apt update && sudo apt install nginx libnss3-tools -y
sudo snap install mkcert  # или brew/linuxbrew install mkcert
mkcert -install           # добавляет CA в trust‑store Chrome/Firefox
mkcert localhost 127.0.0.1 ::1
sudo mkdir -p /etc/nginx/certs
sudo mv localhost*.pem /etc/nginx/certs/
```

### 1.2  Конфигурация /etc/nginx/sites-available/localhost-ssl.conf

```
server {
    listen 443 ssl http2;
    server_name localhost 127.0.0.1;

    ssl_certificate     /etc/nginx/certs/localhost.pem;
    ssl_certificate_key /etc/nginx/certs/localhost-key.pem;

    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers   HIGH:!aNULL:!MD5;

    location / {
        proxy_pass         http://127.0.0.1:8080;
        proxy_set_header   Host $host;
        proxy_set_header   X-Real-IP $remote_addr;
        proxy_set_header   X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header   X-Forwarded-Proto https;
    }
}
server { listen 80; server_name localhost 127.0.0.1; return 301 https://$host$request_uri; }
```

```
sudo ln -s /etc/nginx/sites-available/localhost-ssl.conf /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx
```

Теперь **[https://localhost](https://localhost)** отдаёт Spring‑приложение.

---

## 2  Генерация VAPID‑ключей

```
npx web-push generate-vapid-keys --json > vapid.json
cat vapid.json
#  "publicKey":  "BP…88 симв."
#  "privateKey": "WS…43 симв."
```

* Скопируйте `publicKey` в **frontend** (`index.html`, переменная `vapid`)
* Скопируйте `publicKey` и `privateKey` в **backend** `src/main/resources/application.yml`:

```
push:
  vapid:
    public:  BP...
    private: WS...
    subject: mailto:dev@example.com
```

(Конфиденциальный privateKey лучше хранить в переменной окружения или Vault.)

После смены ключей **обязательно** удаляйте старые подписки в браузере.

---

## 3  Сборка и запуск backend

```
./gradlew clean bootRun   # или mvn spring-boot:run
```

Backend стартует на `localhost:8080`, Nginx проксирует → `https://localhost`.
Логи при первом старте:

* `✅  Bouncy Castle provider registered`
* `Started DemoPushApplication`

---

## 4  Фронтенд‑часть

В проекте уже лежат файлы:

* `src/main/resources/static/index.html` – кнопка «Subscribe & Send»
* `src/main/resources/static/sw.js` – Service Worker с обработкой *push*

При открытии `https://localhost`:

1. Браузер запрашивает разрешение на notifications → нажмите **Allow**.
2. Service Worker регистрируется, подписка отправляется в `/api/push/subscribe/{user}`.
3. Backend по REST `/api/push/notify/{user}` мгновенно шлёт тестовый push (JSON `{title, body}`), FCM возвращает **201 Created**.
4. На рабочем столе GNOME/Windows всплывает баннер «Hello / It works!».

---

## 5  Частые ошибки и решения

| Симптом                                                  | Решение                                                                                         |
| -------------------------------------------------------- | ----------------------------------------------------------------------------------------------- |
| 403 Forbidden + *“crypto-key header had invalid format”* | Вызывайте `client.send(notification, Encoding.AES128GCM)`, пересоздайте подписку.               |
| 403 Forbidden + *“UnauthorizedRegistration”*             | Подписка устарела. Удалите и подпишитесь заново.                                                |
| `SecurityError: Service Worker registration failed`      | Страница не по HTTPS, либо `sw.js` — 404. Проверить DevTools → Network.                         |
| Баннер не отображается, хотя 201                         | Вкладка активна либо OS‑notifications выключены. Закройте вкладку / проверьте «Do Not Disturb». |

---

🎉 Теперь у вас полностью рабочий стек для Web Push‑уведомлений на локальном HTTPS‑стенде.
