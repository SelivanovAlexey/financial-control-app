#!/bin/bash
set -e

echo "Starting application..."
exec java -Dspring.profiles.active=${BUILD_ENV} -jar financial-app.jar