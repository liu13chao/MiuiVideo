package com.miui.video.storage;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

public class StorageUtils {
    public static final String TAG = "StorageUtils";

    // following are private members, be careful if want to change to public
    private static final String DATA_SDCARD_ROOT = "/data/sdcard";
    private static final String INTERNAL_SDCARD_ROOT = "/storage/sdcard0";
    private static final String EXTERNAL_SDCARD_ROOT = "/storage/sdcard1";
    private static final String LEAF_DCIM_SCREENSHOTS_FILE_PATH = "/DCIM/Screenshots";
    private static final String LEAF_MIUI_FILE_PATH = "/MIUI";
    private static final String LEAF_DEMO_VIDEO_FILE_PATH = "/MIUI/Gallery/DemoVideo";
    private static final String LEAF_MILIAO_PAINTING_FILE_PATH = "/miliao/handwritings";
    private static final String LEAF_MILIAO_SAVED_FILE_PATH = "/miliao/saved";
    private static final String LEAF_MISHOP_SAVED_FILE_PATH = "/mishop/save";

    // should only be set once
    public static String LEAF_CAMERA_FILE_PATH;

    private static final String LEAF_MILIAO_FILE_PATH = "/miliao/images";
    public static final String DEFAULT_WALLPAPER_FILE_PATH = "/system/media/wallpaper";
    public static final String DEFAULT_LOCKSCREEN_FILE_PATH = "/system/media/lockscreen";

    private static final String CLOUD_FOLDER = "/MIUI/Gallery/cloud/";
    private static final String LEAF_CLOUD_MICRO_THUMBNAIL_FILE_PATH = CLOUD_FOLDER + ".microthumbnailFile";
    private static final String LEAF_CLOUD_THUMBNAIL_FILE_PATH = CLOUD_FOLDER + ".thumbnailFile";
    private static final String LEAF_CLOUD_DOWNLOAD_FILE_PATH = CLOUD_FOLDER + ".downloadFile";
    private static final String LEAF_CLOUD_WAIT_UPLOAD_FILE_PATH = CLOUD_FOLDER + ".waitUpload";
    private static final String LEAF_CLOUD_AVATAR_FILE_PATH = CLOUD_FOLDER + ".avatar";


    public static String getDataSDCardRoot() {
        return DATA_SDCARD_ROOT;
    }

    private static String getInternalSDCardRoot() {
        return INTERNAL_SDCARD_ROOT;
    }

    private static String getExternalSDCardRoot() {
        return EXTERNAL_SDCARD_ROOT;
    }

