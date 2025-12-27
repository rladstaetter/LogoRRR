#!/bin/bash

echo "Exporting icons to png"
for size in 16 32 64 128 256 512 1024; do
  inkscape --export-type=png --export-width=$size --export-filename="logorrr-icon-${size}.png" icon.svg
done

echo "Creating icon files"
for size in 16 32 64 128 256; do
  convert logorrr-icon-$size.png logorrr-icon-$size.ico
done

echo "Finished"
exit 0