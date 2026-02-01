# Kaspi File Uploader

## Требования
- Java 21
- Docker (для запуска с PostgreSQL)

## Запуск через Docker
1. В корне проекта выполните:
   - `docker compose up --build`
2. Приложение будет доступно на http://localhost:8080

## API
### Загрузка файла
`POST /api/uploads`

Headers:
- `X-Client-Id` — идентификатор клиента
- `X-Idempotency-Key` — ключ идемпотентности

Form-data:
- `file` — файл

Ответ:
- `202 Accepted` — запрос принят, обработка в фоне
- `200 OK` — если запрос уже завершён (идемпотентный повтор)

### Проверка статуса
`GET /api/uploads/{requestId}`

## Идемпотентность
Комбинация `X-Client-Id` + `X-Idempotency-Key` уникальна. Повторный запрос с теми же значениями не создаст дубликатов и вернёт текущий статус.
