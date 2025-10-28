#!/bin/bash

# --- Input Validation ---

if [ "$#" -ne 5 ]; then
    echo "Error: Expected 5 arguments, received $#."
    echo "Usage: $0 <server_address> <username> <local_file_path> <remote_dir_path> <private_key_path>"
    echo "Example: $0 your-server username /path/to/local/file.deb public_html/downloads/1.0.0 ~/.ssh/id_ed25519"
    exit 1
fi

REMOTE_HOST=$1
REMOTE_USER=$2
LOCAL_FILE=$3
REMOTE_DIR=$4
PRIVATE_KEY_PATH=$5

# Resolve tilde in key path
PRIVATE_KEY_PATH=$(eval echo "$PRIVATE_KEY_PATH")

# --- Check Prerequisites ---

if [ ! -f "$LOCAL_FILE" ]; then
    echo "Error: Local file not found: $LOCAL_FILE"
    exit 1
fi

if [ ! -f "$PRIVATE_KEY_PATH" ]; then
    echo "Error: Private key file not found: $PRIVATE_KEY_PATH"
    echo "Please ensure the private key path is correct and accessible."
    exit 1
fi

# --- Execute SFTP Upload using Key Authentication ---

echo "🚀 Starting SFTP upload using key authentication..."
echo "Local File: **$LOCAL_FILE**"
echo "Remote Dir: sftp://${REMOTE_USER}@${REMOTE_HOST}/$REMOTE_DIR/"
echo "Private Key: $PRIVATE_KEY_PATH"

# The sftp batch commands are fed directly via process substitution.
sftp -i "$PRIVATE_KEY_PATH" -b <(cat <<EOF
put "${LOCAL_FILE}" "${REMOTE_DIR}/"
quit
EOF
) "${REMOTE_USER}@${REMOTE_HOST}"

# --- Final Status ---
UPLOAD_STATUS=$?

if [ $UPLOAD_STATUS -eq 0 ]; then
    echo "✅ File successfully uploaded."
else
    echo "❌ Upload failed with error code $UPLOAD_STATUS."
    echo "Please ensure your public key is installed correctly on the server for user ${REMOTE_USER}."
fi

exit $UPLOAD_STATUS