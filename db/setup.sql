-- Одноразовая подготовка PostgreSQL: роли и база данных.
-- Запускать от суперпользователя, пароли передаются параметрами (в файл не записываются):
--   psql -U postgres -v owner_pass=ПАРОЛЬ1 -v service_pass=ПАРОЛЬ2 -f db/setup.sql
-- Пароли должны совпадать с PG_OWNER_PASS и PG_SERVICE_PASS в файле .env

-- Владелец схемы: создаёт таблицы (используется только при инициализации БД)
CREATE ROLE demo_owner LOGIN PASSWORD :'owner_pass';

-- Пользователь сервиса: без прав на изменение структуры, только работа с данными
CREATE ROLE demo_service LOGIN PASSWORD :'service_pass';

CREATE DATABASE rbpo_2026 OWNER demo_owner;
