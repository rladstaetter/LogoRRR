#!/bin/bash

ICONSET_DEST=target/iconset/logorrr-iconset.iconset

mkdir -p $ICONSET_DEST

echo "Creating iconset structure"
cp target/icons/logorrr-icon-16.png   $ICONSET_DEST/icon_16x16.png
cp target/icons/logorrr-icon-32.png   $ICONSET_DEST/icon_16x16@2x.png
cp target/icons/logorrr-icon-32.png   $ICONSET_DEST/icon_32x32.png
cp target/icons/logorrr-icon-64.png   $ICONSET_DEST/icon_32x32@2x.png
cp target/icons/logorrr-icon-128.png  $ICONSET_DEST/icon_128x128.png
cp target/icons/logorrr-icon-256.png  $ICONSET_DEST/icon_128x128@2x.png
cp target/icons/logorrr-icon-256.png  $ICONSET_DEST/icon_256x256.png
cp target/icons/logorrr-icon-512.png  $ICONSET_DEST/icon_256x256@2x.png
cp target/icons/logorrr-icon-512.png  $ICONSET_DEST/icon_512x512.png
cp target/icons/logorrr-icon-1024.png $ICONSET_DEST/icon_512x512@2x.png

echo "Creating iconset with iconutil"
iconutil -c icns $ICONSET_DEST

echo "Finished"
exit 0