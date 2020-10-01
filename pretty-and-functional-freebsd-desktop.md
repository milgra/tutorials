After 15 years of MacOS and 1 year of Linux finally I moved to FreeBSD. I wanted to do that right before MacOS but hardware support for new laptops are leaking down slowly to FreeBSD.

freebsd

well.architectured piece of software. no Spotify or netflix

freebsd 13 current to use the latest/beta drivers
usb stick, boot up
select install
default keyboard
random hostname
optional system components
add ports tree and system source tree
partition Auto UFS 
select entire disk or delete some partitions, finish, auto partition again, voila
root password
network interface - if there is no driver for your hardware no wireless is appearing
auto configure everything
select region
set time date
services at boot - ntpd powerd dumpdev
no hardening options
add user - don't use root

reboot
login as root
ping something
PKG install sudo
yes for PKG
yes for sudo

add user to sudoers

visudo
pgdn to bottom
go to end of line
press I
press enter
type
myuser ALL=(ALL) NOPASSWD: ALL

exit
login as your user

install zsh
sudo PKG install zsh

chsh

/bin/zsh

:wq

chsh -s zsh

exit, login

setup zsh after loginp

sudo PKG install sway dmenu alacritty

XDG_RUNTIME_DIR has to be set, like this

vi .zshrc
end press I
export XDGRUNTIMEDIR=/tmp

logout, login

sway

if doesn't run you need a video driver

Intel uhd 

PKG install drm-current-kmod

add i915kms to /etc/RC.conf
sudo sysrc kld_list += "i915kms"

now restart
sudo reboot

if the drivers fails to load, boot single user, dmesg

for touchpad install iichd
sudo PKG install iichd

sudo sysrc
​ig4_load="YES"
iicbus_load="YES"
iichid_load="YES"

kld_list+="ig4 iichid"

reboot, login, type sway

win d dmenu
win enter terminal
win shift q kill window


autostart sway
zshrc
if tty = ttyv0
exec sway

turn off GPU

sudo PKG install acpi_call

kldnlist+= "acpi_call'

mkdir Scripts
touch turn off GPU.sh
add command to sudo nopasswd
add it to zshrc before sway

reboot

stop beeping

sysrc allscreen_kbdflags="-b quiet.off"

mount Linux filesystems
sysrc kldnlist+="ext2fs"

auto mount

sysrc autofs_enable="YES"
sudo vi /etc/auto_master
uncomment media line

camera

sudo PKG install webcamd

sudo sysrc kldnlist+="cuse"
sudo sysrc webcamd_enable="YES"
sudo sysrc webcamd_0_flags="-d 0.2 - I 0 -v 0"

PW groupmod webcamd -m milgra

chromium

sudo PKG.install chromium

test webcam after reboot

sudo PKG install firefox

sudo PKG install rhythmbox gstreamer
sudo dbus-uuidgen --ensure

sudo PKG install x11/rxvt-unicode

sudo PKG install terminus-font
pimp sway

swaybar.sh
swaylock.sh
swaystart.sh
Inc brightness
Dec brightness
enable java

copy sway config

Firefox enable ssn

sudo PKG install emacs-nox 
copy Emacs conf

sudo PKG install clojure Leiningen

pimp zsh
pkg autosuggest
PKG syntax high
git download history substring

PKG install npm
npm install shadow cljs


sudo pkginstall libreoffice

sudo pkg install virtualbox-ose

firefox theme aurora australis

at setup setup wifi
install krm current devel
load i915 Kms
install sway
xdg temp sir set
touchpad install iichd akármi
enable evdev?
load iichd Kms

dmesg / grep pcm, iijd, video, etc
PKG info sway / default config
libinpit list devices
automount /etc automaster

RC conf loader conf sysctl conf

zsh - -etc/local/share plugin

volume, brightness - MOD key instead of Fn

report iwm 9560 startup problem, acpi, resume
