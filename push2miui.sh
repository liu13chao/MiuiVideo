rm ~/xiaomi/dev/miui/x4-dev/packages/apps/MiuiVideo/phone/ -r
rm ~/xiaomi/dev/miui/x4-dev/packages/apps/MiuiVideo/common/ -r
cp common ~/xiaomi/dev/miui/x4-dev/packages/apps/MiuiVideo -r
cp phone ~/xiaomi/dev/miui/x4-dev/packages/apps/MiuiVideo -r
cd ~/xiaomi/dev/miui/x4-dev/packages/apps/MiuiVideo
cp phone/video/AndroidManifest.xml phone/
rm common/jni/ -r
rm common/libs/MiLink.jar
rm common/libs/miuisdk.jar
rm common/libs/miuisdkstatic.jar
rm phone/video/so/ -r
rm phone/videoplayer/libs/airkan.jar
cp common/res/values-sw360dp/* common/res/values/
mv common/res/values-sw360dp common/res/values-xxhdpi
mv common/res/values-sw392dp/ common/res/values-nxhdpi
cp phone/video/res/values-sw360dp/* phone/video/res/values/
mv phone/video/res/values-sw360dp phone/video/res/values-xxhdpi
mv phone/video/res/values-sw392dp/ phone/video/res/values-nxhdpi
cp phone/videoplayer/res/values-sw360dp/* phone/videoplayer/res/values/
mv phone/videoplayer/res/values-sw360dp phone/videoplayer/res/values-xxhdpi
mv phone/videoplayer/res/values-sw392dp/ phone/videoplayer/res/values-nxhdpi
mv phone/video/drawable-nxhdpi phone/video/res/
#mkdir phone/video/res/drawable-nxhdpi
