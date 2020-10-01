# Pretty and functional FreeBSD desktop for design freaks - step by step

FreeBSD is the best server operating system out there and it has everything linux has for desktop use besides drivers. The FreeBSD dev community is much smaller than the Linux dev community so not all laptop driver is arriving for FreeBSD and the others are arriving slowly - usually 1-2 years is needed. So if ypu want the FreeBSD laptop experience you better start on az older supported laptop or go with a desktop machine.

So why choose FreeBSD over Linux? Easy :
- one kernel one distro - no fragmentation
- exteremely well architected, clean design
- no systemd and pulse audio
- no instability, freezing, random things, surprises

Why not choose FreeBSD over Linux?
- not supported laptop
- you need Spotify, Netflix or Steam

I'm installing FreeBSD on my new laptop ( one year old ) so I will go with the latest CURRENT ( = dev branch or nightly build ) brach, which is 13.0-CURRENT currently. 

Download the latest memstick image from here :
https://download.freebsd.org/ftp/snapshots/ISO-IMAGES/13.0/

Burn it to a flash drive with your favorite tool and boot your machine with it.

1 Select install
2 Select default keyboard
3 Choose host name
4 At Optional System Components select ports tree and system source tree, they can be useful when you want to recompile the kernel for some reason or an utility is non-BSD licensed and not present in pkg repository.
4 Partition your drive - Auto UFS is the best for laptops but feel free to go with other
Let the installer create the partitions
5 Select root password
6 Select network - if there is no driver for your wifi hardware then no wireless will appear in the list
Let the installer auto config DHCP and everything
7 Select region
8 Select time and date
9 Select services at boot : ntpd powerd and dumpdev
10 No hardening options
11 Add a separate user besides root
12 Reboot

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
