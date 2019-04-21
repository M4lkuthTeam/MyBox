package mara.mybox.controller;

import mara.mybox.controller.base.ImageManufactureBatchController;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import javafx.beans.binding.Bindings;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import static mara.mybox.value.AppVaribles.logger;
import mara.mybox.image.ImageConvert;
import static mara.mybox.value.AppVaribles.getMessage;
import mara.mybox.fxml.ImageManufacture;
import mara.mybox.fxml.FxmlControl;
import static mara.mybox.fxml.FxmlControl.badStyle;
import mara.mybox.value.AppVaribles;

/**
 * @Author Mara
 * @CreateDate 2018-9-25
 * @Description
 * @License Apache License Version 2.0
 */
public class ImageManufactureBatchArcController extends ImageManufactureBatchController {

    private final String ImageArcKey, ImageArcPerKey;
    private int arc, percent;
    private boolean isPercent;

    @FXML
    private ColorPicker arcColorPicker;
    @FXML
    private Button transForArcButton;
    @FXML
    private ComboBox<String> arcBox, perBox;
    @FXML
    private ToggleGroup arcGroup;

    public ImageManufactureBatchArcController() {
        baseTitle = AppVaribles.getMessage("ImageManufactureBatchArc");

        ImageArcKey = "ImageArcKey";
        ImageArcPerKey = "ImageArcPerKey";
    }

    @Override
    public void initializeNext2() {
        try {
            operationBarController.startButton.disableProperty().unbind();
            operationBarController.startButton.disableProperty().bind(Bindings.isEmpty(targetPathInput.textProperty())
                    .or(targetPathInput.styleProperty().isEqualTo(badStyle))
                    .or(Bindings.isEmpty(filesTableController.filesTableView.getItems()))
                    .or(arcBox.getEditor().styleProperty().isEqualTo(badStyle))
                    .or(perBox.getEditor().styleProperty().isEqualTo(badStyle))
            );

        } catch (Exception e) {
            logger.debug(e.toString());
        }
    }

    @Override
    public void initOptionsSection() {
        try {
            super.initOptionsSection();

            arcBox.getItems().addAll(Arrays.asList("15", "30", "50", "150", "300", "10", "3"));
            arcBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    checkArc();
                }
            });
            arcBox.getSelectionModel().select(0);

            Tooltip tips = new Tooltip("1~100");
            tips.setFont(new Font(16));
            FxmlControl.quickTooltip(perBox, tips);

            perBox.getItems().addAll(Arrays.asList("15", "25", "30", "10", "12", "8"));
            perBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue ov, String oldValue, String newValue) {
                    checkPercent();
                }
            });
            perBox.getSelectionModel().select(0);

            arcGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
                @Override
                public void changed(ObservableValue<? extends Toggle> ov,
                        Toggle old_toggle, Toggle new_toggle) {
                    checkType();
                }
            });
            checkType();

            arcColorPicker.setValue(Color.TRANSPARENT);

        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    private void checkType() {
        arcBox.setDisable(true);
        perBox.setDisable(true);
        arcBox.getEditor().setStyle(null);
        perBox.getEditor().setStyle(null);

        RadioButton selected = (RadioButton) arcGroup.getSelectedToggle();
        if (getMessage("WidthPercentage").equals(selected.getText())) {
            isPercent = true;
            perBox.setDisable(false);
            checkPercent();

        } else if (getMessage("Custom").equals(selected.getText())) {
            isPercent = false;
            arcBox.setDisable(false);
            checkArc();

        }
    }

    private void checkPercent() {
        try {
            percent = Integer.valueOf(perBox.getValue());
            if (percent > 0 && percent <= 100) {
                FxmlControl.setEditorNormal(perBox);
            } else {
                percent = 15;
                FxmlControl.setEditorBadStyle(perBox);
            }
        } catch (Exception e) {
            logger.debug(e.toString());
            percent = 15;
            FxmlControl.setEditorBadStyle(perBox);
        }
    }

    private void checkArc() {
        try {
            arc = Integer.valueOf(arcBox.getValue());
            if (arc >= 0) {
                FxmlControl.setEditorNormal(arcBox);
            } else {
                arc = 0;
                FxmlControl.setEditorBadStyle(arcBox);
            }
        } catch (Exception e) {
            logger.debug(e.toString());
            arc = 0;
            FxmlControl.setEditorBadStyle(arcBox);
        }
    }

    @FXML
    public void arcTransparentAction() {
        arcColorPicker.setValue(Color.TRANSPARENT);
    }

    @FXML
    public void arcWhiteAction() {
        arcColorPicker.setValue(Color.WHITE);
    }

    @FXML
    public void arcBlackAction() {
        arcColorPicker.setValue(Color.BLACK);
    }

    @Override
    protected BufferedImage handleImage(BufferedImage source) {
        try {
            int value = arc;
            if (isPercent) {
                value = source.getWidth() * percent / 100;
            }
            BufferedImage target = ImageConvert.addArc(source, value,
                    ImageManufacture.toAwtColor(arcColorPicker.getValue()));
            return target;
        } catch (Exception e) {
            logger.error(e.toString());
            return null;
        }

    }

}
