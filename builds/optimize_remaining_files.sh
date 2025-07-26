#!/bin/bash

# Java Code Optimization Script
# This script helps systematically process and optimize remaining Java files

set -e

# Configuration
WORKSPACE_DIR="/workspace"
LOG_FILE="${WORKSPACE_DIR}/optimization.log"
PROCESSED_FILES="${WORKSPACE_DIR}/processed_files.txt"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Logging function
log() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')]${NC} $1" | tee -a "$LOG_FILE"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$LOG_FILE"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1" | tee -a "$LOG_FILE"
}

info() {
    echo -e "${BLUE}[INFO]${NC} $1" | tee -a "$LOG_FILE"
}

# Initialize log file
echo "=== Java Code Optimization Log ===" > "$LOG_FILE"
echo "Started at: $(date)" >> "$LOG_FILE"

# Create processed files tracking
touch "$PROCESSED_FILES"

# Function to check if file was already processed
is_processed() {
    local file="$1"
    grep -Fxq "$file" "$PROCESSED_FILES" 2>/dev/null
}

# Function to mark file as processed
mark_processed() {
    local file="$1"
    echo "$file" >> "$PROCESSED_FILES"
}

# Function to get file category based on path
get_file_category() {
    local file="$1"
    case "$file" in
        */agent/*) echo "agent" ;;
        */model/*) echo "model" ;;
        */core/*) echo "core" ;;
        */runner/*) echo "runner" ;;
        */test/*) echo "test" ;;
        */csv/*) echo "csv" ;;
        */lab/*) echo "lab" ;;
        */remoting/*) echo "remoting" ;;
        *) echo "other" ;;
    esac
}

# Function to check if file needs optimization
needs_optimization() {
    local file="$1"

    # Skip if already processed
    if is_processed "$file"; then
        return 1
    fi

    # Check if file has minimal documentation
    local has_class_doc=$(grep -c "^/\*\*" "$file" 2>/dev/null || echo 0)
    local has_method_doc=$(grep -c "^\s*\*/" "$file" 2>/dev/null || echo 0)

    # If file has very little documentation, it needs optimization
    if [ "$has_class_doc" -lt 1 ] || [ "$has_method_doc" -lt 2 ]; then
        return 0
    fi

    # Check for Chinese comments (basic check)
    if grep -q "[\u4e00-\u9fff]" "$file" 2>/dev/null; then
        return 0
    fi

    # Check for TODO comments in Chinese
    if grep -q "TODO.*[\u4e00-\u9fff]" "$file" 2>/dev/null; then
        return 0
    fi

    return 1
}

# Function to analyze file complexity
analyze_file() {
    local file="$1"
    local lines=$(wc -l < "$file")
    local classes=$(grep -c "^public class\|^class\|^public interface\|^interface" "$file" 2>/dev/null || echo 0)
    local methods=$(grep -c "public.*(" "$file" 2>/dev/null || echo 0)

    echo "Lines: $lines, Classes: $classes, Methods: $methods"
}

# Function to get optimization priority
get_priority() {
    local file="$1"
    local category=$(get_file_category "$file")

    case "$category" in
        "core"|"runner") echo "HIGH" ;;
        "model"|"agent") echo "MEDIUM" ;;
        "test"|"lab") echo "LOW" ;;
        *) echo "MEDIUM" ;;
    esac
}

# Function to create optimization checklist for a file
create_checklist() {
    local file="$1"
    local category=$(get_file_category "$file")
    local priority=$(get_priority "$file")

    cat << EOF

=== OPTIMIZATION CHECKLIST FOR: $file ===
Category: $category
Priority: $priority
Analysis: $(analyze_file "$file")

1. Documentation Tasks:
   [ ] Add comprehensive class-level documentation with <p> tags
   [ ] Document all public methods with parameters and return values
   [ ] Document all fields with purpose and usage
   [ ] Add thread safety notes if applicable
   [ ] Add performance considerations if applicable

2. Code Quality Tasks:
   [ ] Add null checks and parameter validation
   [ ] Improve error handling and exception messages
   [ ] Add proper resource management (try-with-resources)
   [ ] Review and optimize imports
   [ ] Check for code duplication

3. Comment Optimization:
   [ ] Convert all comments to English
   [ ] Remove redundant or obvious comments
   [ ] Improve comment accuracy and clarity
   [ ] Add missing documentation for complex logic
   [ ] Standardize comment formatting

4. Specific Checks for $category module:
EOF

    case "$category" in
        "core")
            echo "   [ ] Verify utility class patterns (private constructor)"
            echo "   [ ] Check singleton implementations for thread safety"
            echo "   [ ] Validate configuration handling"
            ;;
        "runner")
            echo "   [ ] Document thread management and lifecycle"
            echo "   [ ] Add shutdown sequence documentation"
            echo "   [ ] Verify executor service patterns"
            ;;
        "model")
            echo "   [ ] Document validation rules and constraints"
            echo "   [ ] Add builder pattern documentation if applicable"
            echo "   [ ] Verify equals/hashCode implementations"
            ;;
        "agent")
            echo "   [ ] Document message structure and protocol"
            echo "   [ ] Add serialization/deserialization notes"
            echo "   [ ] Verify DTO/VO patterns"
            ;;
        "test")
            echo "   [ ] Document test scenarios and expectations"
            echo "   [ ] Add setup/teardown documentation"
            echo "   [ ] Verify test data and mocking"
            ;;
    esac

    echo ""
}

