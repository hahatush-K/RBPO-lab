DROP SCHEMA IF EXISTS app CASCADE;
CREATE SCHEMA app AUTHORIZATION demo_owner;

CREATE TABLE app.users
(
    id    UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name  VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE
);

CREATE TABLE app.shipments
(
    id        UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    sender_id UUID           NOT NULL REFERENCES app.users (id) ON DELETE CASCADE,
    address   VARCHAR(255)   NOT NULL,
    mass      NUMERIC(10, 3) NOT NULL CHECK (mass > 0),
    status    VARCHAR(20)    NOT NULL DEFAULT 'NEW'
        CHECK (status IN ('NEW', 'ASSIGNED', 'IN_TRANSIT', 'DELIVERED'))
);

CREATE TABLE app.routes
(
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    route_date DATE           NOT NULL,
    max_mass   NUMERIC(10, 3) NOT NULL CHECK (max_mass > 0),
    status     VARCHAR(20)    NOT NULL DEFAULT 'PLANNED'
        CHECK (status IN ('PLANNED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'))
);

CREATE TABLE app.stops
(
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    route_id     UUID         NOT NULL REFERENCES app.routes (id) ON DELETE CASCADE,
    address      VARCHAR(255) NOT NULL,
    order_number INTEGER      NOT NULL CHECK (order_number > 0),
    UNIQUE (route_id, order_number)
);

CREATE TABLE app.shipment_assignments
(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    shipment_id UUID        NOT NULL REFERENCES app.shipments (id) ON DELETE CASCADE,
    stop_id     UUID        NOT NULL REFERENCES app.stops (id) ON DELETE CASCADE,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING'
        CHECK (status IN ('PENDING', 'DELIVERED', 'CANCELLED'))
);

-- У отправления может быть только одно действующее (не отменённое) назначение
CREATE UNIQUE INDEX uq_shipment_active_assignment
    ON app.shipment_assignments (shipment_id) WHERE status <> 'CANCELLED';

GRANT USAGE ON SCHEMA app TO demo_service;
GRANT SELECT, INSERT, UPDATE, DELETE
    ON TABLE app.users, app.shipments, app.routes, app.stops, app.shipment_assignments TO demo_service;
