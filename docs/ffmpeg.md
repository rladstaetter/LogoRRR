# Convert mp4 to gif

ffmpeg -i \#177-copy-to-clipboard.mp4 -vf "fps=10,scale=320:-1:flags=lanczos" -c:v gif -f gif \#177-copy-to-clipboard.gif

