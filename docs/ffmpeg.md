# Convert mov to gif

ffmpeg -i logorrr-poc.mov -pix_fmt rgb8 -s 389x308 out.gif
convert out.gif -verbose -coalesce -layers OptimizeFrame out-preopt.gif
gifsicle -O2 out-preopt.gif -o out-final.gif
