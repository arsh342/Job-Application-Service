#!/bin/bash

# Kill all Spring Boot services started by run.sh (by killing the tmux session)
tmux kill-session -t jobapp 2>/dev/null

# Optionally, kill any stray Java processes for these services (uncomment if needed)
# pkill -f 'Application.*spring-boot:run'
# pkill -f 'Authentication.*spring-boot:run'
# pkill -f 'Job.*spring-boot:run'

echo "All Job Application services stopped."
