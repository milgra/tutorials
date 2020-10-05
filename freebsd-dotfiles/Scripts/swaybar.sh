# top related

cpu_load=$(top -b -n 0 -d 2 -s 1 | grep CPU | cut -c6-10)
cpu_load_formatted=$(echo $cpu_load | cut -c5-9)
mem_load=$(top | grep Mem: | awk '{print $12}')

# apm related

battery_status=$(apm | grep -m1 life: | cut -c25-27)
rem_time=$(apm | grep -m1 "Remaining battery time" | cut -c26-29)

# free space
free_space=$(df -H | grep -vE '^Filesystem|tmpfs|cdrom' | awk '{ print $4 }' | head -1)

# time

uptime_formatted=$(uptime | cut -c14-17)
date_formatted=$(date "+%a %F %H:%M")

# wifi ssid name

ssid=$(ifconfig wlan0 | grep ssid | awk '{print $2}' | cut -c 1-)

# audio volume

volume=$(mixer vol | cut -c36-38)

# display brightness

backlight=$(sysctl sys.class.backlight.intel_backlight.bl_device.brightness | awk '{ print $2 }')
backratio=$(expr $backlight \* 100 / 26666)

# keyboard layout

layout=$(swaymsg -pt get_inputs | grep -m1 "Active Keyboard" | cut -c 27-28)

echo "cpu" $cpu_load_formatted "|"\
     "mem" $mem_load "|"\
     "ssd" $free_space "|"\
     "upt" $uptime_formatted "|"\
     "bat" $battery_status $rem_time "|"\
     "lcd" $backratio"% |"\
     "vol" $volume"% |"\
     "wifi" $ssid "|"\
      $layout "|"\
      $date_formatted" "
