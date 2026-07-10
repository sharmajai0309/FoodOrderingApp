#!/usr/bin/env bash
# ═══════════════════════════════════════════════════════════════
#  FoodOrderingApp — Stop Script
#  Stops the backends and frontends running on known ports.
# ═══════════════════════════════════════════════════════════════

set -u

# Colors
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m'

log()  { echo -e "${CYAN}•${NC} $1"; }
ok()   { echo -e "${GREEN}✅ $1${NC}"; }
warn() { echo -e "${YELLOW}⚠️  $1${NC}"; }

echo ""
echo -e "${BOLD}${RED}  Stopping FoodOrderingApp Services...${NC}"
echo ""

stop_port() {
    local port=$1
    local name=$2
    
    # Find process ID using lsof
    local pids=$(lsof -ti :$port 2>/dev/null)
    
    if [ -n "$pids" ]; then
        log "Stopping $name (Port $port)..."
        for pid in $pids; do
            kill -9 $pid 2>/dev/null
        done
        ok "$name stopped."
    else
        warn "$name is not running on port $port."
    fi
}

stop_port 5454 "Food Backend"
stop_port 8082 "Delivery Backend"
stop_port 5173 "Food Frontend"
stop_port 5174 "Delivery Frontend"

echo ""
ok "All application services have been stopped."
echo "Note: Infrastructure (MySQL, Postgres, etc.) is still running in the background."
echo ""
