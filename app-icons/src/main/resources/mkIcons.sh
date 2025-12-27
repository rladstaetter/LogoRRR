#!/bin/bash

ICONSET_DEST=../../../../dist/dist-osx/installer-osx/src/main/installer/icon/logorrr-iconset.iconset

echo "Exporting icons to png"
for size in 16 32 64 128 256 512 1024; do
  inkscape --export-type=png --export-width=$size --export-filename="logorrr-icon-${size}.png" icon.svg
done

echo "Creating icon files"
for size in 16 32 64 128 256; do
  convert logorrr-icon-$size.png logorrr-icon-$size.ico
done

echo "Creating iconset"
cp logorrr-icon-16.png   $ICONSET_DEST/icon_16x16.png
cp logorrr-icon-32.png   $ICONSET_DEST/icon_16x16@2x.png
cp logorrr-icon-32.png   $ICONSET_DEST/icon_32x32.png
cp logorrr-icon-64.png   $ICONSET_DEST/icon_32x32@2x.png
cp logorrr-icon-128.png  $ICONSET_DEST/icon_128x128.png
cp logorrr-icon-256.png  $ICONSET_DEST/icon_128x128@2x.png
cp logorrr-icon-256.png  $ICONSET_DEST/icon_256x256.png
cp logorrr-icon-512.png  $ICONSET_DEST/icon_256x256@2x.png
cp logorrr-icon-512.png  $ICONSET_DEST/icon_512x512.png
cp logorrr-icon-1024.png $ICONSET_DEST/icon_512x512@2x.png

echo "Finished"
exit 0