# Pretty and functional linux desktop for former Mac users and design freaks

# install
Start with
Manjaro architect
default settings
custom package network manager
install display drivers 

# startup
reboot

** connect to wifi **
sudo systemct enable network manager 

( start ? )

nmcli -d WiFi connect networkname -password password

** install sway with waybar and xwayland bridge so x apps can work **
sudo pacman -S sway waybar xorg-sxserver-wayland wayland=protocols

** install default sway terminal and launcher **
sudo pacman -S dmenu alacrity 

** start sway **
type sway

press WIN(MAC) + ENTER to open terminal
press WIN(MAC) + D to open dmenu and start typing to launch something
press WIN(MAC) + numbers to switch desktops
press WIN(MAC) + SHIFT + numbers to move window to another desktop
press WIN(MAC) + r to resize window

# google chrome

** install AUR package manager **
sudo pacman -S yay

** install google chrome **
yay S google-chrome

# copy default sway and waybar configs

mkdir .config/sway 

mkdir .config/waybar

cp /etc/xdg/waybar/* ~/.config/waybar

cp /etc/sway/config ~/.config/sway

# display brightness control

sudo pacman -S light

set execution permissions to user for backlight

sudo chmod a+w /sys/class/backlight/intel_backlight/brightness  

nano ~/.config/sway/config

bindsym XF86MonBrightnessUp exec light -A 5    # increase screen brightness

bindsym XF86MonBrightnessDown exec light -U 5  # decrease screen brightness

nano - ctrl w, alt u to undo, ctrl o to save, ctrl x exit

nano ~/.config/waybar/config

"backlight": {
   "on-scroll-up" : "light -A 5",
   "on-scroll-down" : "light -U 5"
}

press WIN(MAC) + SHIFT + C to reload sway config

now you can control display brightness with the brightness keys on the keyboard and by moving over the display brightness block on swaybar and scroll up/down

# volume control

sudo pacman -S pulseaudio alsa-utils

this enables and starts alsa service and installs handy utils

start

alsamixer

unmute needed channels with M, left/right arrow to change sources

nano ~/.config/sway/config

bindsym XF86AudioRaiseVolume exec --no-startup-id pactl set-sink-volume @DEFAULT_SINK@ +10%

bindsym XF86AudioLowerVolume exec --no-startup-id pactl set-sink-volume @DEFAULT_SINK@ -10%

bindsym XF86AudioMute exec --no-startup-id pactl set-sink-mute @DEFAULT_SINK@ toggle

press WIN(MAC) + SHIFT + C to reload sway config

now you can control volume with the volume keys on the keyboard and by moving over the volume block on swaybar and scroll up/down

nano ~/.config/waybar/config

add this to pulseaudio :

"on-click": "swaymsg exec '$term -e alsamixer'"

so on click it will bring up alsamixer for deeper control

to fix bluetooth audio problems ( shown by systemctl --user status pulseaudio )

systemctl enable bluetooth.service to get rid of error in systemctl


# touchpad tweaks

get your touchpad's hardware id

libinput -list-devices

nano ~/.config/sway/config

** enable tap and natural scroll **
input 1739:52575:MSFT0001:01_06CB:CD5F_Touchpad tap enabled
input 1739:52575:MSFT0001:01_06CB:CD5F_Touchpad natural_scroll enabled

** make touchpad smoother **
input 1739:52575:MSFT0001:01_06CB:CD5F_Touchpad accel_profile flat
input 1739:52575:MSFT0001:01_06CB:CD5F_Touchpad pointer_accel 0


# autostart sway

nano ~/.bash_profile

or

nano ~/.zshrc

if [[ -z $DISPLAY ]] && [[ $(tty) = /dev/tty1 ]]; then
  XKB_DEFAULT_LAYOUT=us exec sway
fi

# idle, lock, sleep

sudo pacman -S swaylock swayidle

nano ~/.config/sway/config

uncomment this :

exec swayidle -w \
          timeout 300 'swaylock -f -c 000000' \
          timeout 600 'swaymsg "output * dpms off"' \
               resume 'swaymsg "output * dpms on"' \
          before-sleep 'swaylock -f -c 000000'


# wifi selector

nano ~/.config/waybar/config

add this to network :

"on-click": "swaymsg exec '$term -e nmtui-connect'"

# mailspring

yay -S mailspring gnome-keyring

at first startup gnome-keyring asks for a password, leave it blank, no other apps will use it

# simplenote

yay -S simplenote

what do we loved ini macos? screenshot!
grim/slurp 
for screenshot?
sway win key plus numbers, alt enter, alt d
 
# gaps
gaps inner 5
gaps top -10

# borders
default_border pixel 1
default_floating_border pixel 1

# language switch

nano ~/.config/sway/config

add this with your device id

input "1165:49408:ITE_Tech._Inc._ITE_Device(8910)_Keyboard" {
	xkb_layout "us,hu"
	xkb_variant ",101_qwerty_dot_nodead"
	xkb_options "grp:alt_space_toggle"
	repeat_rate 60
    	repeat_delay 250
}

# waybar pimp

dark transparent background

nano ~/.config/waybar/style.css

modify window#waybar :

background-color: rgba(0,0,0,0.2);

/* border-bottom: 3px solid rgba(100, 114, 125, 0.5); */

modify common block css :

#clock,
#battery,
#cpu,
#memory,
#temperature,
#backlight,
#network,
#pulseaudio,
#custom-media,
#tray,
#mode,
#disk,
#idle_inhibitor,
#mpd {
    padding: 0 10px 0 10px;
    margin: 0 4px;
    color: #ffffff;
    border-bottom:3px solid #ffffff;
}

delete background colors from all individual block css's ( CTRL-K in nano )

set colors to black

nano ~/.config/waybar/config

remove unnecessary symbols from blocks

rearrange blocks

add disk icon :

"disk":{
     "format":"{percentage_free}%  "
},


and bring icons to the left of the labels

# auto-open stuff in proper app

sudo pacman -S xdg-utils

# alacritty

mkdir -p ~/.config/alacritty
cp /usr/share/doc/alacritty/example/alacritty.yml ~/.config/alacritty

nano ~/.config/alacritty/alacritty.yml

invert colors :

colors:
  # Default colors
  primary:
    background: '0xffffff'
    foreground: '0x222200'
