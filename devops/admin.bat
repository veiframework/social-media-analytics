@echo off
setlocal

echo Stopping container: social-media-analytics-admin-1
docker stop social-media-analytics-admin-1 >nul 2>&1

echo Removing image: social-media-analytics-admin:1.0.0
docker rmi -f social-media-analytics-admin:1.0.0 >nul 2>&1

echo Building service 'admin' with no cache...
docker compose -f docker-compose-service.yml build --no-cache admin

echo Starting ONLY the 'admin' service in detached mode...
docker compose -f docker-compose-service.yml up -d admin

echo.
echo Admin service rebuilt and started successfully!
pause