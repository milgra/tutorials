# Pretty and functional linux desktop for former Mac users and design freaks

Start with
Manjaro architect
default settings
custom package network manager
install display drivers 
reboot
sudo systemct enable network manager
nmcli -d WiFi connect network name -password password
sudo pacman -s insta sway xorg Zealand Wayland protocols
pacman s dmenu alacrity 
pacman s yay
yay d Google chrome
alacrity
dmenu
xorg xserver Wayland
Wayland protocols

mkdir .config/sway .config/waybar
copy default sway
copy def waybar cp /etc/xdg/waybar/* ~/.config/waybar
cp /etc/sway/config ~/.config/sway

adding display brightness
pulseaudio
alsa-utils - enables alse service

alsamixer, unmute channels, set volume with scroll

bindsym XF86AudioRaiseVolume exec --no-startup-id pactl set-sink-volume @DEFAULT_SINK@ +10%
bindsym XF86AudioLowerVolume exec --no-startup-id pactl set-sink-volume @DEFAULT_SINK@ -10%
bindsym XF86AudioMute exec --no-startup-id pactl set-sink-mute @DEFAULT_SINK@ toggle

systemctl enable networkmanager

systemctl enable bluetooth.service to get rid of error in systemctl --user status pulseaudio

set execution permissions to user for backlight
sudo chmod a+w /sys/class/backlight/intel_backlight/brightness  

what do we loved ini macos? screenshot!
grim/slurp for screenshot?

touchpad

libinput -list-devices

# enable tap and natural scroll
input 1739:52575:MSFT0001:01_06CB:CD5F_Touchpad tap enabled
input 1739:52575:MSFT0001:01_06CB:CD5F_Touchpad natural_scroll enabled
input 1739:52575:MSFT0001:01_06CB:CD5F_Touchpad accel_profile flat
input 1739:52575:MSFT0001:01_06CB:CD5F_Touchpad pointer_accel 0
