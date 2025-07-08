#!/bin/bash

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

# Default to full substitution if not specified, the default value is true.
# If it is true, use the envsubst command in the script below to replace. Note: Non-existent variables will be replaced with empty;
# otherwise, customize the replacement method to only replace variables in .env and only replace those in the format of ${env variable}.
FULL_SUBSTITUTION=${FULL_SUBSTITUTION:-true}

# Load environment variables
if [[ "$FULL_SUBSTITUTION" == "true" ]]; then
    # Full substitution: load all variables into environment
    export $(grep -v '^#' "$ENV_FILE" | xargs)
else
    # Selective substitution: load variables into associative array
    declare -A env_vars
    while IFS='=' read -r key value; do
        if [[ ! $key =~ ^\s*# && -n $key ]]; then
            # Remove quotes from value
            value="${value%\"}"
            value="${value#\"}"
            env_vars["$key"]="$value"
        fi
    done < <(grep -v '^#' "$ENV_FILE" | grep '=')
fi

# Create output directory
mkdir -p "$OUTPUT_PATH"
# Delete the previous file and rewrite it
rm -rf "$OUTPUT_PATH/*"

# Process each file
for file in $INPUT_FILE_PATHS; do
    if [ ! -f "$file" ]; then
        echo "Warning: File $file does not exist, skipping"
        continue
    fi

    # Get relative path and create output directory
    output_dir="$OUTPUT_PATH"
    # mkdir -p "$output_dir"
    output_file="$output_dir/$(basename "$file")"

    echo "Processing file: $file -> $output_file"

    if [[ "$FULL_SUBSTITUTION" == "true" ]]; then
        # Full substitution using envsubst
        envsubst < "$file" > "$output_file"
    else
        # Selective substitution: only replace ${VAR} from .env
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
                    # Skip to next match if not found
                    break
                fi
            done
            echo "$line" >> "$output_file"
        done < "$file"
    fi
done

echo "Processing completed! Results output to: $OUTPUT_PATH"
echo "Substitution mode: $FULL_SUBSTITUTION"
