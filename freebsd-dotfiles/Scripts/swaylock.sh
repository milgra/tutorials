# put task in background
swayidle \
    timeout 1 'swaymsg "output * dpms off"' \
    resume 'swaymsg "output * dpms on"' &
# lock screen
swaylock -c000000
# kill last background task
kill %%
