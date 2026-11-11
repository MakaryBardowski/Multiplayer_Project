package com.mccurry.game.gameplay.system.equipment.visual.firstperson.component;

import com.jme3.anim.SkinningControl;
import com.jme3.scene.Node;
import com.mccurry.game.gameplay.core.ecs.component.Component;

public class HandsViewmodelComponent implements Component {
    private final SkinningControl skinningControl;
    private final Node handsNode;

    public HandsViewmodelComponent(Node handsNode){
        this.skinningControl = handsNode.getChild("Armature").getControl(SkinningControl.class);
        this.handsNode = handsNode;
    }

    public Node getRootHandsNode(){
        return handsNode;
    }

    public Node getRightHandAttachmentNode(){
        return skinningControl.getAttachmentsNode("HandAttachmentBoneR");
    }

    public Node getLeftHandAttachmentNode(){
        return skinningControl.getAttachmentsNode("HandAttachmentBoneL");
    }
}
