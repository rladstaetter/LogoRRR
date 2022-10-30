# https://gource.io - gource is a tool to visualize code repos
# checkout https://github.com/acaudwell/Gource/wiki/Videos for mor information
gource --title "History of LogoRRR so far" --seconds-per-day 1 --hide bloom -b FFFFFF --font-colour 111111 -c 4 --filename-colour 000000 -800x600 --user-image-dir /Users/lad/gh/LogoRRR/docs/gource -o - | ffmpeg -y -r 60 -f image2pipe -vcodec ppm -i - -vcodec libx264 -preset ultrafast -pix_fmt yuv420p -crf 17 -threads 0 -bf 0 logorrr-history.mp4
