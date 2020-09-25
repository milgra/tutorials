uptime_formatted=$(uptime | cut -c14-17)

date_formatted=$(date "+%a %F %H:%M")

battery_status=$(apm | grep -m1 life: | cut -c25-27)

ssid=$(ifconfig wlan0 | grep ssid | cut -c7-15)

free_space=$(df -H | grep -vE '^Filesystem|tmpfs|cdrom' | awk '{ print $4 }' | head -1)

volume=$(mixer vol | cut -c37-38)

cpu_load=$(top -b -n 0 | grep CPU | cut -c6-10)

mem_load=$(top | grep Mem: | awk '{print $12}')

rem_time=$(apm -t)
rem_time_hr=$(expr $rem_time / 3600)
rem_time_rnd=$(expr $rem_time_hr \* 3600)
rem_time_min=$(expr $rem_time % $rem_time_rnd / 60)

backlight=$(sysctl sys.class.backlight.intel_backlight.bl_device.brightness | awk '{ print $2 }')
backratio=$(expr $backlight \* 100 / 26666)

layout=$(swaymsg -pt get_inputs | grep -m1 "Active Keyboard" | cut -c 27-28)

echo "cpu" $cpu_load "|"\
     "mem" $mem_load "|"\
     "ssd" $free_space "|"\
     "upt" $uptime_formatted "|"\
     "bat" $battery_status "rem" $rem_time_hr $rem_time_min "|"\
     "lcd" $backratio"% |"\
     "vol" $volume"% |"\
     "wifi" $ssid "|"\
      $layout "|"\
      $date_formatted " "
