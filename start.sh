#!/bin/bash
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-21.jdk/Contents/Home
# ═══════════════════════════════════════════════════════════════
#  FoodOrderingApp — Full Stack Startup Script
#  Starts everything in the correct order:
#  1. Infrastructure  (MySQL · PostgreSQL · MongoDB · Kafka)
#  2. Food Backend    (Spring Boot · port 5454)
#  3. Delivery Backend(Spring Boot · port 8082)
#  4. Food Frontend   (Vite React · port 5173)
#  5. Delivery Frontend (Vite React · port 5174)
# ═══════════════════════════════════════════════════════════════

set -e

ROOT="/Users/jai/Documents/FoodOrderingApp"
FOOD_BACKEND="$ROOT/FoodOrderingApp"
DELIVERY_BACKEND="$ROOT/Delivery Partner/deliveryPartner"
FOOD_FRONTEND="$ROOT/FoodOrdering-frontend"
DELIVERY_FRONTEND="$ROOT/delivery-frontend"
MYSQL="/usr/local/mysql/bin/mysql"
LOG_DIR="$ROOT/.logs"

# ── Env vars required by Spring Boot ──────────────────────────
export Jwt_Key="your-256-bit-secret-your-256-bit-secret-your-256-bit-secret"
export payment_Key="sk_test_51SeHobQdOqw18SPTYWfIqGK5ZGyYhq7l9dtyig12fDVtYzbUThNYiDGK8D0MKfYjebM3EgEMSW24KUNacZK6pLV7005k0HHNCY"

# ── Colors ────────────────────────────────────────────────────
RED='\033[0;31m'; GREEN='\033[0;32m'; YELLOW='\033[1;33m'
BLUE='\033[0;34m'; CYAN='\033[0;36m'; BOLD='\033[1m'; NC='\033[0m'

log()  { echo -e "${BOLD}${BLUE}[$(date +%H:%M:%S)]${NC} $1"; }
ok()   { echo -e "${GREEN}  ✅ $1${NC}"; }
warn() { echo -e "${YELLOW}  ⚠️  $1${NC}"; }
err()  { echo -e "${RED}  ❌ $1${NC}"; }
sep()  { echo -e "${CYAN}──────────────────────────────────────────${NC}"; }

mkdir -p "$LOG_DIR"

# ── Helper: wait for a port to be open ────────────────────────
wait_for_port() {
  local port=$1; local name=$2; local timeout=${3:-60}
  echo -ne "  ⏳ Waiting for $name (port $port)"
  for i in $(seq 1 $timeout); do
    if nc -z localhost "$port" 2>/dev/null; then
      echo -e " ${GREEN}✅ up${NC}"
      return 0
    fi
    echo -n "."
    sleep 1
  done
  echo ""
  err "$name did not start in ${timeout}s — check $LOG_DIR/$name.log"
  return 1
}

# ── Helper: check if port is already in use ───────────────────
port_in_use() { nc -z localhost "$1" 2>/dev/null; }

# ══════════════════════════════════════════════════════════════
echo ""
echo -e "${BOLD}${CYAN}"
echo "  ███████╗ ██████╗  ██████╗ ██████╗      █████╗ ██████╗ ██████╗ "
echo "  ██╔════╝██╔═══██╗██╔═══██╗██╔══██╗    ██╔══██╗██╔══██╗██╔══██╗"
echo "  █████╗  ██║   ██║██║   ██║██║  ██║    ███████║██████╔╝██████╔╝"
echo "  ██╔══╝  ██║   ██║██║   ██║██║  ██║    ██╔══██║██╔═══╝ ██╔═══╝ "
echo "  ██║     ╚██████╔╝╚██████╔╝██████╔╝    ██║  ██║██║     ██║     "
echo "  ╚═╝      ╚═════╝  ╚═════╝ ╚═════╝     ╚═╝  ╚═╝╚═╝     ╚═╝     "
echo -e "${NC}"
echo -e "  Starting all services for ${BOLD}FoodOrderingApp${NC}..."
echo ""

# ══════════════════════════════════════════════════════════════
# STEP 1 — INFRASTRUCTURE
# ══════════════════════════════════════════════════════════════
sep
log "STEP 1 — Checking Infrastructure (MySQL · PostgreSQL · MongoDB · Kafka)"
sep

# MySQL ───────────────────────────────────────────────────────
if port_in_use 3306; then
  ok "MySQL is already running (port 3306)"
else
  warn "MySQL not detected — attempting to start..."
  sudo /usr/local/mysql/support-files/mysql.server start 2>/dev/null || warn "Could not auto-start MySQL. Start it from System Preferences → MySQL."
  wait_for_port 3306 "MySQL" 30
fi

# Docker Infrastructure (PostgreSQL, MongoDB, Kafka) ─────────
DOCKER_COMPOSE_FILE="$DELIVERY_BACKEND/docker-compose.yml"