# Main processing function
process_files() {
    local priority_filter="$1"

    log "Starting optimization process for priority: ${priority_filter:-ALL}"

    # Find all Java files
    find "$WORKSPACE_DIR" -name "*.java" -type f | while read -r file; do
        local category=$(get_file_category "$file")
        local priority=$(get_priority "$file")

        # Skip if priority filter is specified and doesn't match
        if [ -n "$priority_filter" ] && [ "$priority" != "$priority_filter" ]; then
            continue
        fi

        # Check if file needs optimization
        if needs_optimization "$file"; then
            info "File needs optimization: $file"
            create_checklist "$file"

            # Here you would call your optimization function
            # optimize_file "$file"

            # For now, just mark as processed to avoid reprocessing
            # mark_processed "$file"
        else
            info "File already optimized or doesn't need optimization: $file"
        fi
    done
}

# Function to generate statistics
generate_stats() {
    log "Generating optimization statistics..."

    local total_files=$(find "$WORKSPACE_DIR" -name "*.java" -type f | wc -l)
    local processed_files=$(wc -l < "$PROCESSED_FILES" 2>/dev/null || echo 0)
    local remaining_files=$((total_files - processed_files))

    echo ""
    echo "=== OPTIMIZATION STATISTICS ==="
    echo "Total Java files: $total_files"
    echo "Processed files: $processed_files"
    echo "Remaining files: $remaining_files"
    echo "Progress: $(( processed_files * 100 / total_files ))%"
    echo ""

    # Category breakdown
    echo "=== BY CATEGORY ==="
    for category in agent model core runner test csv lab remoting other; do
        local count=$(find "$WORKSPACE_DIR" -name "*.java" -type f | while read file; do
            if [ "$(get_file_category "$file")" = "$category" ]; then
                echo "$file"
            fi
        done | wc -l)
        echo "$category: $count files"
    done
    echo ""

    # Priority breakdown
    echo "=== BY PRIORITY ==="
    for priority in HIGH MEDIUM LOW; do
        local count=$(find "$WORKSPACE_DIR" -name "*.java" -type f | while read file; do
            if [ "$(get_priority "$file")" = "$priority" ]; then
                echo "$file"
            fi
        done | wc -l)
        echo "$priority: $count files"
    done
}

# Function to validate optimized files
validate_optimizations() {
    log "Validating optimized files..."

    local error_count=0

    while IFS= read -r file; do
        if [ -f "$file" ]; then
            # Check if file compiles (basic syntax check)
            if ! javac -cp "." "$file" -d /tmp/java_validation 2>/dev/null; then
                error "Compilation error in: $file"
                ((error_count++))
            fi
        fi
    done < "$PROCESSED_FILES"

    if [ $error_count -eq 0 ]; then
        log "All optimized files passed validation"
    else
        error "$error_count files failed validation"
    fi

    return $error_count
}

# Main script logic
main() {
    case "${1:-help}" in
        "stats")
            generate_stats
            ;;
        "high")
            process_files "HIGH"
            ;;
        "medium")
            process_files "MEDIUM"
            ;;
        "low")
            process_files "LOW"
            ;;
        "all")
            process_files
            ;;
        "validate")
            validate_optimizations
            ;;
        "help"|*)
            cat << EOF
Java Code Optimization Script

Usage: $0 [command]

Commands:
  stats    - Generate optimization statistics
  high     - Process HIGH priority files only
  medium   - Process MEDIUM priority files only
  low      - Process LOW priority files only
  all      - Process all files
  validate - Validate optimized files
  help     - Show this help message

Examples:
  $0 stats          # Show current progress
  $0 high           # Process high priority files
  $0 all            # Process all remaining files
  $0 validate       # Check optimized files

Files are categorized by module and prioritized as follows:
  HIGH:   core, runner modules
  MEDIUM: model, agent modules
  LOW:    test, lab modules

Log file: $LOG_FILE
Progress tracking: $PROCESSED_FILES
EOF
            ;;
    esac
}

# Run main function with all arguments
main "$@"
