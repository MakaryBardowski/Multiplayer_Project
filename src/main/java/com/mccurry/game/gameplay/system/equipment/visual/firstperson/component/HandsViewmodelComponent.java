package com.mccurry.game.gameplay.system.equipment.visual.firstperson.component;

import com.jme3.anim.SkinningControl;
import com.jme3.scene.Node;
import com.mccurry.game.gameplay.core.ecs.component.Component;

public class HandsViewmodelComponent implements Component {
    private final SkinningControl skinningControl;

    public HandsViewmodelComponent(Node humanoidRootNode){
        this.skinningControl = humanoidRootNode.getChild("Armature").getControl(SkinningControl.class);
    }

    public Node getRightHandAttachmentNode(){
        return skinningControl.getAttachmentsNode("HandAttachmentBoneR");
    }

    public Node getLeftHandAttachmentNode(){
        return skinningControl.getAttachmentsNode("HandAttachmentBoneL");
    }
}