    // main SD card is either sdcard0 or sdcard1
    // depending on system
    public static String getMainSDCardRoot() {
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String getValidMIUIFilePath() {
        return hasMainSDCard()
                ? getMainSDCardRoot() + LEAF_MIUI_FILE_PATH
                : getDataSDCardRoot() + LEAF_MIUI_FILE_PATH;
    }

    public static ArrayList<String> getNonDataSDCardFilePath(String leafFolder) {
        ArrayList<String> result = new ArrayList<String>();
        //TODO tfling
        /*
        if (Build.IS_HONGMI) {
            result.add(getInternalSDCardRoot() + leafFolder);
            result.add(getExternalSDCardRoot() + leafFolder);
        } else {
            result.add(getMainSDCardRoot() + leafFolder);
        }
        */
        result.add(getMainSDCardRoot() + leafFolder);
        return result;
    }

    public static String getDataSDCardFilePath(String leafFolder) {
        return getDataSDCardRoot() + leafFolder;
    }

    public static ArrayList<String> getAllSDCardFilePath(String leafFolder) {
        ArrayList<String> result = new ArrayList<String>();
        //TODO tfling
        /*
        if (Build.IS_HONGMI) {
            result.add(getDataSDCardRoot() + leafFolder);
            result.add(getInternalSDCardRoot() + leafFolder);
            result.add(getExternalSDCardRoot() + leafFolder);
        } else {
            result.add(getDataSDCardRoot() + leafFolder);
            result.add(getMainSDCardRoot() + leafFolder);
        }
        */
        result.add(getDataSDCardRoot() + leafFolder);
        result.add(getMainSDCardRoot() + leafFolder);
        return result;
    }

    private static String getMainSDCardFilePath(String leafFolder) {
        return getMainSDCardRoot() + leafFolder;
    }

    private static String getInternalDataSDCardFilePath(String leafFolder) {
        return getInternalSDCardRoot() + leafFolder;
    }

    private static String getExternalDataSDCardFilePath(String leafFolder) {
        return getExternalSDCardRoot() + leafFolder;
    }

    public static String getMainSDCardCameraFilePath() {
        return getMainSDCardFilePath(LEAF_CAMERA_FILE_PATH);
    }

    public static ArrayList<String> getAllSDCardCameraFilePath() {
        return getAllSDCardFilePath(LEAF_CAMERA_FILE_PATH);
    }

    public static String getDataSDCardCameraFilePath() {
        return getDataSDCardFilePath(LEAF_CAMERA_FILE_PATH);
    }

    // only use for MediaSetUtils, others should use getMainSDCardCameraFilePath()
    public static String getInternalSDCardCameraFilePath() {
        return getInternalDataSDCardFilePath(LEAF_CAMERA_FILE_PATH);
    }

    // only use for MediaSetUtils, others should use getMainSDCardCameraFilePath()
    public static String getExternalSDCardCameraFilePath() {
        return getExternalDataSDCardFilePath(LEAF_CAMERA_FILE_PATH);
    }

    public static ArrayList<String> getAllSDCardScreenshotsFilePath() {
        return getAllSDCardFilePath(LEAF_DCIM_SCREENSHOTS_FILE_PATH);
    }

    public static String getMainSDCardScreenshotsFilePath() {
        return getMainSDCardFilePath(LEAF_DCIM_SCREENSHOTS_FILE_PATH);
    }

    public static String getDataSDCardScreenshotsFilePath() {
        return getDataSDCardFilePath(LEAF_DCIM_SCREENSHOTS_FILE_PATH);
    }

    // only use for MediaSetUtils, others should use getMainSDCardScreenshotsFilePath()
    public static String getInternalSDCardScreenshotsFilePath() {
        return getInternalDataSDCardFilePath(LEAF_DCIM_SCREENSHOTS_FILE_PATH);
    }

    // only use for MediaSetUtils, others should use getMainSDCardScreenshotsFilePath()
    public static String getExternalSDCardScreenshotsFilePath() {
        return getExternalDataSDCardFilePath(LEAF_DCIM_SCREENSHOTS_FILE_PATH);
    }

    public static ArrayList<String> getNonDataSDCardCameraFilePath() {
        return getNonDataSDCardFilePath(LEAF_CAMERA_FILE_PATH);
    }

    public static ArrayList<String> getNonDataSDCardScreenshotsFilePath() {
        return getNonDataSDCardFilePath(LEAF_DCIM_SCREENSHOTS_FILE_PATH);
    }

    // demo video is put in internal SD Card when flash factory ROM
    public static String getDemoVideoFilePath() {
        return getInternalSDCardRoot() + LEAF_DEMO_VIDEO_FILE_PATH;
    }

    public static ArrayList<String> getMiliaoPaintingFilePath() {
        return getNonDataSDCardFilePath(LEAF_MILIAO_PAINTING_FILE_PATH);
    }

    public static ArrayList<String> getMiliaoSavedFilePath() {
        return getNonDataSDCardFilePath(LEAF_MILIAO_SAVED_FILE_PATH);
    }

    public static ArrayList<String> getMiliaoFilePath() {
        return getNonDataSDCardFilePath(LEAF_MILIAO_FILE_PATH);
    }

    public static ArrayList<String> getMishopSavedFilePath() {
        return getNonDataSDCardFilePath(LEAF_MISHOP_SAVED_FILE_PATH);
    }

    public static ArrayList<String> getCloudFilePath() {
        return getNonDataSDCardFilePath(CLOUD_FOLDER);
    }

    // invoked when write avatar data
    public static String getMainSDCardCloudAvatarFilePath() {
        return getMainSDCardFilePath(LEAF_CLOUD_AVATAR_FILE_PATH);
    }

    // invoked when delete avatar data
    public static ArrayList<String> getCloudAvatarFilePath() {
        return getNonDataSDCardFilePath(LEAF_CLOUD_AVATAR_FILE_PATH);
    }

    public static ArrayList<String> getCloudMicroThumbnailFilePath() {
        return getNonDataSDCardFilePath(LEAF_CLOUD_MICRO_THUMBNAIL_FILE_PATH);
    }

    public static ArrayList<String> getCloudThumbnailFilePath() {
        return getNonDataSDCardFilePath(LEAF_CLOUD_THUMBNAIL_FILE_PATH);
    }

    public static ArrayList<String> getCloudDownloadFilePath() {
        return getNonDataSDCardFilePath(LEAF_CLOUD_DOWNLOAD_FILE_PATH);
    }

    public static ArrayList<String> getCloudWaitUploadFilePath() {
        return getNonDataSDCardFilePath(LEAF_CLOUD_WAIT_UPLOAD_FILE_PATH);
    }

    public static String getMainSDCardCloudFilePath() {
        return getMainSDCardFilePath(CLOUD_FOLDER);
    }

    public static String getMainSDCardCloudMicroThumbnailFilePath() {
        return getMainSDCardFilePath(LEAF_CLOUD_MICRO_THUMBNAIL_FILE_PATH);
    }

    public static String getMainSDCardCloudThumbnailFilePath() {
        return getMainSDCardFilePath(LEAF_CLOUD_THUMBNAIL_FILE_PATH);
    }

    public static String getMainSDCardCloudDownloadFilePath() {
        return getMainSDCardFilePath(LEAF_CLOUD_DOWNLOAD_FILE_PATH);
    }

    public static String getMainSDCardCloudWaitUploadFilePath() {
        return getMainSDCardFilePath(LEAF_CLOUD_WAIT_UPLOAD_FILE_PATH);
    }

    public static boolean hasMainSDCard() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            boolean writable = checkFsWritable();
//            return writable;
        	return true;
        }
        return false;
    }

    private static ArrayList<String> getAllSpecialFilePath() {
        ArrayList<String> result = new ArrayList<String>();
        result.addAll(getAllSDCardCameraFilePath());
        result.addAll(getAllSDCardScreenshotsFilePath());
        return result;
    }

    // special folders. E.g. camera, screenshots
    public static boolean isSpecialFilePathForDelete(String filePath) {
        ArrayList<String> paths = getAllSpecialFilePath();
        for (String onePath : paths) {
            if (onePath.equalsIgnoreCase(filePath)) {
                return true;
            }
        }
        return false;
    }

