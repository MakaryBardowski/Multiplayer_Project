package guiComponents.inventoryComponents;

import com.simsilica.lemur.*;
import guiComponents.LemurPlayerInventoryGui;

import static guiComponents.inventoryComponents.LemurUtils.getHeightRatio;
import static guiComponents.inventoryComponents.LemurUtils.getWidthRatio;

import guiComponents.dragDrop.DragAndDropControl;
import client.Main;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.simsilica.lemur.component.BoxLayout;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.event.MouseListener;
import lombok.Setter;

public class EquipmentPreviewDragToRotateListener implements MouseListener {
    private final LemurPlayerInventoryGui lemurPlayerEquipmentGui;
    private final Label previewLabel;
    private boolean rotatingLeft;
    private boolean rotatingRight;

    @Setter
    private boolean isPressed;

    public EquipmentPreviewDragToRotateListener(LemurPlayerInventoryGui lemurPlayerEquipmentGui, Label label) {
        this.lemurPlayerEquipmentGui = lemurPlayerEquipmentGui;
        this.previewLabel = label;
    }

    @Override
    public void mouseButtonEvent(MouseButtonEvent mbe, Spatial sptl, Spatial sptl1) {
        if (mbe.isPressed()) {
            isPressed = true;
        } else if (mbe.isReleased()) {
            isPressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {
    }

    @Override
    public void mouseExited(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {
        isPressed = false;
    }

    @Override
    public void mouseMoved(MouseMotionEvent mme, Spatial sptl, Spatial sptl1) {

        if (isPressed) {
            var previewLabelPosX = previewLabel.getWorldTranslation().getX();
            var relativeX = mme.getX() - previewLabelPosX;
            var previewLabelWidth = previewLabel.getPreferredSize().getX();

            if (relativeX < previewLabelWidth / 2) {
                rotatingLeft = true;
                rotatingRight = false;
            } else {
                rotatingRight = true;
                rotatingLeft = false;
            }


            if (rotatingLeft) {
                lemurPlayerEquipmentGui.getPlayer().getEquipmentPreviewCameraAnchorNode().rotate(0, 0.05f, 0);
            } else if (rotatingRight) {
                lemurPlayerEquipmentGui.getPlayer().getEquipmentPreviewCameraAnchorNode().rotate(0, -0.05f, 0);
            }
        }
    }
}

