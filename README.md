# Tile Unbind Fix

An Xposed/LSPosed module to prevent the notification shade from unbinding quick settings tiles after 30 seconds, which seems to fix slow activation times and visual glitches.

The resource footprint should be minimal, that is, a TileService for each of your current third party tiles, which would normally stay for 30 seconds after closing the notification shade, and should instead be reused every time.

Not sure if the "3 active tiles" limit still applies, for that there's [someone else's module](https://github.com/Xposed-Modules-Repo/eu.hxreborn.qsboundlesstiles).

Tested on Android 15 / Magisk / LSPosed 2.1.0
