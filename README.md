Bank REST — Система управления банковскими картами

Bank REST — это полнофункциональное Spring Boot приложение для управления банковскими картами, поддерживающее безопасное хранение данных, переводы средств, разграничение прав доступа (администратор/пользователь) и интеграцию с Docker и Liquibase.

Функционал
Для администратора (ROLE_ADMIN)

-Создание, активация, блокировка и удаление карт

-Управление пользователями: создание, активация, блокировка, удаление

-Просмотр всех карт с фильтрацией и пагинацией

-Для пользователя (ROLE_USER)

-Создание собственных карт

-Просмотр своих карт (фильтрация + пагинация)

-Запрос на блокировку карты

-Просмотр баланса карты

-Переводы между своими картами

Безопасность

-Spring Security + JWT

-Ролевая модель доступа: ROLE_ADMIN и ROLE_USER

-Шифрование номера карты с помощью AES/GCM (AesGcm)

-Маскирование PAN в ответах (**** **** **** 1234)

-Валидация и централизованная обработка ошибок

Архитектура

-Controller: REST-эндпоинты

-Service: бизнес-логика и транзакции (@Transactional)

-Repository: Spring Data JPA для работы с БД

-Entity / DTO: модели данных и объекты передачи данных

Основные модули
src/main/java/com/example/bankcards
├── controller          # REST контроллеры (Admin, User, Auth)
├── dto                 # DTO запросов/ответов (Card, User, Transfer, Balance)
├── entity              # JPA сущности (Card, User, Transfer)
├── repository          # Репозитории Spring Data JPA
├── service             # Сервисы бизнес-логики
├── security            # JWT и Spring Security конфигурация
└── util                # Утилиты (AES шифрование и др.)

Работа с БД

-PostgreSQL в связке с Liquibase:

-миграции лежат в src/main/resources/db/migration/

-автоматическое создание и обновление схемы при запуске

-

API документация

Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI YAML: http://localhost:8080/v3/api-docs.yaml / в папке docs проекта


Запуск через Docker

Собрать и запустить:

docker-compose up -d


Приложение доступно на:
http://localhost:8080

При необходимости пересборки:

docker-compose build --no-cache
docker-compose up -d

Тестирование

-Юнит-тесты для всех основных сервисов и контроллеров:

-CardService, UserService, TransferService

-AdminCardController, AdminUserController, AuthController, UserCardController, UserTransfersController

-Используется MockMvc, Mockito и spring-security-test для тестов контроллеров с проверкой безопасности.

Запуск:

mvn clean test

Дальнейшее развитие

Проект легко расширяем:

-Переводы между картами разных пользователей

-Добавление комиссий, мультивалютности, курсов валют

-Вынесение сервисов (User / Card / Transfer) в микросервисы

-Масштабирование через Kubernetes и Spring Cloud
 
-Подключение Prometheus/Grafana для мониторинга

Технологии

Java 17+

Spring Boot 3.x

Spring Security, Spring Data JPA

PostgreSQL, Liquibase

JWT

Docker, Docker Compose

Swagger/OpenAPI

JUnit 5, Mockito

Статус проекта

Готов к использованию в продакшене / для демонстрации как pet project.
Проект полностью соответствует техническому заданию, поддерживает современный стек и легко расширяется.

Автор

Разработано как pet-project для демонстрации промышленного уровня разработки на Spring Boot
с безопасностью, шифрованием данных и полноценным CI/CD через Docker

By Burn221