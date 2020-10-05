# Pretty and functional FreeBSD desktop for design freaks

![Image](screen1.png)
![Image](screen2.png)
![Image](screen3.png)

FreeBSD is the best (server) operating system and it has everything linux has for desktop use besides (laptop) device drivers. The FreeBSD user/dev community is much smaller than Linux's so not all device driver is arriving for FreeBSD and the others are arriving slowly - usually 1-2 years is needed. So if you want the FreeBSD laptop experience you better start with an older and supported laptop or go with a desktop machine.

**So why choose FreeBSD over Linux?**
- one kernel one distro - no fragmentation  
- exteremely well architected, clean design  
- logical command line tools (pkg install vs pacman -Qs, pkg info vs dpkg -s, pkg search vs pacman -Ss)  
- no systemd and pulse audio  
- no random malfunctions, surprises after rolling updates  

**Why not choose FreeBSD over Linux?**
- no drivers for your laptop  
- you need Spotify, Netflix or Steam

**Installing**

We will install the latest CURRENT ( = dev branch or nightly build ) which is 13.0-CURRENT to have the latest drivers. 

Download the latest memstick image from here :
https://download.freebsd.org/ftp/snapshots/ISO-IMAGES/13.0/

Burn it to a flash drive with your favorite tool and boot up your machine with it.

1 Select install  
2 Select default keyboard  
3 Choose host name  
4 At Optional System Components select ports tree and system source tree, they can be useful when you want to recompile the kernel for some reason or if an utility is non-BSD licensed and not present in pkg repository.  
4 Partition your drive - Auto UFS is the best for laptops but feel free to go with other, let the installer create the partitions  
5 Select root password  
6 Select network - if there is no driver for your wifi hardware then no wireless will appear in the list. Let the installer auto config DHCP and everything  
7 Select your region  
8 Select time and date  
9 Select services at boot : ntpd powerd and dumpdev  
10 No hardening options  
11 Add a separate user for yourself besides root
12 Reboot  

Cool, the base system is ready. Now we have to install the needed tools and drivers.

**Basic Setup**

Login with the root user

Check internet connection first : ping freebsd.org   
If it's not working you have to figure out why the wifi interface is down - dmesg, sysctl, etc, google bravely!

**pkg and sudo**

Install sudo first:

```
pkg install sudo
```

It will offer you to install pkg first, say yes. After installing pkg, say yes for sudo install.

Now we have to add your separate user to the sudoers file to make things comfortable. Type :

```
visudo
```

Go down/page down to the bottom, go to the end of the line, press i to enter edit mode, press enter to go to newline, type

```
youruser ALL=(ALL)
```

Press escape, type :wq to save it. If you want to quit without saveing press esc anytime and type :q!

Now your user is in the sudoers file. Type exit to log out as root and login as your user.

**default shell : zsh**

Let's install zsh because it can figure out your thoughts.

```
sudo pkg install zsh
```

Now we need to change the default shell to it :

```
chsh -s zsh
```

Cool, now log out with your user and log in, zsh will ask for default settings, generate a config with the default settings.

**window manager: sway**

```
sudo pkg install sway swaylock swayidle dmenu alacritty slurp grim
```

sway is the wayland based window manager  
swaylock is the screen lock utility for sway  
swayidle is the idle lock utility for sway  
dmenu is the default launcher for sway  
alacritty is the default wayland based terminal for sway  
slurp is the default screenshot utility for sway  
grim is the default screen region shot utility for sway  

For sway to run XDG_RUNTIME_DIR has to be set, let's add it to .zshrc

```
vi .zshrc
```

Go to the end of file, go into edit mode with i and type

```
export XDG_RUNTIME_DIR=/tmp
```

Press escape and type :wq to save and exit.

Logout and login again.

**video drivers**

After login, type

```
sway
```

If it shows up you have video drivers/the default driver works for you. If not you have to tell FreeBSD to load it at startup.
The video drivers for FreeBSD are called drm-kmod, drm-current-kmod, drm-devel-kmod.
Since we are on the CURRENT branch we have to use drm-current-kmod

```
sudo pkg install drm-current-kmod
```

Now depending on your GPU manufacturer you have to load the corresponding kernel mode settings. I have an intel UHD so I will go with i915kms. Adding new lines to /etc/rc.conf is done with an editor or with the sysrc tool, we will use the latter.

```
sudo sysrc kld_list += "i915kms"
```

Cool, now reboot by typing

```
sudo reboot
```
If the screen remained black after reboot, then you installed or selected the wrong driver. Don't panic, reboot in single user mode, it starts up with the bios vesa driver, you can safely login and remove the driver from /etc/rc.conf and then you can reboot and login in the normal way.

After login check if driver is loaded :

```
dmesg | grep drm
```

If it's loaded then great, start up sway.

```
sway
```

You should see a beautiful blue background. To open a terminal press WIN+ENTER. To open the launcher menu press WIN+d and start typing anything. To kill a window press WIN + SHIFT + q

Cool but why doesn't the touchpad working?!?!?

**touchpad**

It you have no touchpad you have to install the latest iichd drivers.

```
sudo pkg install iichd
```

And the let's load them at startup :

```
sudo sysrc ig4_load="YES"
sudo sysrc iicbus_load="YES"
sudo sysrc iichid_load="YES"
sudo sysrc kld_list+="ig4 iichid"
```

Reboot, login and touchpad should work.

**webcam**

