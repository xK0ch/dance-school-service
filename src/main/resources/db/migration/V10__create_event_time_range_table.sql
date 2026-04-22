CREATE TABLE event_time_range (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    event_id UUID NOT NULL REFERENCES event(id),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now()
);
