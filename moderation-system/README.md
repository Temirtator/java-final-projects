# Moderation System

Проект 2: Модерация обращений клиентов (event-driven)

## Состав
- Service-1: слушает Kafka Topic-1, вызывает Service-2, применяет правила, публикует в Topic-2
- Service-2: REST сервис, берёт данные из Redis
- PostgreSQL: хранение идемпотентности и активных обращений
- Kafka: Topic-1 и Topic-2
- Redis: кэш расширенной информации

## Запуск
Из корня проекта:

- `docker compose up --build`

Порты:
- Service-1: 8081
- Service-2: 8082
- Kafka: 9092
- Redis: 6379
- PostgreSQL: 5433

## Правила модерации
1) Если событие уже обработано (eventId есть в БД) — выход
2) Если у клиента уже есть активное обращение по той же категории — выход
3) Если категория входит в `rules.restricted-categories` и сейчас вне рабочего времени — выход
4) Если все правила пройдены — отправка результата в Topic-2

## Service-2 (Redis)
Ключ: `enrichment:{clientId}:{category}`
Поля: `customerType`, `riskLevel`

При отсутствии данных возвращается `found=false`.

## Пример события (Topic-1)
```json
{
  "eventId": "evt-1",
  "clientId": "client-1",
  "category": "fraud",
  "createdAt": "2026-02-01T10:00:00+06:00"
}
```

## Проверка
Отправь сообщение в Topic-1 (например, через kafka-console-producer) и проверь, что Service-1 публикует результат в Topic-2.