//    // special path. E.g. camera, screenshots album
//    public static boolean isSpecialPathForDelete(Path path) {
//        int pathSuffix = 0;
//        try {
//            // should not use getMediaSetBucketIdByPath()
//            pathSuffix = Integer.parseInt(path.getSuffix());
//        } catch (Exception e) {
////            Log.d(TAG, "isSpecialPathForDelete() fail to parse path suffix", e);
//            return false;
//        }
//        return MediaSetUtils.isSpecialAlbumBucketId(pathSuffix);
//    }

//    private static boolean checkFsWritable(String path) {
//        // Create a temporary file to see whether a volume is really writeable.
//        // It's important not to put it in the root directory which may have a
//        // limit on the number of files.
//        String directoryName = Environment.getExternalStorageDirectory().toString() + "/DCIM";
//        File directory = new File(directoryName);
//        if (!directory.isDirectory()) {
//            if (!GalleryUtils.createFolder(directory, false)) {
//                return false;
//            }
//        }
//        return directory.canWrite();
//    }

    public static boolean isFileExist(String path) {
        if (TextUtils.isEmpty(path)) return false;
        File file = new File(path);
        return file.exists() && file.isFile();
    }

    public static boolean isExist(String path) {
        if (TextUtils.isEmpty(path)) return false;
        File file = new File(path);
        return file.exists();
    }

    public static String getFolderPath(String path) {
        String folderPath = "";

        if (!TextUtils.isEmpty(path)) {
            int i = path.lastIndexOf('/');
            if (i > 0) {
                folderPath = path.substring(0, i);
            }
        }

        return folderPath;
    }

    public static String getFilename(String path) {
        return TextUtils.isEmpty(path) ? "" : path.substring(path.lastIndexOf('/')+1);
    }

    public static String getFilenameWithoutExt(String path) {
        String filename = "";

        if (!TextUtils.isEmpty(path)) {
            int indexOfSlash = path.lastIndexOf('/');
            int indexOfDot = path.lastIndexOf('.');
            if (indexOfDot <= indexOfSlash) {
                indexOfDot = path.length();
            }
            filename = path.substring(indexOfSlash + 1, indexOfDot);
        }

        return filename;
    }

    public static String getFileExt(String path) {
        String extension = "";

        if (!TextUtils.isEmpty(path)) {
            int indexOfSlash = path.lastIndexOf('/');
            int indexOfDot = path.lastIndexOf('.');
            if (indexOfDot > indexOfSlash) {
                extension = path.substring(indexOfDot + 1);
            }
        }

        return extension;
    }

    public static String getMimeType(String path) {
        String extension = StorageUtils.getFileExt(path).toLowerCase();
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
//        if(mimeType == null) {
//            mimeType = ImageMimeUtils.getMimeType(path);
//        }
        return mimeType != null ? mimeType : "*/*";
    }

    public static boolean isVideoFromFilePath(String path) {
        String mimeType = StorageUtils.getMimeType(path);
        return mimeType.startsWith("video");
    }

    public static boolean isImageFromFilePath(String path) {
        String mimeType = StorageUtils.getMimeType(path);
        return mimeType.startsWith("image");
    }

    public static boolean isDirFromFilePath(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return false;
        }
        return file.isDirectory();
    }

    public static boolean isInternal(String path) {
        return TextUtils.isEmpty(path)
                || path.startsWith(Environment.getRootDirectory() + "/media")
                || path.startsWith(getDataSDCardRoot());
    }

    public static boolean containsFilePath(ArrayList<String> filePaths, String filePath) {
        for (String onePath : filePaths) {
            if (onePath.equalsIgnoreCase(filePath)) {
                return true;
            }
        }
        return false;
    }

    public static boolean startsWithFilePath(ArrayList<String> filePaths, String filePath) {
        String lowerCaseFilePath = filePath.toLowerCase();
        for (String onePath : filePaths) {
            if (lowerCaseFilePath.startsWith(onePath.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public static String cutFilePathRoot(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        String root = "";
        // filePath only can starts with "/storage/sdcardX" or "/mnt/sdcard" or "/sdcard" or /storage/emulated/0
        if (filePath.startsWith(StorageUtils.getMainSDCardRoot())) {
            // filePath: "/storage/sdcard0/miliao/background/wh"
            // or: "/mnt/sdcard/miliao/background/wh"
            // we should cut "/storage/sdcard0" to find in blacklist,
            // such as "/miliao/background/wh"
            root = StorageUtils.getMainSDCardRoot();
        }
        else if (filePath.startsWith("/sdcard")) {
            // filePath: "/sdcard/miliao/background/wh"
            // we should cut "/sdcard" to find in blacklist,
            // such as "/miliao/background/wh"
            root = "/sdcard";
        }
        // return filePath to fit the case that the path is in black list
        // but it does not start with root path of any external storage,
        // such as "/system"
        return filePath.substring(root.length());
    }


//    public static String getLocalizedDisplayName(String filePath, String bucketName) {
//        int id = 0;
//
//        // Some paths' display names only can be determined at runtime,
//        // so we don't put them our whitelist.
//        if (containsFilePath(getNonDataSDCardCameraFilePath(), filePath)) {
//            id = R.string.camera_folder_display_name;
//        }
//        else if (containsFilePath(getNonDataSDCardScreenshotsFilePath(), filePath)) {
//            id = R.string.miui_capture_screen_folder_display_name;
//        }
//        else if (getDataSDCardCameraFilePath().equalsIgnoreCase(filePath)) {
//            id = R.string.internal_camera_folder_display_name;
//        }
//        else if (getDataSDCardScreenshotsFilePath().equalsIgnoreCase(filePath)) {
//            id = R.string.internal_miui_capture_screen_folder_display_name;
//        }
//        else {
//            String filePathWithoutRoot = cutFilePathRoot(filePath.toLowerCase());
//            Integer displayNameId = DefaultAlbums.whiteListDisplayNames.get(filePathWithoutRoot);
//            if (displayNameId != null) {
//                id = displayNameId;
//            }
//        }
//
//        return (id == 0) ? bucketName
//                : (String) GalleryAppImpl.sGetGalleryContext().getResources().getText(id);
//    }

}
