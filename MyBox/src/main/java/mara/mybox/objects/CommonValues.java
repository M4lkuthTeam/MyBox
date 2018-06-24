package mara.mybox.objects;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.TimeZone;
import javafx.scene.image.Image;

/**
 * @Author Mara
 * @CreateDate 2018-6-4 16:04:05
 *
 * @Description
 * @License Apache License Version 2.0
 */
public class CommonValues {

    public static final double AppVersion = 1.5;
    public static final String AppVersionDate = "2018-06-24";

    public static final String UserFilePath = System.getProperty("user.home") + "/mybox";
    public static final String UserConfigFile = UserFilePath + "/.conf.properties";

    public static final Image AppIcon = new Image("img/mybox.png");

    public static final String MyboxFxml = "/fxml/MyBox.fxml";
    public static final String PdfExtractImagesFxml = "/fxml/PdfExtractImages.fxml";
    public static final String PdfExtractImagesBatchFxml = "/fxml/PdfExtractImagesBatch.fxml";
    public static final String PdfConvertImagesFxml = "/fxml/PdfConvertImages.fxml";
    public static final String PdfConvertImagesBatchFxml = "/fxml/PdfConvertImagesBatch.fxml";
    public static final String PdfInformationFxml = "/fxml/PdfInformation.fxml";
    public static final String FileFxml = "/fxml/PdfConvertImages.fxml";
    public static final String AboutFxml = "/fxml/About.fxml";
    public static final String LoadingFxml = "/fxml/Loading.fxml";
    public static final String ImageInformationFxml = "/fxml/ImageInformation.fxml";
    public static final String ImageViewerFxml = "/fxml/ImageViewer.fxml";
    public static final String ImageMetaDataFxml = "/fxml/ImageMetaData.fxml";

    public static final Locale LocaleZhCN = new Locale("zh", "CN");
    public static final Locale LocaleEnUS = new Locale("en", "US");
    public static final Locale LocaleFrFR = new Locale("fr", "FR");
    public static final Locale LocaleEsES = new Locale("es", "ES");
    public static final Locale LocaleRuRU = new Locale("ru", "RU");

    public static final ResourceBundle BundleZhCN = ResourceBundle.getBundle("bundles/Messages", LocaleZhCN);
    public static final ResourceBundle BundleEnUS = ResourceBundle.getBundle("bundles/Messages", LocaleEnUS);
    public static final ResourceBundle BundleFrFR = ResourceBundle.getBundle("bundles/Messages", LocaleFrFR);
    public static final ResourceBundle BundleEsES = ResourceBundle.getBundle("bundles/Messages", LocaleEsES);
    public static final ResourceBundle BundleRuRU = ResourceBundle.getBundle("bundles/Messages", LocaleRuRU);
    public static final ResourceBundle BundleDefault = ResourceBundle.getBundle("bundles/Messages", Locale.getDefault());

    public static final TimeZone zoneUTC = TimeZone.getTimeZone("GMT+0"); // 世界标准时,UTC
    public static final TimeZone zoneZhCN = TimeZone.getTimeZone("GMT+8"); // 北京时区,东八区 UTC+0800
    public static final String DatetimeFormat = "yyyy-MM-dd HH:mm:ss";
    public static final String DatetimeFormat2 = "yyyy-MM-dd HH:mm:ss.SSS";
    public static final String DatetimeFormat3 = "yyyy-MM-dd-HH-mm-ss-SSS";

    public static final int InvalidValue = -9999999;

}