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

If your system keeps on using the wrong device (HDMI instead of PCH or vica versa for example), you can force ALSA to use the correct device. Start by getting a list of your audio devices with the command:

cat /proc/asound/cards

Note the number of the sound device that you want to make the primary. Then using a text editor, put the following into /etc/asound.conf (You may need to create /etc/asound.conf if it doesn't already exist).

  defaults.pcm.card 0
  defaults.ctl.card 0
