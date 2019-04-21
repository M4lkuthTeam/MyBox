package mara.mybox.fxml;

import java.awt.Desktop;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import mara.mybox.controller.base.BaseController;
import mara.mybox.controller.BytesEditerController;
import mara.mybox.controller.HtmlEditorController;
import mara.mybox.controller.ImageInformationController;
import mara.mybox.controller.base.ImageManufactureController;
import mara.mybox.controller.ImageMetaDataController;
import mara.mybox.controller.ImageStatisticController;
import mara.mybox.controller.ImageViewerController;
import mara.mybox.controller.ImagesBrowserController;
import mara.mybox.controller.PdfViewController;
import mara.mybox.controller.TextEditerController;
import mara.mybox.value.AppVaribles;
import mara.mybox.value.CommonValues;
import mara.mybox.data.ImageInformation;
import mara.mybox.data.VisitHistory;
import mara.mybox.tools.FileTools;
import static mara.mybox.value.AppVaribles.logger;

/**
 * @Author Mara
 * @CreateDate 2019-1-27 21:48:55
 * @Version 1.0
 * @Description
 * @License Apache License Version 2.0
 */
public class FxmlStage {

    public static BaseController initScene(Class theClass, final Stage stage, String newFxml) {
        try {
            if (stage == null) {
                return null;
            }
            FXMLLoader fxmlLoader = new FXMLLoader(theClass.getResource(newFxml), AppVaribles.currentBundle);
            Pane pane = fxmlLoader.load();
            pane.getStylesheets().add(theClass.getResource(AppVaribles.getStyle()).toExternalForm());
            Scene scene = new Scene(pane);

            final BaseController controller = (BaseController) fxmlLoader.getController();
            controller.myStage = stage;
            controller.myScene = scene;
            controller.loadFxml = newFxml;

            if (controller.getBaseTitle() == null) {
                stage.setTitle(AppVaribles.getMessage("AppTitle"));
            } else {
                stage.setTitle(controller.getBaseTitle());
            }
            stage.setTitle(controller.getBaseTitle());
            VisitHistory.visitMenu(controller.getBaseTitle(), newFxml);

            stage.getIcons().add(CommonValues.AppIcon);

            stage.setOnShown(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    controller.afterStageShown();
                }
            });
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    if (!controller.leavingScene()) {
                        event.consume();
                    } else {
                        FxmlStage.closeStage(stage);
                    }
                }
            });

            Scene s = stage.getScene();
            s = null;
            stage.setScene(scene);
            stage.show();
            stage.requestFocus();

            controller.initStage();

            AppVaribles.stageOpened(stage, controller);

            Platform.setImplicitExit(AppVaribles.scheduledTasks == null || AppVaribles.scheduledTasks.isEmpty());

            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static BaseController openStage(Class theClass, Stage myStage,
            String newFxml, boolean isOwned, Modality modality) {
        try {
            Stage stage = new Stage();
            stage.initModality(modality);
            if (isOwned && myStage != null) {
                stage.initOwner(myStage);
            } else {
                stage.initOwner(null);
            }
            return initScene(theClass, stage, newFxml);
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static BaseController openStage(Class theClass, Stage myStage,
            String newFxml, boolean isOwned) {
        return openStage(theClass, myStage, newFxml, isOwned, Modality.NONE);
    }

    public static BaseController openStage(Class theClass, Stage myStage,
            String newFxml) {
        return openStage(theClass, myStage, newFxml, false, Modality.NONE);
    }

    public static BaseController openScene(Class theClass, Stage stage, String newFxml) {
        try {
            if (stage == null) {
                stage = new Stage();
                stage.initModality(Modality.NONE);
                stage.initStyle(StageStyle.DECORATED);
                stage.initOwner(null);
            }
            return initScene(theClass, stage, newFxml);
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static void closeStage(Stage stage) {
        AppVaribles.stageClosed(stage);
        stage.close();
        if (AppVaribles.openedStages.isEmpty()) {
            FxmlStage.appExit();
        }
    }

    public static void appExit() {
        try {
            final List<BaseController> controllers = new ArrayList();
            controllers.addAll(AppVaribles.openedStages.values());
            for (BaseController controller : controllers) {
                if (controller != null && !controller.closeStage()) {
                    return;
                }
            }
            if (AppVaribles.scheduledTasks != null && !AppVaribles.scheduledTasks.isEmpty()) {
                if (AppVaribles.getUserConfigBoolean("StopAlarmsWhenExit")) {
                    for (Long key : AppVaribles.scheduledTasks.keySet()) {
                        ScheduledFuture future = AppVaribles.scheduledTasks.get(key);
                        future.cancel(true);
                    }
                    AppVaribles.scheduledTasks = null;
                    if (AppVaribles.executorService != null) {
                        AppVaribles.executorService.shutdownNow();
                        AppVaribles.executorService = null;
                    }
                }

            } else {
                if (AppVaribles.scheduledTasks != null) {
                    AppVaribles.scheduledTasks = null;
                }
                if (AppVaribles.executorService != null) {
                    AppVaribles.executorService.shutdownNow();
                    AppVaribles.executorService = null;
                }
            }

            if (AppVaribles.scheduledTasks == null || AppVaribles.scheduledTasks.isEmpty()) {
                Platform.exit();
            }

        } catch (Exception e) {
            logger.debug(e.toString());
        }

    }

    public static BaseController openMyBox(Class theClass, Stage stage) {
        return openScene(theClass, stage, CommonValues.MyboxFxml);
    }

    public static PdfViewController openPdfViewer(Class theClass, Stage stage, File file) {
        try {
            final PdfViewController controller
                    = (PdfViewController) openScene(theClass, stage, CommonValues.PdfViewFxml);
            controller.sourceFileChanged(file);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static HtmlEditorController openHtmlEditor(Class theClass, Stage stage, File file) {
        try {
            final HtmlEditorController controller
                    = (HtmlEditorController) openScene(theClass, stage, CommonValues.HtmlEditorFxml);
            controller.switchBroswerTab();
            controller.loadHtml(file);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImageManufactureController openImageManufacture(Class theClass, Stage stage, File file) {
        try {
            final ImageManufactureController controller
                    = (ImageManufactureController) openScene(theClass, stage, CommonValues.ImageManufactureFileFxml);
            controller.loadImage(file.getAbsolutePath());
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImageManufactureController openImageManufacture(Class theClass, Stage stage,
            File file, Image image, ImageInformation imageInfo) {
        try {
            final ImageManufactureController controller
                    = (ImageManufactureController) openScene(theClass, stage, CommonValues.ImageManufactureFileFxml);
            controller.loadImage(file, image, imageInfo);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImageManufactureController openImageManufacture(Class theClass, Stage stage,
            ImageInformation imageInfo) {
        try {
            final ImageManufactureController controller
                    = (ImageManufactureController) openScene(theClass, stage, CommonValues.ImageManufactureFileFxml);
            controller.loadImage(imageInfo);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImageManufactureController openImageManufacture(Class theClass, Stage stage,
            Image image) {
        try {
            final ImageManufactureController controller
                    = (ImageManufactureController) openScene(theClass, stage, CommonValues.ImageManufactureFileFxml);
            controller.loadImage(image);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImageViewerController openImageViewer(Class theClass, Stage stage) {
        try {
            final ImageViewerController controller
                    = (ImageViewerController) openScene(theClass, stage, CommonValues.ImageViewerFxml);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImageViewerController openImageViewer(Class theClass, Stage stage, File file) {
        try {
            final ImageViewerController controller = openImageViewer(theClass, stage);
            if (controller != null) {
                controller.loadImage(file.getAbsolutePath());
            }
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImagesBrowserController openImagesBrowser(Class theClass, Stage stage) {
        try {
            final ImagesBrowserController controller = (ImagesBrowserController) openScene(theClass, stage,
                    CommonValues.ImagesBrowserFxml);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static TextEditerController openTextEditer(Class theClass, Stage stage, File file) {
        try {
            final TextEditerController controller
                    = (TextEditerController) openScene(theClass, stage, CommonValues.TextEditerFxml);
            controller.openFile(file);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static BytesEditerController openBytesEditer(Class theClass, Stage stage, File file) {
        try {
            final BytesEditerController controller
                    = (BytesEditerController) openScene(theClass, stage, CommonValues.BytesEditerFxml);
            controller.openFile(file);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImageInformationController openImageInformation(Class theClass, Stage stage, ImageInformation info) {
        try {
            final ImageInformationController controller = (ImageInformationController) openScene(theClass, stage,
                    CommonValues.ImageInformationFxml);
            controller.loadInformation(info);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImageMetaDataController openImageMetaData(Class theClass, Stage stage, ImageInformation info) {
        try {
            final ImageMetaDataController controller = (ImageMetaDataController) openScene(theClass, stage,
                    CommonValues.ImageMetaDataFxml);
            controller.loadData(info);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImageStatisticController openImageStatistic(Class theClass, Stage stage, ImageInformation info) {
        try {
            final ImageStatisticController controller = (ImageStatisticController) openScene(theClass, stage,
                    CommonValues.ImageStatisticFxml);
            controller.loadImage(info);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static ImageStatisticController openImageStatistic(Class theClass, Stage stage, Image image) {
        try {
            final ImageStatisticController controller = (ImageStatisticController) openScene(theClass, stage,
                    CommonValues.ImageStatisticFxml);
            controller.loadImage(image);
            return controller;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }
    }

    public static BaseController openTarget(Class theClass, Stage stage,
            String filename) {
        return openTarget(theClass, stage, filename, true);
    }

    public static BaseController openTarget(Class theClass, Stage stage,
            String filename, boolean mustOpen) {
        BaseController controller = null;
        if (filename == null) {
            return controller;
        }
        File file = new File(filename);
        if (!file.exists()) {
            return controller;
        }
        if (file.isDirectory()) {
            try {
                Desktop.getDesktop().browse(file.toURI());

            } catch (Exception e) {

            }
            return controller;
        }

        String suffix = FileTools.getFileSuffix(file.getAbsolutePath()).toLowerCase();
        if (CommonValues.SupportedImages.contains(suffix)) {
            controller = openImageViewer(theClass, stage, file);

        } else if ("html".equals(suffix) || "htm".equals(suffix)) {
            controller = openHtmlEditor(theClass, stage, file);

        } else if (Arrays.asList(CommonValues.TextFileSuffix).contains(suffix)) {
            controller = openTextEditer(theClass, stage, file);

        } else if ("pdf".equals(suffix)) {
            controller = openPdfViewer(theClass, stage, file);

        } else if (mustOpen) {
            controller = openBytesEditer(theClass, stage, file);
            //            try {
//                Desktop.getDesktop().browse(file.toURI());
//            } catch (Exception e) {
//            }
        }
        return controller;
    }

    public static void alertError(Stage myStage, String information) {
        try {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(myStage.getTitle());
            alert.setHeaderText(null);
            alert.setContentText(information);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public static void alertWarning(Stage myStage, String information) {
        try {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle(myStage.getTitle());
            alert.setHeaderText(null);
            alert.setContentText(information);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.showAndWait();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    public static void alertInformation(Stage myStage, String information) {
        try {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(myStage.getTitle());
            alert.setHeaderText(null);
            alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            alert.getDialogPane().setContent(new Label(information));
//            alert.setContentText(information);

            alert.showAndWait();
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

}
