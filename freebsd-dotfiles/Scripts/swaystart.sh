swaymsg "workspace 1"
swaymsg "exec urxvt"
sleep 0.3
swaymsg "workspace 2"
swaymsg "exec urxvt"
swaymsg "exec urxvt"
sleep 0.3
swaymsg "workspace 3"
swaymsg "exec chrome"
sleep 1.0
swaymsg "workspace 4"
swaymsg "exec MOZ_ENABLE_WAYLAND=1 firefox -ssb https://www.messenger.com"
swaymsg "exec MOZ_ENABLE_WAYLAND=1 firefox -ssb https://web.telegram.org"
sleep 6.0
swaymsg "workspace 5"
swaymsg "exec MOZ_ENABLE_WAYLAND=1 firefox -ssb https://mail.milenia.hu"
swaymsg "exec MOZ_ENABLE_WAYLAND=1 firefox -ssb https://app.simplenote.com"
sleep 6.0
swaymsg "workspace 6"
swaymsg "exec MOZ_ENABLE_WAYLAND=1 firefox -new-window"
sudo sysctl sys.class.backlight.intel_backlight.bl_device.brightness=13333
