LEVEL=$(sysctl sys.class.backlight.intel_backlight.bl_device.brightness | awk '{ print $2}')
FINAL=$(expr $LEVEL + 1000)
sudo sysctl sys.class.backlight.intel_backlight.bl_device.brightness=$FINAL