if [ -f "$DOCKER_COMPOSE_FILE" ]; then
  log "Starting Docker Infrastructure (PostgreSQL, MongoDB, Kafka)..."
  docker compose -f "$DOCKER_COMPOSE_FILE" up -d 2>"$LOG_DIR/docker.log"
  
  wait_for_port 5433 "PostgreSQL" 30
  wait_for_port 27017 "MongoDB" 30
  wait_for_port 9092 "Kafka" 60
else
  warn "docker-compose.yml not found at $DOCKER_COMPOSE_FILE"
  warn "Make sure PostgreSQL, MongoDB, and Kafka are running manually."
fi

echo ""
ok "All infrastructure services are UP!"

# ══════════════════════════════════════════════════════════════
# STEP 2 — FOOD ORDERING BACKEND (Spring Boot · Port 5454)
# ══════════════════════════════════════════════════════════════
sep
log "STEP 2 — Starting Food Ordering Backend (port 5454)"
sep

if port_in_use 5454; then
  ok "Food backend already running on port 5454"
else
  cd "$FOOD_BACKEND"
  nohup ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev \
    > "$LOG_DIR/food-backend.log" 2>&1 &
  FOOD_PID=$!
  echo "  📋 PID: $FOOD_PID  |  Log: $LOG_DIR/food-backend.log"
  wait_for_port 5454 "Food Backend" 90
fi

# ══════════════════════════════════════════════════════════════
# STEP 3 — DELIVERY PARTNER BACKEND (Spring Boot · Port 8082)
# ══════════════════════════════════════════════════════════════
sep
log "STEP 3 — Starting Delivery Partner Backend (port 8082)"
sep

if port_in_use 8082; then
  ok "Delivery backend already running on port 8082"
else
  cd "$DELIVERY_BACKEND"
  nohup ./mvnw spring-boot:run \
    > "$LOG_DIR/delivery-backend.log" 2>&1 &
  DELIVERY_PID=$!
  echo "  📋 PID: $DELIVERY_PID  |  Log: $LOG_DIR/delivery-backend.log"
  wait_for_port 8082 "Delivery Backend" 90
fi

# ══════════════════════════════════════════════════════════════
# STEP 4 — FOOD ORDERING FRONTEND (Vite · Port 5173)
# ══════════════════════════════════════════════════════════════
sep
log "STEP 4 — Starting Food Ordering Frontend (port 5173)"
sep

if port_in_use 5173; then
  ok "Food frontend already running on port 5173"
else
  cd "$FOOD_FRONTEND"
  nohup npm run dev > "$LOG_DIR/food-frontend.log" 2>&1 &
  echo "  📋 Log: $LOG_DIR/food-frontend.log"
  wait_for_port 5173 "Food Frontend" 30
fi

# ══════════════════════════════════════════════════════════════
# STEP 5 — DELIVERY PARTNER FRONTEND (Vite · Port 5174)
# ══════════════════════════════════════════════════════════════
sep
log "STEP 5 — Starting Delivery Partner Frontend (port 5174)"
sep

if port_in_use 5174; then
  ok "Delivery frontend already running on port 5174"
else
  cd "$DELIVERY_FRONTEND"
  nohup npm run dev > "$LOG_DIR/delivery-frontend.log" 2>&1 &
  echo "  📋 Log: $LOG_DIR/delivery-frontend.log"
  wait_for_port 5174 "Delivery Frontend" 30
fi

# ══════════════════════════════════════════════════════════════
# DONE — Print URLs
# ══════════════════════════════════════════════════════════════
sep
echo ""
echo -e "${BOLD}${GREEN}  🚀 All services are UP!${NC}"
echo ""
echo -e "  ${BOLD}Application URLs${NC}"
echo -e "  ┌──────────────────────────────────────────────┐"
echo -e "  │  🍔  Food App (Customer)                      │"
echo -e "  │      http://localhost:5173                    │"
echo -e "  │                                               │"
echo -e "  │  🚴  Delivery Partner App                     │"
echo -e "  │      http://localhost:5174                    │"
echo -e "  └──────────────────────────────────────────────┘"
echo ""
echo -e "  ${BOLD}Backend APIs${NC}"
echo -e "  ┌──────────────────────────────────────────────┐"
echo -e "  │  🍽️   Food API       → http://localhost:5454  │"
echo -e "  │  🚚  Delivery API    → http://localhost:8082  │"
echo -e "  └──────────────────────────────────────────────┘"
echo ""
echo -e "  ${BOLD}Infrastructure${NC}"
echo -e "  ┌──────────────────────────────────────────────┐"
echo -e "  │  🐬  MySQL       → localhost:3306             │"
echo -e "  │  🐘  PostgreSQL  → localhost:5433             │"
echo -e "  │  🍃  MongoDB     → localhost:27017            │"
echo -e "  │  📨  Kafka       → localhost:9092             │"
echo -e "  │  📊  Kafka UI    → http://localhost:8090      │"
echo -e "  └──────────────────────────────────────────────┘"
echo ""
echo -e "  ${BOLD}Logs folder:${NC} $LOG_DIR"
echo ""
sep

# Open browser tabs (optional — comment out if you don't want this)
sleep 2
open "http://localhost:5173" 2>/dev/null
open "http://localhost:5174" 2>/dev/null