To make the webcam work webcamd has to be installed and started, usb unit.addr has to be set ( the example should work for 99 percent of the machines ) and you have to add your user to webcamd group

```
sudo pkg install webcamd
sudo sysrc kld_list+="cuse"
sudo sysrc webcamd_enable="YES"
sudo sysrc webcamd_0_flags="-d 0.2 - I 0 -v 0"
sudo groupmod webcamd -m youruser
````

Reboot, login and webcam should work. Let's test it on the web!

**chromium, firefox**

```
sudo pkg install chromium
sudo pkg install firefox
```

Start up chromium and go to a webcam tester page, does it work?

**set default sway config**

Copy default config. Let's check where is the default config :

```
pkg info -l sway
```
It's under /usr/local/etc/sway/config.sample. Let's copy it to ~/.config/sway/

```
mkdir -p ~/.config/sway
cp /usr/local/etc/sway/config.sample ~/.config/sway/
```

Don't forget that by pressing TAB in the terminal it completes the path or lists possible subfolders if you are uncertain.

**make sway autostart at terminal 0**

```
vi .zshrc
```

At the bottom add

```
if [[ -z $DISPLAY ]] && [[ $(tty) = /dev/ttyv0 ]]; then
 exec sway
fi

```

Now after reboot and login sway will start automagically.

**turn off dedicated GPU ( if exists )**

Install and load acpi_call on startup

```
sudo pkg install acpi_call
sudo sysrc kld_list+= "acpi_call'
```

Download this script :

https://github.com/milgra/tutorials/blob/master/freebsd-dotfiles/Scripts/turn_off_gpu.sh

Edit .zshrc, execute it before exec sway

```
 sudo ~/Scripts/turn_off_gpu.sh
```

**stop keyboard beeping**

```
sudo sysrc allscreen_kbdflags="-b quiet.off"
```

**mount linux filesystems**

```
sudo sysrc kld_list+="ext2fs"
```

**auto mount connected drives**

```
sysrc autofs_enable="YES"
```

```
sudo vi /etc/auto_master
```

Uncomment media line.

**install rxvt for a non-cpu-demanding terminal experience**

```
sudo pkg install x11/rxvt-unicode
```

**install terminus-font for coolness***
```
sudo pkg install terminus-font
```

**make sway status bar show system information**

Download this script :

https://github.com/milgra/tutorials/blob/master/freebsd-dotfiles/Scripts/swaybar.sh

Add it to ~/.config/sway/config

```
...
bar {
    ...
    status_command while ~/Scripts/swaybar.sh; do sleep 5; done
...
```

**lock/sleep on shortcut**

Download this script :

https://github.com/milgra/tutorials/blob/master/freebsd-dotfiles/Scripts/swaylock.sh

Add shortcut to ~/.config/sway/config

```
bindsym $mod+p exec ~/Scripts/swaylock.sh
```

**start up desktop apps automatically on given workspaces after sway start**

Download this script :

https://github.com/milgra/tutorials/blob/master/freebsd-dotfiles/Scripts/swaystart.sh

Add exec to ~/.config/sway/config

```
exec ~/Scripts/swaystart.sh
```

**increase and decrease screen brightness on WIN + brightness control keys**

Download this script :

https://github.com/milgra/tutorials/blob/master/freebsd-dotfiles/Scripts/dec_brightness.sh
https://github.com/milgra/tutorials/blob/master/freebsd-dotfiles/Scripts/inc_brightness.sh

Add shortcuts to ~/.config/sway/config

```
bindsym $mod+F5 exec ~/Scripts/dec_brightness.sh
bindsym $mod+F6 exec ~/Scripts/inc_brightness.sh
```

**mute, increase and decrease audio volume on WIN + volume control keys**

Add shortcuts to ~/.config/sway/config

```
bindsym $mod+F1 exec mixer vol 0
bindsym $mod+F2 exec mixer vol -10
bindsym $mod+F3 exec mixer vol +10
```

**language switching on keyboard shortcut**

Add definition to ~/.config/sway/config

```
input * {
    xkb_layout "us,hu"
    xkb_variant ",101_qwerty_dot_nodead"
    xkb_options "grp:alt_space_toggle"
    repeat_rate 40
    repeat_delay 250
}
```

**touchpad natural scroll and double finger tap**

get your touchpad id with

```
swaymsg -t get_inputs
```

with your id add this to sway config
```
input "1739:52575:MSFT0001:00_06CB:CD5F_TouchPad" {
    dwt enabled
    tap enabled
    natural_scroll enabled
    middle_emulation enabled
    drag disabled
}
```

**setup sway font and colors**

Download my sway config and use the font/color configs from it.

https://github.com/milgra/tutorials/blob/master/freebsd-dotfiles/.config/sway

**setup zsh plugins and colors**

Install zsh autosuggest and syntax highlighting, downoad zsh-history-substring and copy to usr/local/share

```
sudo pkg install zsh-autosuggestions
sudo pkg install zsh-syntax-highlighting
```

Download my .zshrc and use it

https://github.com/milgra/tutorials/blob/master/freebsd-dotfiles/.zshrc

**enable site-specific browsing in firefox to be able to open sites as desktop apps**

Go to about:config url in firefox, search for ssb and enable it, then open firefox with -ssb parameter. If you use chromium then use --app switch/

**other office and media tools**

```
sudo pkg install libreoffice gimp vlc virtualbox-ose
```
**select wifi network**

```
sudo pkg install wifimgr
wifimgr
```

**things to be done**

make numlock work  
make resume work  
