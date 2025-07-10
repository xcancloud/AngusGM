#!/bin/bash
set -eo pipefail

# Check required parameters
required_vars=("ENV_FILE" "OUTPUT_PATH" "INPUT_FILE_PATHS")
missing_vars=()
for var in "${required_vars[@]}"; do
    if [[ -z "${!var}" ]]; then
        missing_vars+=("$var")
    fi
done

if [[ ${#missing_vars[@]} -gt 0 ]]; then
    echo "Error: The following environment variables must be set:"
    printf "  - %s\n" "${missing_vars[@]}"
    exit 1
fi

# Default to full substitution if not specified
FULL_SUBSTITUTION=${FULL_SUBSTITUTION:-true}

# Load environment variables
if [[ "$FULL_SUBSTITUTION" == "true" ]]; then
    # Full substitution: load all variables into environment
    export $(grep -v '^#' "$ENV_FILE" | xargs -0)
else
    # Selective substitution: load variables into associative array
    declare -A env_vars
    while IFS='=' read -r key value; do
        if [[ ! $key =~ ^[[:space:]]*# && -n $key ]]; then
            # Remove quotes from value
            value="${value%\"}"
            value="${value#\"}"
            env_vars["$key"]="$value"
        fi
    done < <(grep -v '^#' "$ENV_FILE" | grep '=')
fi

# Create output directory
mkdir -p "$OUTPUT_PATH"

# Process each file
for file in $INPUT_FILE_PATHS; do
    if [ ! -f "$file" ]; then
        echo "Warning: File $file does not exist, skipping"
        continue
    fi

    filename=$(basename "$file")
    if [ -d "$OUTPUT_PATH" ]; then
        output_file="$OUTPUT_PATH/$filename"
    else
        output_file="$OUTPUT_PATH"
    fi

    # 关键优化：检查输出文件是否已存在
    if [[ -e "$output_file" ]]; then
        echo "Output file $output_file already exists, skipping"
        continue
    fi

    # 确保输出目录存在
    output_dir=$(dirname "$output_file")
    mkdir -p "$output_dir"

    echo "Processing file: $file -> $output_file"

    if [[ "$FULL_SUBSTITUTION" == "true" ]]; then
        # Full substitution using envsubst (overwrite)
        envsubst < "$file" > "$output_file"
    else
        # Create temporary file for atomic replacement
        temp_output=$(mktemp)

        # Process each line with selective substitution
        while IFS= read -r line; do
            # Find all ${VAR} patterns in the line
            while [[ $line =~ (\$\{[a-zA-Z_][a-zA-Z0-9_]*\}) ]]; do
                full_match="${BASH_REMATCH[0]}"
                var_name="${full_match:2:-1}"  # Remove ${ and }

                # Replace only if variable exists in .env
                if [[ -v env_vars["$var_name"] ]]; then
                    replacement="${env_vars[$var_name]}"
                    line="${line//$full_match/$replacement}"
                else
                    break  # Skip to next match
                fi
            done
            echo "$line" >> "$temp_output"
        done < "$file"

        # Atomically replace output file
        mv -f "$temp_output" "$output_file"
    fi
done

echo "Processing completed! Results output to: $OUTPUT_PATH"
echo "Substitution mode: $FULL_SUBSTITUTION"
