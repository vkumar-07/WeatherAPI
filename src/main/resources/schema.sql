CREATE TABLE IF NOT EXISTS userProfile (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255),
    pass VARCHAR(255),
    location VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS location (
    id SERIAL PRIMARY KEY,
    location VARCHAR(255),
    name VARCHAR(255),
    region VARCHAR(255),
    country VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS forecastDay (
    id SERIAL PRIMARY KEY,
    forecast_id BIGINT NOT NULL,
    date DATE NOT NULL,
    maxtemp_c DOUBLE PRECISION,
    maxtemp_f DOUBLE PRECISION,
    mintemp_c DOUBLE PRECISION,
    mintemp_f DOUBLE PRECISION,
    date_epoch BIGINT
);

CREATE TABLE IF NOT EXISTS forecast (
    id SERIAL PRIMARY KEY,
    location VARCHAR(255),
    last_updated_epoch BIGINT,
    last_updated VARCHAR(255),
    temp_c DOUBLE PRECISION,
    temp_f DOUBLE PRECISION,
    is_day INTEGER,
    wind_mph DOUBLE PRECISION,
    wind_kph DOUBLE PRECISION,
    wind_degree INTEGER,
    wind_dir VARCHAR(50),
    pressure_mb DOUBLE PRECISION,
    pressure_in DOUBLE PRECISION,
    precip_mm DOUBLE PRECISION,
    precip_in DOUBLE PRECISION,
    humidity INTEGER,
    cloud INTEGER,
    feelslike_c DOUBLE PRECISION,
    feelslike_f DOUBLE PRECISION,
    vis_km DOUBLE PRECISION,
    vis_miles DOUBLE PRECISION,
    uv DOUBLE PRECISION,
    gust_mph DOUBLE PRECISION,
    gust_kph DOUBLE PRECISION
);

--INSERT INTO public.userProfile(username, pass, location) VALUES ('user1', 'password', null);